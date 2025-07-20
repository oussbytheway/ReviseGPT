package com.hybridata.revisegpt.web.rest;

import com.hybridata.revisegpt.repository.KnowledgeBaseRepository;
import com.hybridata.revisegpt.service.KnowledgeBaseService;
import com.hybridata.revisegpt.service.dto.KnowledgeBaseDTO;
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
 * REST controller for managing {@link com.hybridata.revisegpt.domain.KnowledgeBase}.
 */
@RestController
@RequestMapping("/api/knowledge-bases")
public class KnowledgeBaseResource {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeBaseResource.class);

    private static final String ENTITY_NAME = "knowledgeBase";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KnowledgeBaseService knowledgeBaseService;

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    public KnowledgeBaseResource(KnowledgeBaseService knowledgeBaseService, KnowledgeBaseRepository knowledgeBaseRepository) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.knowledgeBaseRepository = knowledgeBaseRepository;
    }

    /**
     * {@code POST  /knowledge-bases} : Create a new knowledgeBase.
     *
     * @param knowledgeBaseDTO the knowledgeBaseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new knowledgeBaseDTO, or with status {@code 400 (Bad Request)} if the knowledgeBase has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<KnowledgeBaseDTO> createKnowledgeBase(@RequestBody KnowledgeBaseDTO knowledgeBaseDTO) throws URISyntaxException {
        LOG.debug("REST request to save KnowledgeBase : {}", knowledgeBaseDTO);
        if (knowledgeBaseDTO.getId() != null) {
            throw new BadRequestAlertException("A new knowledgeBase cannot already have an ID", ENTITY_NAME, "idexists");
        }
        knowledgeBaseDTO = knowledgeBaseService.save(knowledgeBaseDTO);
        return ResponseEntity.created(new URI("/api/knowledge-bases/" + knowledgeBaseDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, knowledgeBaseDTO.getId().toString()))
            .body(knowledgeBaseDTO);
    }

    /**
     * {@code PUT  /knowledge-bases/:id} : Updates an existing knowledgeBase.
     *
     * @param id the id of the knowledgeBaseDTO to save.
     * @param knowledgeBaseDTO the knowledgeBaseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeBaseDTO,
     * or with status {@code 400 (Bad Request)} if the knowledgeBaseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeBaseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeBaseDTO> updateKnowledgeBase(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody KnowledgeBaseDTO knowledgeBaseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update KnowledgeBase : {}, {}", id, knowledgeBaseDTO);
        if (knowledgeBaseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeBaseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!knowledgeBaseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        knowledgeBaseDTO = knowledgeBaseService.update(knowledgeBaseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, knowledgeBaseDTO.getId().toString()))
            .body(knowledgeBaseDTO);
    }

    /**
     * {@code PATCH  /knowledge-bases/:id} : Partial updates given fields of an existing knowledgeBase, field will ignore if it is null
     *
     * @param id the id of the knowledgeBaseDTO to save.
     * @param knowledgeBaseDTO the knowledgeBaseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeBaseDTO,
     * or with status {@code 400 (Bad Request)} if the knowledgeBaseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the knowledgeBaseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeBaseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<KnowledgeBaseDTO> partialUpdateKnowledgeBase(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody KnowledgeBaseDTO knowledgeBaseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update KnowledgeBase partially : {}, {}", id, knowledgeBaseDTO);
        if (knowledgeBaseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeBaseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!knowledgeBaseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<KnowledgeBaseDTO> result = knowledgeBaseService.partialUpdate(knowledgeBaseDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, knowledgeBaseDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /knowledge-bases} : get all the knowledgeBases.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of knowledgeBases in body.
     */
    @GetMapping("")
    public ResponseEntity<List<KnowledgeBaseDTO>> getAllKnowledgeBases(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of KnowledgeBases");
        Page<KnowledgeBaseDTO> page;
        if (eagerload) {
            page = knowledgeBaseService.findAllWithEagerRelationships(pageable);
        } else {
            page = knowledgeBaseService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /knowledge-bases/:id} : get the "id" knowledgeBase.
     *
     * @param id the id of the knowledgeBaseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the knowledgeBaseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeBaseDTO> getKnowledgeBase(@PathVariable("id") Long id) {
        LOG.debug("REST request to get KnowledgeBase : {}", id);
        Optional<KnowledgeBaseDTO> knowledgeBaseDTO = knowledgeBaseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(knowledgeBaseDTO);
    }

    /**
     * {@code DELETE  /knowledge-bases/:id} : delete the "id" knowledgeBase.
     *
     * @param id the id of the knowledgeBaseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKnowledgeBase(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete KnowledgeBase : {}", id);
        knowledgeBaseService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
