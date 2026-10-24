package com.order.platform.assester.repositories;

import com.order.platform.assester.entities.Course;
import com.order.platform.assester.entities.Material;
import com.order.platform.assester.enums.MaterialStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    @EntityGraph(attributePaths = "files")
    List<Material> findAllByAuthorEmailOrderByUpdatedAtDesc(String email);

    @EntityGraph(attributePaths = {"files", "author"})
    List<Material> findAllByStatusOrderByUpdatedAtDesc(MaterialStatus status);

    @Query("""
    SELECT m
    FROM Material m
    WHERE m.status = MaterialStatus.PUBLISHED
    AND LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%'))
    ORDER BY m.updatedAt DESC
    """)
    @EntityGraph(attributePaths = {"files", "author"})
    List<Material> findAllPublishedByQuery(
            String query);

    @EntityGraph(attributePaths = {"files", "author"})
    Optional<Material> findByMaterialCodeIgnoreCaseAndStatus(String code, MaterialStatus status);

    @EntityGraph(attributePaths = {"files", "author"})
    Optional<Material> findByIdAndStatus(Long id, MaterialStatus status);

    @EntityGraph(attributePaths = "files")
    Optional<Material> findByIdAndAuthorEmail(Long id, String email);

    @Query("""
    SELECT m
    FROM Material m
    WHERE m.author.email = :email
    AND m.status = MaterialStatus.PUBLISHED
    ORDER BY m.title ASC
    """)
    @EntityGraph(attributePaths = "files")
    List<Material> findAllAuthorPublishedMaterials(String email);

    @Query("""
    SELECT m
    FROM Material m
    WHERE m.id IN :ids
    AND m.author.email = :authorEmail
    AND m.status = MaterialStatus.PUBLISHED
    """)
    List<Material> findPublishedAuthorMaterialsByIds(List<Long> ids, String email);
}
