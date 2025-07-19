package com.hybridata.revisegpt.repository;

import com.hybridata.revisegpt.domain.ChatHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChatHistory entity.
 */
@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    @Query("select chatHistory from ChatHistory chatHistory where chatHistory.user.login = ?#{authentication.name}")
    List<ChatHistory> findByUserIsCurrentUser();

    default Optional<ChatHistory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ChatHistory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ChatHistory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select chatHistory from ChatHistory chatHistory left join fetch chatHistory.user",
        countQuery = "select count(chatHistory) from ChatHistory chatHistory"
    )
    Page<ChatHistory> findAllWithToOneRelationships(Pageable pageable);

    @Query("select chatHistory from ChatHistory chatHistory left join fetch chatHistory.user")
    List<ChatHistory> findAllWithToOneRelationships();

    @Query("select chatHistory from ChatHistory chatHistory left join fetch chatHistory.user where chatHistory.id =:id")
    Optional<ChatHistory> findOneWithToOneRelationships(@Param("id") Long id);
}
