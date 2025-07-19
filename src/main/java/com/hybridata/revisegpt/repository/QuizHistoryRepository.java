package com.hybridata.revisegpt.repository;

import com.hybridata.revisegpt.domain.QuizHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuizHistory entity.
 */
@Repository
public interface QuizHistoryRepository extends JpaRepository<QuizHistory, Long> {
    @Query("select quizHistory from QuizHistory quizHistory where quizHistory.user.login = ?#{authentication.name}")
    List<QuizHistory> findByUserIsCurrentUser();

    default Optional<QuizHistory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<QuizHistory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<QuizHistory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select quizHistory from QuizHistory quizHistory left join fetch quizHistory.user",
        countQuery = "select count(quizHistory) from QuizHistory quizHistory"
    )
    Page<QuizHistory> findAllWithToOneRelationships(Pageable pageable);

    @Query("select quizHistory from QuizHistory quizHistory left join fetch quizHistory.user")
    List<QuizHistory> findAllWithToOneRelationships();

    @Query("select quizHistory from QuizHistory quizHistory left join fetch quizHistory.user where quizHistory.id =:id")
    Optional<QuizHistory> findOneWithToOneRelationships(@Param("id") Long id);
}
