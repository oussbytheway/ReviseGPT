package com.hybridata.revisegpt.web.rest;

import com.hybridata.revisegpt.repository.QuizHistoryRepository;
import com.hybridata.revisegpt.service.QuizHistoryService;
import com.hybridata.revisegpt.service.dto.QuizHistoryDTO;
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
 * REST controller for managing {@link com.hybridata.revisegpt.domain.QuizHistory}.
 */
@RestController
@RequestMapping("/api/quiz-histories")
public class QuizHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(QuizHistoryResource.class);

    private static final String ENTITY_NAME = "quizHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuizHistoryService quizHistoryService;

    private final QuizHistoryRepository quizHistoryRepository;

    public QuizHistoryResource(QuizHistoryService quizHistoryService, QuizHistoryRepository quizHistoryRepository) {
        this.quizHistoryService = quizHistoryService;
        this.quizHistoryRepository = quizHistoryRepository;
    }

    /**
     * {@code POST  /quiz-histories} : Create a new quizHistory.
     *
     * @param quizHistoryDTO the quizHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quizHistoryDTO, or with status {@code 400 (Bad Request)} if the quizHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<QuizHistoryDTO> createQuizHistory(@RequestBody QuizHistoryDTO quizHistoryDTO) throws URISyntaxException {
        LOG.debug("REST request to save QuizHistory : {}", quizHistoryDTO);
        if (quizHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new quizHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        quizHistoryDTO = quizHistoryService.save(quizHistoryDTO);
        return ResponseEntity.created(new URI("/api/quiz-histories/" + quizHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, quizHistoryDTO.getId().toString()))
            .body(quizHistoryDTO);
    }

    /**
     * {@code PUT  /quiz-histories/:id} : Updates an existing quizHistory.
     *
     * @param id the id of the quizHistoryDTO to save.
     * @param quizHistoryDTO the quizHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quizHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the quizHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quizHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuizHistoryDTO> updateQuizHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody QuizHistoryDTO quizHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update QuizHistory : {}, {}", id, quizHistoryDTO);
        if (quizHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quizHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        quizHistoryDTO = quizHistoryService.update(quizHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quizHistoryDTO.getId().toString()))
            .body(quizHistoryDTO);
    }

    /**
     * {@code PATCH  /quiz-histories/:id} : Partial updates given fields of an existing quizHistory, field will ignore if it is null
     *
     * @param id the id of the quizHistoryDTO to save.
     * @param quizHistoryDTO the quizHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quizHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the quizHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the quizHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the quizHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<QuizHistoryDTO> partialUpdateQuizHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody QuizHistoryDTO quizHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update QuizHistory partially : {}, {}", id, quizHistoryDTO);
        if (quizHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quizHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuizHistoryDTO> result = quizHistoryService.partialUpdate(quizHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quizHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /quiz-histories} : get all the quizHistories.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of quizHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<QuizHistoryDTO>> getAllQuizHistories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of QuizHistories");
        Page<QuizHistoryDTO> page;
        if (eagerload) {
            page = quizHistoryService.findAllWithEagerRelationships(pageable);
        } else {
            page = quizHistoryService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /quiz-histories/:id} : get the "id" quizHistory.
     *
     * @param id the id of the quizHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quizHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuizHistoryDTO> getQuizHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get QuizHistory : {}", id);
        Optional<QuizHistoryDTO> quizHistoryDTO = quizHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(quizHistoryDTO);
    }

    /**
     * {@code DELETE  /quiz-histories/:id} : delete the "id" quizHistory.
     *
     * @param id the id of the quizHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuizHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete QuizHistory : {}", id);
        quizHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
