package com.hybridata.revisegpt.service;

import com.hybridata.revisegpt.domain.CourseHistory;
import com.hybridata.revisegpt.repository.CourseHistoryRepository;
import com.hybridata.revisegpt.service.dto.CourseHistoryDTO;
import com.hybridata.revisegpt.service.mapper.CourseHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hybridata.revisegpt.domain.CourseHistory}.
 */
@Service
@Transactional
public class CourseHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseHistoryService.class);

    private final CourseHistoryRepository courseHistoryRepository;

    private final CourseHistoryMapper courseHistoryMapper;

    public CourseHistoryService(CourseHistoryRepository courseHistoryRepository, CourseHistoryMapper courseHistoryMapper) {
        this.courseHistoryRepository = courseHistoryRepository;
        this.courseHistoryMapper = courseHistoryMapper;
    }

    /**
     * Save a courseHistory.
     *
     * @param courseHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public CourseHistoryDTO save(CourseHistoryDTO courseHistoryDTO) {
        LOG.debug("Request to save CourseHistory : {}", courseHistoryDTO);
        CourseHistory courseHistory = courseHistoryMapper.toEntity(courseHistoryDTO);
        courseHistory = courseHistoryRepository.save(courseHistory);
        return courseHistoryMapper.toDto(courseHistory);
    }

    /**
     * Update a courseHistory.
     *
     * @param courseHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public CourseHistoryDTO update(CourseHistoryDTO courseHistoryDTO) {
        LOG.debug("Request to update CourseHistory : {}", courseHistoryDTO);
        CourseHistory courseHistory = courseHistoryMapper.toEntity(courseHistoryDTO);
        courseHistory = courseHistoryRepository.save(courseHistory);
        return courseHistoryMapper.toDto(courseHistory);
    }

    /**
     * Partially update a courseHistory.
     *
     * @param courseHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CourseHistoryDTO> partialUpdate(CourseHistoryDTO courseHistoryDTO) {
        LOG.debug("Request to partially update CourseHistory : {}", courseHistoryDTO);

        return courseHistoryRepository
            .findById(courseHistoryDTO.getId())
            .map(existingCourseHistory -> {
                courseHistoryMapper.partialUpdate(existingCourseHistory, courseHistoryDTO);

                return existingCourseHistory;
            })
            .map(courseHistoryRepository::save)
            .map(courseHistoryMapper::toDto);
    }

    /**
     * Get all the courseHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseHistoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CourseHistories");
        return courseHistoryRepository.findAll(pageable).map(courseHistoryMapper::toDto);
    }

    /**
     * Get all the courseHistories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CourseHistoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseHistoryRepository.findAllWithEagerRelationships(pageable).map(courseHistoryMapper::toDto);
    }

    /**
     * Get one courseHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CourseHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get CourseHistory : {}", id);
        return courseHistoryRepository.findOneWithEagerRelationships(id).map(courseHistoryMapper::toDto);
    }

    /**
     * Delete the courseHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CourseHistory : {}", id);
        courseHistoryRepository.deleteById(id);
    }
}
