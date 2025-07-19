package com.hybridata.revisegpt.web.rest;

import com.hybridata.revisegpt.repository.CourseHistoryRepository;
import com.hybridata.revisegpt.service.CourseHistoryService;
import com.hybridata.revisegpt.service.dto.CourseHistoryDTO;
import com.hybridata.revisegpt.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.hybridata.revisegpt.domain.CourseHistory}.
 */
@RestController
@RequestMapping("/api/course-histories")
public class CourseHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseHistoryResource.class);

    private static final String ENTITY_NAME = "courseHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CourseHistoryService courseHistoryService;

    private final CourseHistoryRepository courseHistoryRepository;

    public CourseHistoryResource(CourseHistoryService courseHistoryService, CourseHistoryRepository courseHistoryRepository) {
        this.courseHistoryService = courseHistoryService;
        this.courseHistoryRepository = courseHistoryRepository;
    }

    /**
     * {@code POST  /course-histories} : Create a new courseHistory.
     *
     * @param courseHistoryDTO the courseHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseHistoryDTO, or with status {@code 400 (Bad Request)} if the courseHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CourseHistoryDTO> createCourseHistory(@RequestBody CourseHistoryDTO courseHistoryDTO) throws URISyntaxException {
        LOG.debug("REST request to save CourseHistory : {}", courseHistoryDTO);
        if (courseHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new courseHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        courseHistoryDTO = courseHistoryService.save(courseHistoryDTO);
        return ResponseEntity.created(new URI("/api/course-histories/" + courseHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, courseHistoryDTO.getId().toString()))
            .body(courseHistoryDTO);
    }

    /**
     * {@code PUT  /course-histories/:id} : Updates an existing courseHistory.
     *
     * @param id the id of the courseHistoryDTO to save.
     * @param courseHistoryDTO the courseHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the courseHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseHistoryDTO> updateCourseHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CourseHistoryDTO courseHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CourseHistory : {}, {}", id, courseHistoryDTO);
        if (courseHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        courseHistoryDTO = courseHistoryService.update(courseHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseHistoryDTO.getId().toString()))
            .body(courseHistoryDTO);
    }

    /**
     * {@code PATCH  /course-histories/:id} : Partial updates given fields of an existing courseHistory, field will ignore if it is null
     *
     * @param id the id of the courseHistoryDTO to save.
     * @param courseHistoryDTO the courseHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the courseHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the courseHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CourseHistoryDTO> partialUpdateCourseHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CourseHistoryDTO courseHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CourseHistory partially : {}, {}", id, courseHistoryDTO);
        if (courseHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseHistoryDTO> result = courseHistoryService.partialUpdate(courseHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /course-histories} : get all the courseHistories.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courseHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CourseHistoryDTO>> getAllCourseHistories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of CourseHistories");
        Page<CourseHistoryDTO> page;
        if (eagerload) {
            page = courseHistoryService.findAllWithEagerRelationships(pageable);
        } else {
            page = courseHistoryService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /course-histories/:id} : get the "id" courseHistory.
     *
     * @param id the id of the courseHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseHistoryDTO> getCourseHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CourseHistory : {}", id);
        Optional<CourseHistoryDTO> courseHistoryDTO = courseHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courseHistoryDTO);
    }

    /**
     * {@code DELETE  /course-histories/:id} : delete the "id" courseHistory.
     *
     * @param id the id of the courseHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CourseHistory : {}", id);
        courseHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
