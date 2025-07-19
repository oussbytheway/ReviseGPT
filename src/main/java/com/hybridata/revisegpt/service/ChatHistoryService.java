package com.hybridata.revisegpt.service;

import com.hybridata.revisegpt.domain.ChatHistory;
import com.hybridata.revisegpt.repository.ChatHistoryRepository;
import com.hybridata.revisegpt.service.dto.ChatHistoryDTO;
import com.hybridata.revisegpt.service.mapper.ChatHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hybridata.revisegpt.domain.ChatHistory}.
 */
@Service
@Transactional
public class ChatHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatHistoryService.class);

    private final ChatHistoryRepository chatHistoryRepository;

    private final ChatHistoryMapper chatHistoryMapper;

    public ChatHistoryService(ChatHistoryRepository chatHistoryRepository, ChatHistoryMapper chatHistoryMapper) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.chatHistoryMapper = chatHistoryMapper;
    }

    /**
     * Save a chatHistory.
     *
     * @param chatHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public ChatHistoryDTO save(ChatHistoryDTO chatHistoryDTO) {
        LOG.debug("Request to save ChatHistory : {}", chatHistoryDTO);
        ChatHistory chatHistory = chatHistoryMapper.toEntity(chatHistoryDTO);
        chatHistory = chatHistoryRepository.save(chatHistory);
        return chatHistoryMapper.toDto(chatHistory);
    }

    /**
     * Update a chatHistory.
     *
     * @param chatHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public ChatHistoryDTO update(ChatHistoryDTO chatHistoryDTO) {
        LOG.debug("Request to update ChatHistory : {}", chatHistoryDTO);
        ChatHistory chatHistory = chatHistoryMapper.toEntity(chatHistoryDTO);
        chatHistory = chatHistoryRepository.save(chatHistory);
        return chatHistoryMapper.toDto(chatHistory);
    }

    /**
     * Partially update a chatHistory.
     *
     * @param chatHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ChatHistoryDTO> partialUpdate(ChatHistoryDTO chatHistoryDTO) {
        LOG.debug("Request to partially update ChatHistory : {}", chatHistoryDTO);

        return chatHistoryRepository
            .findById(chatHistoryDTO.getId())
            .map(existingChatHistory -> {
                chatHistoryMapper.partialUpdate(existingChatHistory, chatHistoryDTO);

                return existingChatHistory;
            })
            .map(chatHistoryRepository::save)
            .map(chatHistoryMapper::toDto);
    }

    /**
     * Get all the chatHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ChatHistoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ChatHistories");
        return chatHistoryRepository.findAll(pageable).map(chatHistoryMapper::toDto);
    }

    /**
     * Get all the chatHistories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ChatHistoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return chatHistoryRepository.findAllWithEagerRelationships(pageable).map(chatHistoryMapper::toDto);
    }

    /**
     * Get one chatHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ChatHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get ChatHistory : {}", id);
        return chatHistoryRepository.findOneWithEagerRelationships(id).map(chatHistoryMapper::toDto);
    }

    /**
     * Delete the chatHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ChatHistory : {}", id);
        chatHistoryRepository.deleteById(id);
    }
}
