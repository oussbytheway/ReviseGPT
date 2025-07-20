package com.hybridata.revisegpt.web.rest;

import static com.hybridata.revisegpt.domain.ChatHistoryAsserts.*;
import static com.hybridata.revisegpt.web.rest.TestUtil.createUpdateProxyForBean;
import static com.hybridata.revisegpt.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybridata.revisegpt.IntegrationTest;
import com.hybridata.revisegpt.domain.ChatHistory;
import com.hybridata.revisegpt.repository.ChatHistoryRepository;
import com.hybridata.revisegpt.repository.UserRepository;
import com.hybridata.revisegpt.service.ChatHistoryService;
import com.hybridata.revisegpt.service.dto.ChatHistoryDTO;
import com.hybridata.revisegpt.service.mapper.ChatHistoryMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ChatHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ChatHistoryResourceIT {

    private static final String DEFAULT_PROMPT = "AAAAAAAAAA";
    private static final String UPDATED_PROMPT = "BBBBBBBBBB";

    private static final String DEFAULT_GENERATED_RESPONSE = "AAAAAAAAAA";
    private static final String UPDATED_GENERATED_RESPONSE = "BBBBBBBBBB";

    private static final Integer DEFAULT_TOKEN_USAGE = 1;
    private static final Integer UPDATED_TOKEN_USAGE = 2;

    private static final Integer DEFAULT_RESPONSE_TIME_MS = 1;
    private static final Integer UPDATED_RESPONSE_TIME_MS = 2;

    private static final Integer DEFAULT_FEEDBACK_RATING = 1;
    private static final Integer UPDATED_FEEDBACK_RATING = 2;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/chat-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ChatHistoryRepository chatHistoryRepositoryMock;

    @Autowired
    private ChatHistoryMapper chatHistoryMapper;

    @Mock
    private ChatHistoryService chatHistoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChatHistoryMockMvc;

    private ChatHistory chatHistory;

    private ChatHistory insertedChatHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChatHistory createEntity() {
        return new ChatHistory()
            .prompt(DEFAULT_PROMPT)
            .generatedResponse(DEFAULT_GENERATED_RESPONSE)
            .tokenUsage(DEFAULT_TOKEN_USAGE)
            .responseTimeMs(DEFAULT_RESPONSE_TIME_MS)
            .feedbackRating(DEFAULT_FEEDBACK_RATING)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChatHistory createUpdatedEntity() {
        return new ChatHistory()
            .prompt(UPDATED_PROMPT)
            .generatedResponse(UPDATED_GENERATED_RESPONSE)
            .tokenUsage(UPDATED_TOKEN_USAGE)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        chatHistory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedChatHistory != null) {
            chatHistoryRepository.delete(insertedChatHistory);
            insertedChatHistory = null;
        }
    }

    @Test
    @Transactional
    void createChatHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChatHistory
        ChatHistoryDTO chatHistoryDTO = chatHistoryMapper.toDto(chatHistory);
        var returnedChatHistoryDTO = om.readValue(
            restChatHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatHistoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChatHistoryDTO.class
        );

        // Validate the ChatHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChatHistory = chatHistoryMapper.toEntity(returnedChatHistoryDTO);
        assertChatHistoryUpdatableFieldsEquals(returnedChatHistory, getPersistedChatHistory(returnedChatHistory));

        insertedChatHistory = returnedChatHistory;
    }

    @Test
    @Transactional
    void createChatHistoryWithExistingId() throws Exception {
        // Create the ChatHistory with an existing ID
        chatHistory.setId(1L);
        ChatHistoryDTO chatHistoryDTO = chatHistoryMapper.toDto(chatHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChatHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ChatHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllChatHistories() throws Exception {
        // Initialize the database
        insertedChatHistory = chatHistoryRepository.saveAndFlush(chatHistory);

        // Get all the chatHistoryList
        restChatHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chatHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].prompt").value(hasItem(DEFAULT_PROMPT)))
            .andExpect(jsonPath("$.[*].generatedResponse").value(hasItem(DEFAULT_GENERATED_RESPONSE)))
            .andExpect(jsonPath("$.[*].tokenUsage").value(hasItem(DEFAULT_TOKEN_USAGE)))
            .andExpect(jsonPath("$.[*].responseTimeMs").value(hasItem(DEFAULT_RESPONSE_TIME_MS)))
            .andExpect(jsonPath("$.[*].feedbackRating").value(hasItem(DEFAULT_FEEDBACK_RATING)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChatHistoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(chatHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChatHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(chatHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChatHistoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(chatHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChatHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(chatHistoryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getChatHistory() throws Exception {
        // Initialize the database
        insertedChatHistory = chatHistoryRepository.saveAndFlush(chatHistory);

        // Get the chatHistory
        restChatHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, chatHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chatHistory.getId().intValue()))
            .andExpect(jsonPath("$.prompt").value(DEFAULT_PROMPT))
            .andExpect(jsonPath("$.generatedResponse").value(DEFAULT_GENERATED_RESPONSE))
            .andExpect(jsonPath("$.tokenUsage").value(DEFAULT_TOKEN_USAGE))
            .andExpect(jsonPath("$.responseTimeMs").value(DEFAULT_RESPONSE_TIME_MS))
            .andExpect(jsonPath("$.feedbackRating").value(DEFAULT_FEEDBACK_RATING))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingChatHistory() throws Exception {
        // Get the chatHistory
        restChatHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChatHistory() throws Exception {
        // Initialize the database
        insertedChatHistory = chatHistoryRepository.saveAndFlush(chatHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chatHistory
        ChatHistory updatedChatHistory = chatHistoryRepository.findById(chatHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChatHistory are not directly saved in db
        em.detach(updatedChatHistory);
        updatedChatHistory
            .prompt(UPDATED_PROMPT)
            .generatedResponse(UPDATED_GENERATED_RESPONSE)
            .tokenUsage(UPDATED_TOKEN_USAGE)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT);
        ChatHistoryDTO chatHistoryDTO = chatHistoryMapper.toDto(updatedChatHistory);

        restChatHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chatHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chatHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the ChatHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChatHistoryToMatchAllProperties(updatedChatHistory);
    }

    @Test
    @Transactional
    void putNonExistingChatHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatHistory.setId(longCount.incrementAndGet());

        // Create the ChatHistory
        ChatHistoryDTO chatHistoryDTO = chatHistoryMapper.toDto(chatHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chatHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chatHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChatHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatHistory.setId(longCount.incrementAndGet());

        // Create the ChatHistory
        ChatHistoryDTO chatHistoryDTO = chatHistoryMapper.toDto(chatHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chatHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChatHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatHistory.setId(longCount.incrementAndGet());

        // Create the ChatHistory
        ChatHistoryDTO chatHistoryDTO = chatHistoryMapper.toDto(chatHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChatHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChatHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedChatHistory = chatHistoryRepository.saveAndFlush(chatHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chatHistory using partial update
        ChatHistory partialUpdatedChatHistory = new ChatHistory();
        partialUpdatedChatHistory.setId(chatHistory.getId());

        partialUpdatedChatHistory.generatedResponse(UPDATED_GENERATED_RESPONSE).responseTimeMs(UPDATED_RESPONSE_TIME_MS);

        restChatHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChatHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChatHistory))
            )
            .andExpect(status().isOk());

        // Validate the ChatHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChatHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChatHistory, chatHistory),
            getPersistedChatHistory(chatHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateChatHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedChatHistory = chatHistoryRepository.saveAndFlush(chatHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chatHistory using partial update
        ChatHistory partialUpdatedChatHistory = new ChatHistory();
        partialUpdatedChatHistory.setId(chatHistory.getId());

        partialUpdatedChatHistory
            .prompt(UPDATED_PROMPT)
            .generatedResponse(UPDATED_GENERATED_RESPONSE)
            .tokenUsage(UPDATED_TOKEN_USAGE)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT);

        restChatHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChatHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChatHistory))
            )
            .andExpect(status().isOk());

        // Validate the ChatHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChatHistoryUpdatableFieldsEquals(partialUpdatedChatHistory, getPersistedChatHistory(partialUpdatedChatHistory));
    }

    @Test
    @Transactional
    void patchNonExistingChatHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatHistory.setId(longCount.incrementAndGet());

        // Create the ChatHistory
        ChatHistoryDTO chatHistoryDTO = chatHistoryMapper.toDto(chatHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chatHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chatHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChatHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatHistory.setId(longCount.incrementAndGet());

        // Create the ChatHistory
        ChatHistoryDTO chatHistoryDTO = chatHistoryMapper.toDto(chatHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chatHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChatHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatHistory.setId(longCount.incrementAndGet());

        // Create the ChatHistory
        ChatHistoryDTO chatHistoryDTO = chatHistoryMapper.toDto(chatHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chatHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChatHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChatHistory() throws Exception {
        // Initialize the database
        insertedChatHistory = chatHistoryRepository.saveAndFlush(chatHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chatHistory
        restChatHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, chatHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chatHistoryRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ChatHistory getPersistedChatHistory(ChatHistory chatHistory) {
        return chatHistoryRepository.findById(chatHistory.getId()).orElseThrow();
    }

    protected void assertPersistedChatHistoryToMatchAllProperties(ChatHistory expectedChatHistory) {
        assertChatHistoryAllPropertiesEquals(expectedChatHistory, getPersistedChatHistory(expectedChatHistory));
    }

    protected void assertPersistedChatHistoryToMatchUpdatableProperties(ChatHistory expectedChatHistory) {
        assertChatHistoryAllUpdatablePropertiesEquals(expectedChatHistory, getPersistedChatHistory(expectedChatHistory));
    }
}
