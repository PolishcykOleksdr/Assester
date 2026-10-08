package com.order.platform.assester.services;

import com.order.platform.assester.dto.MaterialFileDTO;
import com.order.platform.assester.dto.MaterialSummaryDTO;
import com.order.platform.assester.entities.Material;
import com.order.platform.assester.entities.MaterialFile;
import com.order.platform.assester.enums.MaterialStatus;
import com.order.platform.assester.repositories.MaterialRepository;
import com.order.platform.assester.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
public class MaterialService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "ppt", "pptx", "odp", "doc", "docx", "odt", "txt", "png", "jpg", "jpeg");
    private static final int MAX_FILES = 5;
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;

    @Value("${assester.material-storage-location:uploads/materials}")
    private String storageLocation;

    @Transactional(readOnly = true)
    public List<MaterialSummaryDTO> findPublished(String query) {
        List<Material> materials = query == null || query.isBlank()
                ? materialRepository.findAllByStatusOrderByUpdatedAtDesc(MaterialStatus.PUBLISHED)
                : materialRepository.findAllByStatusAndTitleContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCaseOrderByUpdatedAtDesc(
                    MaterialStatus.PUBLISHED, query.trim(), MaterialStatus.PUBLISHED, query.trim());
        return materials.stream().map(MaterialService::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public MaterialSummaryDTO findPublishedByCode(String code) {
        return materialRepository.findByMaterialCodeIgnoreCaseAndStatus(code.trim(), MaterialStatus.PUBLISHED)
                .map(MaterialService::toSummary).orElse(null);
    }

    @Transactional(readOnly = true)
    public MaterialSummaryDTO findPublishedById(Long id) {
        return materialRepository.findByIdAndStatus(id, MaterialStatus.PUBLISHED).map(MaterialService::toSummary)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<MaterialFileDTO> getPublishedFiles(Long materialId) {
        Material material = materialRepository.findByIdAndStatus(materialId, MaterialStatus.PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return material.getFiles().stream().map(MaterialService::toFileDTO).toList();
    }

    @Transactional(readOnly = true)
    public Resource getPublishedFile(Long materialId, Long fileId) {
        Material material = materialRepository.findByIdAndStatus(materialId, MaterialStatus.PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        MaterialFile file = material.getFiles().stream().filter(candidate -> candidate.getId().equals(fileId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Path target = getStoragePath().resolve(file.getStorageKey()).normalize();
        if (!target.getParent().equals(getStoragePath().normalize())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Resource resource = new FileSystemResource(target);
        if (!resource.exists() || !resource.isReadable()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return resource;
    }

    @Transactional(readOnly = true)
    public MaterialFileDTO getPublishedFileDetails(Long materialId, Long fileId) {
        Material material = materialRepository.findByIdAndStatus(materialId, MaterialStatus.PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return material.getFiles().stream().filter(candidate -> candidate.getId().equals(fileId))
                .findFirst().map(MaterialService::toFileDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<MaterialSummaryDTO> findMine(String email) {
        return materialRepository.findAllByAuthorEmailOrderByUpdatedAtDesc(email).stream().map(MaterialService::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<MaterialSummaryDTO> findMyPublishedForSelection(String email) {
        if (email == null) return List.of();
        return materialRepository.findAllByAuthorEmailAndStatusOrderByTitleAsc(email, MaterialStatus.PUBLISHED)
                .stream().map(MaterialService::toSummary).toList();
    }

    @Transactional
    public void publish(String title, String description, List<MultipartFile> uploads, String authorEmail) {
        List<MultipartFile> files = uploads == null ? List.of() : uploads.stream().filter(file -> !file.isEmpty()).toList();
        if (files.isEmpty() || files.size() > MAX_FILES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload between 1 and 5 files");
        }
        List<Path> stored = new ArrayList<>();
        try {
            Files.createDirectories(getStoragePath());
            Material material = new Material();
            material.setTitle(title.trim());
            material.setDescription(description.trim());
            material.setAuthor(userRepository.findByEmail(authorEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
            material.setStatus(MaterialStatus.PUBLISHED);
            for (MultipartFile upload : files) {
                String originalName = StringUtils.cleanPath(upload.getOriginalFilename() == null ? "file" : upload.getOriginalFilename());
                if (originalName.contains("..") || originalName.length() > 255) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
                }
                String extension = StringUtils.getFilenameExtension(originalName);
                if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This file type is not allowed");
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
        } catch (IOException exception) {
            deleteFiles(stored);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to store uploaded files", exception);
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
            try { Files.deleteIfExists(path); } catch (IOException ignored) { }
        });
    }

    private static MaterialSummaryDTO toSummary(Material material) {
        return new MaterialSummaryDTO(material.getId(), material.getMaterialCode(), material.getTitle(), material.getDescription(),
                material.getAuthor().getUsername(), material.getFiles().size(), material.getCreatedAt());
    }

    private static MaterialFileDTO toFileDTO(MaterialFile file) {
        return new MaterialFileDTO(file.getId(), file.getOriginalName(), file.getContentType(), file.getSizeBytes());
    }
}
