package com.hybridata.revisegpt.repository;

import com.hybridata.revisegpt.domain.KnowledgeBase;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KnowledgeBase entity.
 */
@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {
    @Query("select knowledgeBase from KnowledgeBase knowledgeBase where knowledgeBase.user.login = ?#{authentication.name}")
    List<KnowledgeBase> findByUserIsCurrentUser();

    default Optional<KnowledgeBase> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<KnowledgeBase> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<KnowledgeBase> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select knowledgeBase from KnowledgeBase knowledgeBase left join fetch knowledgeBase.user",
        countQuery = "select count(knowledgeBase) from KnowledgeBase knowledgeBase"
    )
    Page<KnowledgeBase> findAllWithToOneRelationships(Pageable pageable);

    @Query("select knowledgeBase from KnowledgeBase knowledgeBase left join fetch knowledgeBase.user")
    List<KnowledgeBase> findAllWithToOneRelationships();

    @Query("select knowledgeBase from KnowledgeBase knowledgeBase left join fetch knowledgeBase.user where knowledgeBase.id =:id")
    Optional<KnowledgeBase> findOneWithToOneRelationships(@Param("id") Long id);
}
