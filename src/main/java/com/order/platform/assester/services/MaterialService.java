package com.order.platform.assester.services;

import com.order.platform.assester.dto.MaterialFileDTO;
import com.order.platform.assester.dto.MaterialFormDTO;
import com.order.platform.assester.dto.MaterialSummaryDTO;
import com.order.platform.assester.entities.Material;
import com.order.platform.assester.entities.MaterialFile;
import com.order.platform.assester.enums.MaterialStatus;
import com.order.platform.assester.exceptions.material.InvalidMaterialException;
import com.order.platform.assester.exceptions.material.MaterialNotFoundException;
import com.order.platform.assester.exceptions.material.MaterialStorageException;
import com.order.platform.assester.exceptions.user.UserNotFoundException;
import com.order.platform.assester.mapper.MaterialFileMapper;
import com.order.platform.assester.mapper.MaterialMapper;
import com.order.platform.assester.repositories.MaterialRepository;
import com.order.platform.assester.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "ppt", "pptx", "odp", "doc", "docx", "odt", "txt", "png", "jpg", "jpeg");
    private static final int MAX_FILES = 5;

    private final MaterialMapper materialMapper;
    private final MaterialFileMapper materialFileMapper;

    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;

    @Value("${assester.material-storage-location:uploads/materials}")
    private String storageLocation;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(getStoragePath());
    }

    @Transactional(readOnly = true)
    public List<MaterialSummaryDTO> findPublishedByQuery(String query) {
        List<Material> materials = materialRepository.findAllPublishedByQuery(query.trim());

        return materials.stream().map(materialMapper::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public MaterialSummaryDTO findPublishedByCode(String code) {
        return materialRepository.findByMaterialCodeIgnoreCaseAndStatus(code.trim())
                .map(materialMapper::toSummary).orElse(null);
    }

    @Transactional(readOnly = true)
    public MaterialSummaryDTO findPublishedById(Long id) {
        return materialRepository.findPublishedById(id)
                .map(materialMapper::toSummary)
                .orElseThrow(() ->
                        new MaterialNotFoundException("Published material with id " +
                                id + " was not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<MaterialFileDTO> getPublishedFiles(Long materialId) {
        Material material = materialRepository
                .findPublishedById(materialId)
                .orElseThrow(() ->
                        new MaterialNotFoundException("Published material with id " +
                                materialId + " was not found"
                        )
                );
        return material.getFiles().stream().map(materialFileMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public Resource getPublishedFile(Long materialId, Long fileId) {
        Material material = materialRepository
                .findPublishedById(materialId)
                .orElseThrow(() ->
                        new MaterialNotFoundException("Published material with id " +
                                materialId + " was not found")
                );

        MaterialFile file = material.getFiles()
                .stream()
                .filter(mf -> mf.getId().equals(fileId))
                .findFirst()
                .orElseThrow(() -> new MaterialNotFoundException("File with id " + fileId + " was not found"));

        Path target = getStoragePath().resolve(file.getStorageKey()).normalize();
        if (!target.getParent().equals(getStoragePath().normalize())) {
            throw new MaterialNotFoundException("File with id " + fileId + " was not found");
        }

        Resource resource = new FileSystemResource(target);
        if (!resource.exists() || !resource.isReadable()) {
            log.warn(
                    "Stored material file is missing or unreadable, materialId={}, fileId={}",
                    materialId,
                    fileId
            );

            throw new MaterialNotFoundException("File with id " + fileId + " was not found");
        }
        return resource;
    }

    @Transactional(readOnly = true)
    public MaterialFileDTO getPublishedFileDetails(Long materialId, Long fileId) {
        Material material = materialRepository.findPublishedById(materialId)
                .orElseThrow(() ->
                        new MaterialNotFoundException("Published material with id "
                                + materialId + " was not found")
                );

        return material.getFiles()
                .stream()
                .filter(mf -> mf.getId().equals(fileId))
                .findFirst()
                .map(materialFileMapper::toDTO)
                .orElseThrow(() ->
                        new MaterialNotFoundException("File with id " +
                                fileId + " was not found")
                );
    }

    @Transactional(readOnly = true)
    public List<MaterialSummaryDTO> findMyMaterialAsSummaries(String email) {
        return materialRepository
                .findAllByAuthorEmailOrderByUpdatedAtDesc(email)
                .stream()
                .map(materialMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaterialSummaryDTO> findMyPublishedForSelection(String email) {
        return materialRepository
                .findAllAuthorPublishedMaterials(email)
                .stream()
                .map(materialMapper::toSummary)
                .toList();
    }

    @Transactional
    public void publish(MaterialFormDTO form, List<MultipartFile> uploads, String authorEmail) {
        if (uploads.isEmpty() || uploads.size() > MAX_FILES) {
            throw new InvalidMaterialException("Upload must be between 1 and 5 files");
        }

        List<Path> stored = new ArrayList<>();
        try {
            Material material = new Material();
            material.setTitle(form.getTitle().trim());
            material.setDescription(form.getDescription().trim());
            material.setAuthor(userRepository.findByEmail(authorEmail)
                    .orElseThrow(() ->
                            new UserNotFoundException("Author with email "
                                    + authorEmail + " was not found")
                    )
            );
            material.setStatus(MaterialStatus.PUBLISHED);

            for (MultipartFile upload : uploads) {
                String originalName = StringUtils.cleanPath(upload.getOriginalFilename() == null ?
                        "file" :
                        upload.getOriginalFilename()
                );

                if (originalName.contains("..") || originalName.length() > 255) {
                    throw new InvalidMaterialException("Invalid file name");
                }

                String extension = StringUtils.getFilenameExtension(originalName);
                if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                    throw new InvalidMaterialException("This file type is not allowed");
                }

                String key = UUID.randomUUID().toString();
                Path target = getStoragePath().resolve(key).normalize();

                stored.add(target);

                upload.transferTo(target);

                MaterialFile file = new MaterialFile();
                file.setMaterial(material);
                file.setOriginalName(originalName);
                file.setStorageKey(key);
                file.setContentType(upload.getContentType() == null ? "application/octet-stream" : upload.getContentType());
                file.setSizeBytes(upload.getSize());

                material.getFiles().add(file);
            }
            materialRepository.save(material);

            log.info(
                    "Material published successfully, filesCount={}",
                    stored.size()
            );
        } catch (IOException exception) {
            deleteFiles(stored);
            throw new MaterialStorageException("Unable to store uploaded files", exception);
        } catch (RuntimeException exception) {
            deleteFiles(stored);
            throw exception;
        }
    }

    private Path getStoragePath() {
        return Path.of(storageLocation).toAbsolutePath().normalize();
    }

    private void deleteFiles(List<Path> paths) {
        paths.forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.warn("Failed to delete file: {}", path, e);
            }
        });
    }

    @Transactional(readOnly = true)
    public List<MaterialSummaryDTO> findPublishedMaterials() {
        List<Material> materials = materialRepository
                .findAllByStatusOrderByUpdatedAtDesc(MaterialStatus.PUBLISHED);

        return materials.stream()
                .map(materialMapper::toSummary)
                .toList();
    }
}
