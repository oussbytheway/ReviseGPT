package com.hybridata.revisegpt.service;

import com.hybridata.revisegpt.domain.QuizHistory;
import com.hybridata.revisegpt.repository.QuizHistoryRepository;
import com.hybridata.revisegpt.service.dto.QuizHistoryDTO;
import com.hybridata.revisegpt.service.mapper.QuizHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hybridata.revisegpt.domain.QuizHistory}.
 */
@Service
@Transactional
public class QuizHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(QuizHistoryService.class);

    private final QuizHistoryRepository quizHistoryRepository;

    private final QuizHistoryMapper quizHistoryMapper;

    public QuizHistoryService(QuizHistoryRepository quizHistoryRepository, QuizHistoryMapper quizHistoryMapper) {
        this.quizHistoryRepository = quizHistoryRepository;
        this.quizHistoryMapper = quizHistoryMapper;
    }

    /**
     * Save a quizHistory.
     *
     * @param quizHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public QuizHistoryDTO save(QuizHistoryDTO quizHistoryDTO) {
        LOG.debug("Request to save QuizHistory : {}", quizHistoryDTO);
        QuizHistory quizHistory = quizHistoryMapper.toEntity(quizHistoryDTO);
        quizHistory = quizHistoryRepository.save(quizHistory);
        return quizHistoryMapper.toDto(quizHistory);
    }

    /**
     * Update a quizHistory.
     *
     * @param quizHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public QuizHistoryDTO update(QuizHistoryDTO quizHistoryDTO) {
        LOG.debug("Request to update QuizHistory : {}", quizHistoryDTO);
        QuizHistory quizHistory = quizHistoryMapper.toEntity(quizHistoryDTO);
        quizHistory = quizHistoryRepository.save(quizHistory);
        return quizHistoryMapper.toDto(quizHistory);
    }

    /**
     * Partially update a quizHistory.
     *
     * @param quizHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<QuizHistoryDTO> partialUpdate(QuizHistoryDTO quizHistoryDTO) {
        LOG.debug("Request to partially update QuizHistory : {}", quizHistoryDTO);

        return quizHistoryRepository
            .findById(quizHistoryDTO.getId())
            .map(existingQuizHistory -> {
                quizHistoryMapper.partialUpdate(existingQuizHistory, quizHistoryDTO);

                return existingQuizHistory;
            })
            .map(quizHistoryRepository::save)
            .map(quizHistoryMapper::toDto);
    }

    /**
     * Get all the quizHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<QuizHistoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all QuizHistories");
        return quizHistoryRepository.findAll(pageable).map(quizHistoryMapper::toDto);
    }

    /**
     * Get all the quizHistories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<QuizHistoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return quizHistoryRepository.findAllWithEagerRelationships(pageable).map(quizHistoryMapper::toDto);
    }

    /**
     * Get one quizHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<QuizHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get QuizHistory : {}", id);
        return quizHistoryRepository.findOneWithEagerRelationships(id).map(quizHistoryMapper::toDto);
    }

    /**
     * Delete the quizHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete QuizHistory : {}", id);
        quizHistoryRepository.deleteById(id);
    }
}
