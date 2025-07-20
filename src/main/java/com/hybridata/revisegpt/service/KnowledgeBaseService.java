package com.hybridata.revisegpt.service;

import com.hybridata.revisegpt.domain.KnowledgeBase;
import com.hybridata.revisegpt.repository.KnowledgeBaseRepository;
import com.hybridata.revisegpt.service.dto.KnowledgeBaseDTO;
import com.hybridata.revisegpt.service.mapper.KnowledgeBaseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hybridata.revisegpt.domain.KnowledgeBase}.
 */
@Service
@Transactional
public class KnowledgeBaseService {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeBaseService.class);

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    private final KnowledgeBaseMapper knowledgeBaseMapper;

    public KnowledgeBaseService(KnowledgeBaseRepository knowledgeBaseRepository, KnowledgeBaseMapper knowledgeBaseMapper) {
        this.knowledgeBaseRepository = knowledgeBaseRepository;
        this.knowledgeBaseMapper = knowledgeBaseMapper;
    }

    /**
     * Save a knowledgeBase.
     *
     * @param knowledgeBaseDTO the entity to save.
     * @return the persisted entity.
     */
    public KnowledgeBaseDTO save(KnowledgeBaseDTO knowledgeBaseDTO) {
        LOG.debug("Request to save KnowledgeBase : {}", knowledgeBaseDTO);
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.toEntity(knowledgeBaseDTO);
        knowledgeBase = knowledgeBaseRepository.save(knowledgeBase);
        return knowledgeBaseMapper.toDto(knowledgeBase);
    }

    /**
     * Update a knowledgeBase.
     *
     * @param knowledgeBaseDTO the entity to save.
     * @return the persisted entity.
     */
    public KnowledgeBaseDTO update(KnowledgeBaseDTO knowledgeBaseDTO) {
        LOG.debug("Request to update KnowledgeBase : {}", knowledgeBaseDTO);
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.toEntity(knowledgeBaseDTO);
        knowledgeBase = knowledgeBaseRepository.save(knowledgeBase);
        return knowledgeBaseMapper.toDto(knowledgeBase);
    }

    /**
     * Partially update a knowledgeBase.
     *
     * @param knowledgeBaseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<KnowledgeBaseDTO> partialUpdate(KnowledgeBaseDTO knowledgeBaseDTO) {
        LOG.debug("Request to partially update KnowledgeBase : {}", knowledgeBaseDTO);

        return knowledgeBaseRepository
            .findById(knowledgeBaseDTO.getId())
            .map(existingKnowledgeBase -> {
                knowledgeBaseMapper.partialUpdate(existingKnowledgeBase, knowledgeBaseDTO);

                return existingKnowledgeBase;
            })
            .map(knowledgeBaseRepository::save)
            .map(knowledgeBaseMapper::toDto);
    }

    /**
     * Get all the knowledgeBases.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<KnowledgeBaseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all KnowledgeBases");
        return knowledgeBaseRepository.findAll(pageable).map(knowledgeBaseMapper::toDto);
    }

    /**
     * Get all the knowledgeBases with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<KnowledgeBaseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return knowledgeBaseRepository.findAllWithEagerRelationships(pageable).map(knowledgeBaseMapper::toDto);
    }

    /**
     * Get one knowledgeBase by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<KnowledgeBaseDTO> findOne(Long id) {
        LOG.debug("Request to get KnowledgeBase : {}", id);
        return knowledgeBaseRepository.findOneWithEagerRelationships(id).map(knowledgeBaseMapper::toDto);
    }

    /**
     * Delete the knowledgeBase by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete KnowledgeBase : {}", id);
        knowledgeBaseRepository.deleteById(id);
    }
}
