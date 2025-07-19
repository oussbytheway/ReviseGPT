package com.hybridata.revisegpt.web.rest;

import com.hybridata.revisegpt.repository.ChatHistoryRepository;
import com.hybridata.revisegpt.service.ChatHistoryService;
import com.hybridata.revisegpt.service.dto.ChatHistoryDTO;
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
 * REST controller for managing {@link com.hybridata.revisegpt.domain.ChatHistory}.
 */
@RestController
@RequestMapping("/api/chat-histories")
public class ChatHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChatHistoryResource.class);

    private static final String ENTITY_NAME = "chatHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChatHistoryService chatHistoryService;

    private final ChatHistoryRepository chatHistoryRepository;

    public ChatHistoryResource(ChatHistoryService chatHistoryService, ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryService = chatHistoryService;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    /**
     * {@code POST  /chat-histories} : Create a new chatHistory.
     *
     * @param chatHistoryDTO the chatHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chatHistoryDTO, or with status {@code 400 (Bad Request)} if the chatHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChatHistoryDTO> createChatHistory(@RequestBody ChatHistoryDTO chatHistoryDTO) throws URISyntaxException {
        LOG.debug("REST request to save ChatHistory : {}", chatHistoryDTO);
        if (chatHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new chatHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        chatHistoryDTO = chatHistoryService.save(chatHistoryDTO);
        return ResponseEntity.created(new URI("/api/chat-histories/" + chatHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, chatHistoryDTO.getId().toString()))
            .body(chatHistoryDTO);
    }

    /**
     * {@code PUT  /chat-histories/:id} : Updates an existing chatHistory.
     *
     * @param id the id of the chatHistoryDTO to save.
     * @param chatHistoryDTO the chatHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chatHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the chatHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chatHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChatHistoryDTO> updateChatHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChatHistoryDTO chatHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ChatHistory : {}, {}", id, chatHistoryDTO);
        if (chatHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chatHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chatHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        chatHistoryDTO = chatHistoryService.update(chatHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, chatHistoryDTO.getId().toString()))
            .body(chatHistoryDTO);
    }

    /**
     * {@code PATCH  /chat-histories/:id} : Partial updates given fields of an existing chatHistory, field will ignore if it is null
     *
     * @param id the id of the chatHistoryDTO to save.
     * @param chatHistoryDTO the chatHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chatHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the chatHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the chatHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the chatHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChatHistoryDTO> partialUpdateChatHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChatHistoryDTO chatHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ChatHistory partially : {}, {}", id, chatHistoryDTO);
        if (chatHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chatHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chatHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChatHistoryDTO> result = chatHistoryService.partialUpdate(chatHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, chatHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /chat-histories} : get all the chatHistories.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chatHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ChatHistoryDTO>> getAllChatHistories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ChatHistories");
        Page<ChatHistoryDTO> page;
        if (eagerload) {
            page = chatHistoryService.findAllWithEagerRelationships(pageable);
        } else {
            page = chatHistoryService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /chat-histories/:id} : get the "id" chatHistory.
     *
     * @param id the id of the chatHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chatHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChatHistoryDTO> getChatHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ChatHistory : {}", id);
        Optional<ChatHistoryDTO> chatHistoryDTO = chatHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chatHistoryDTO);
    }

    /**
     * {@code DELETE  /chat-histories/:id} : delete the "id" chatHistory.
     *
     * @param id the id of the chatHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ChatHistory : {}", id);
        chatHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
