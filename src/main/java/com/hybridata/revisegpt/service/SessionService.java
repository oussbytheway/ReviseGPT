package com.hybridata.revisegpt.service;

import com.hybridata.revisegpt.domain.Session;
import com.hybridata.revisegpt.repository.SessionRepository;
import com.hybridata.revisegpt.service.dto.SessionDTO;
import com.hybridata.revisegpt.service.mapper.SessionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hybridata.revisegpt.domain.Session}.
 */
@Service
@Transactional
public class SessionService {

    private static final Logger LOG = LoggerFactory.getLogger(SessionService.class);

    private final SessionRepository sessionRepository;

    private final SessionMapper sessionMapper;

    public SessionService(SessionRepository sessionRepository, SessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
    }

    /**
     * Save a session.
     *
     * @param sessionDTO the entity to save.
     * @return the persisted entity.
     */
    public SessionDTO save(SessionDTO sessionDTO) {
        LOG.debug("Request to save Session : {}", sessionDTO);
        Session session = sessionMapper.toEntity(sessionDTO);
        session = sessionRepository.save(session);
        return sessionMapper.toDto(session);
    }

    /**
     * Update a session.
     *
     * @param sessionDTO the entity to save.
     * @return the persisted entity.
     */
    public SessionDTO update(SessionDTO sessionDTO) {
        LOG.debug("Request to update Session : {}", sessionDTO);
        Session session = sessionMapper.toEntity(sessionDTO);
        session = sessionRepository.save(session);
        return sessionMapper.toDto(session);
    }

    /**
     * Partially update a session.
     *
     * @param sessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SessionDTO> partialUpdate(SessionDTO sessionDTO) {
        LOG.debug("Request to partially update Session : {}", sessionDTO);

        return sessionRepository
            .findById(sessionDTO.getId())
            .map(existingSession -> {
                sessionMapper.partialUpdate(existingSession, sessionDTO);

                return existingSession;
            })
            .map(sessionRepository::save)
            .map(sessionMapper::toDto);
    }

    /**
     * Get all the sessions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SessionDTO> findAll() {
        LOG.debug("Request to get all Sessions");
        return sessionRepository.findAll().stream().map(sessionMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the sessions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SessionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return sessionRepository.findAllWithEagerRelationships(pageable).map(sessionMapper::toDto);
    }

    /**
     * Get one session by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SessionDTO> findOne(Long id) {
        LOG.debug("Request to get Session : {}", id);
        return sessionRepository.findOneWithEagerRelationships(id).map(sessionMapper::toDto);
    }

    /**
     * Delete the session by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Session : {}", id);
        sessionRepository.deleteById(id);
    }
}
