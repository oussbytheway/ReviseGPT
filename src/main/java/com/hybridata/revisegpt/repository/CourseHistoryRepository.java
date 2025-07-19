package com.hybridata.revisegpt.repository;

import com.hybridata.revisegpt.domain.CourseHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CourseHistory entity.
 */
@Repository
public interface CourseHistoryRepository extends JpaRepository<CourseHistory, Long> {
    @Query("select courseHistory from CourseHistory courseHistory where courseHistory.user.login = ?#{authentication.name}")
    List<CourseHistory> findByUserIsCurrentUser();

    default Optional<CourseHistory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CourseHistory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CourseHistory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select courseHistory from CourseHistory courseHistory left join fetch courseHistory.user",
        countQuery = "select count(courseHistory) from CourseHistory courseHistory"
    )
    Page<CourseHistory> findAllWithToOneRelationships(Pageable pageable);

    @Query("select courseHistory from CourseHistory courseHistory left join fetch courseHistory.user")
    List<CourseHistory> findAllWithToOneRelationships();

    @Query("select courseHistory from CourseHistory courseHistory left join fetch courseHistory.user where courseHistory.id =:id")
    Optional<CourseHistory> findOneWithToOneRelationships(@Param("id") Long id);
}
