package com.order.platform.assester.repositories;

import com.order.platform.assester.entities.Material;
import com.order.platform.assester.enums.MaterialStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    @EntityGraph(attributePaths = "files")
    List<Material> findAllByAuthorEmailOrderByUpdatedAtDesc(String email);

    @EntityGraph(attributePaths = {"files", "author"})
    List<Material> findAllByStatusOrderByUpdatedAtDesc(MaterialStatus status);

    @EntityGraph(attributePaths = {"files", "author"})
    List<Material> findAllByStatusAndTitleContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCaseOrderByUpdatedAtDesc(
            MaterialStatus titleStatus, String title, MaterialStatus descriptionStatus, String description);

    @EntityGraph(attributePaths = {"files", "author"})
    Optional<Material> findByMaterialCodeIgnoreCaseAndStatus(String code, MaterialStatus status);

    @EntityGraph(attributePaths = {"files", "author"})
    Optional<Material> findByIdAndStatus(Long id, MaterialStatus status);

    @EntityGraph(attributePaths = "files")
    Optional<Material> findByIdAndAuthorEmail(Long id, String email);

    @EntityGraph(attributePaths = "files")
    List<Material> findAllByAuthorEmailAndStatusOrderByTitleAsc(String email, MaterialStatus status);

    List<Material> findAllByIdInAndAuthorEmailAndStatus(List<Long> ids, String email, MaterialStatus status);
}
