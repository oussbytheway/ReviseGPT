package com.hybridata.revisegpt.web.rest;

import static com.hybridata.revisegpt.domain.QuizHistoryAsserts.*;
import static com.hybridata.revisegpt.web.rest.TestUtil.createUpdateProxyForBean;
import static com.hybridata.revisegpt.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybridata.revisegpt.IntegrationTest;
import com.hybridata.revisegpt.domain.QuizHistory;
import com.hybridata.revisegpt.domain.enumeration.InputType;
import com.hybridata.revisegpt.repository.QuizHistoryRepository;
import com.hybridata.revisegpt.repository.UserRepository;
import com.hybridata.revisegpt.service.QuizHistoryService;
import com.hybridata.revisegpt.service.dto.QuizHistoryDTO;
import com.hybridata.revisegpt.service.mapper.QuizHistoryMapper;
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
 * Integration tests for the {@link QuizHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuizHistoryResourceIT {

    private static final InputType DEFAULT_INPUT_TYPE = InputType.PDF;
    private static final InputType UPDATED_INPUT_TYPE = InputType.TEXT;

    private static final String DEFAULT_PROMPT = "AAAAAAAAAA";
    private static final String UPDATED_PROMPT = "BBBBBBBBBB";

    private static final String DEFAULT_GENERATED_RESPONSE = "AAAAAAAAAA";
    private static final String UPDATED_GENERATED_RESPONSE = "BBBBBBBBBB";

    private static final String DEFAULT_GENERATED_FILE_URL = "AAAAAAAAAA";
    private static final String UPDATED_GENERATED_FILE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_INPUT_FILE_URL = "AAAAAAAAAA";
    private static final String UPDATED_INPUT_FILE_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_TOKEN_USAGE = 1;
    private static final Integer UPDATED_TOKEN_USAGE = 2;

    private static final Integer DEFAULT_RESPONSE_TIME_MS = 1;
    private static final Integer UPDATED_RESPONSE_TIME_MS = 2;

    private static final Integer DEFAULT_FEEDBACK_RATING = 1;
    private static final Integer UPDATED_FEEDBACK_RATING = 2;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/quiz-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuizHistoryRepository quizHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private QuizHistoryRepository quizHistoryRepositoryMock;

    @Autowired
    private QuizHistoryMapper quizHistoryMapper;

    @Mock
    private QuizHistoryService quizHistoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuizHistoryMockMvc;

    private QuizHistory quizHistory;

    private QuizHistory insertedQuizHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuizHistory createEntity() {
        return new QuizHistory()
            .inputType(DEFAULT_INPUT_TYPE)
            .prompt(DEFAULT_PROMPT)
            .generatedResponse(DEFAULT_GENERATED_RESPONSE)
            .generatedFileUrl(DEFAULT_GENERATED_FILE_URL)
            .inputFileUrl(DEFAULT_INPUT_FILE_URL)
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
    public static QuizHistory createUpdatedEntity() {
        return new QuizHistory()
            .inputType(UPDATED_INPUT_TYPE)
            .prompt(UPDATED_PROMPT)
            .generatedResponse(UPDATED_GENERATED_RESPONSE)
            .generatedFileUrl(UPDATED_GENERATED_FILE_URL)
            .inputFileUrl(UPDATED_INPUT_FILE_URL)
            .tokenUsage(UPDATED_TOKEN_USAGE)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        quizHistory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedQuizHistory != null) {
            quizHistoryRepository.delete(insertedQuizHistory);
            insertedQuizHistory = null;
        }
    }

    @Test
    @Transactional
    void createQuizHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the QuizHistory
        QuizHistoryDTO quizHistoryDTO = quizHistoryMapper.toDto(quizHistory);
        var returnedQuizHistoryDTO = om.readValue(
            restQuizHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizHistoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuizHistoryDTO.class
        );

        // Validate the QuizHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuizHistory = quizHistoryMapper.toEntity(returnedQuizHistoryDTO);
        assertQuizHistoryUpdatableFieldsEquals(returnedQuizHistory, getPersistedQuizHistory(returnedQuizHistory));

        insertedQuizHistory = returnedQuizHistory;
    }

    @Test
    @Transactional
    void createQuizHistoryWithExistingId() throws Exception {
        // Create the QuizHistory with an existing ID
        quizHistory.setId(1L);
        QuizHistoryDTO quizHistoryDTO = quizHistoryMapper.toDto(quizHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuizHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the QuizHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllQuizHistories() throws Exception {
        // Initialize the database
        insertedQuizHistory = quizHistoryRepository.saveAndFlush(quizHistory);

        // Get all the quizHistoryList
        restQuizHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].inputType").value(hasItem(DEFAULT_INPUT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].prompt").value(hasItem(DEFAULT_PROMPT)))
            .andExpect(jsonPath("$.[*].generatedResponse").value(hasItem(DEFAULT_GENERATED_RESPONSE)))
            .andExpect(jsonPath("$.[*].generatedFileUrl").value(hasItem(DEFAULT_GENERATED_FILE_URL)))
            .andExpect(jsonPath("$.[*].inputFileUrl").value(hasItem(DEFAULT_INPUT_FILE_URL)))
            .andExpect(jsonPath("$.[*].tokenUsage").value(hasItem(DEFAULT_TOKEN_USAGE)))
            .andExpect(jsonPath("$.[*].responseTimeMs").value(hasItem(DEFAULT_RESPONSE_TIME_MS)))
            .andExpect(jsonPath("$.[*].feedbackRating").value(hasItem(DEFAULT_FEEDBACK_RATING)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuizHistoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(quizHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuizHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(quizHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuizHistoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(quizHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuizHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(quizHistoryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getQuizHistory() throws Exception {
        // Initialize the database
        insertedQuizHistory = quizHistoryRepository.saveAndFlush(quizHistory);

        // Get the quizHistory
        restQuizHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, quizHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quizHistory.getId().intValue()))
            .andExpect(jsonPath("$.inputType").value(DEFAULT_INPUT_TYPE.toString()))
            .andExpect(jsonPath("$.prompt").value(DEFAULT_PROMPT))
            .andExpect(jsonPath("$.generatedResponse").value(DEFAULT_GENERATED_RESPONSE))
            .andExpect(jsonPath("$.generatedFileUrl").value(DEFAULT_GENERATED_FILE_URL))
            .andExpect(jsonPath("$.inputFileUrl").value(DEFAULT_INPUT_FILE_URL))
            .andExpect(jsonPath("$.tokenUsage").value(DEFAULT_TOKEN_USAGE))
            .andExpect(jsonPath("$.responseTimeMs").value(DEFAULT_RESPONSE_TIME_MS))
            .andExpect(jsonPath("$.feedbackRating").value(DEFAULT_FEEDBACK_RATING))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingQuizHistory() throws Exception {
        // Get the quizHistory
        restQuizHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuizHistory() throws Exception {
        // Initialize the database
        insertedQuizHistory = quizHistoryRepository.saveAndFlush(quizHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quizHistory
        QuizHistory updatedQuizHistory = quizHistoryRepository.findById(quizHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuizHistory are not directly saved in db
        em.detach(updatedQuizHistory);
        updatedQuizHistory
            .inputType(UPDATED_INPUT_TYPE)
            .prompt(UPDATED_PROMPT)
            .generatedResponse(UPDATED_GENERATED_RESPONSE)
            .generatedFileUrl(UPDATED_GENERATED_FILE_URL)
            .inputFileUrl(UPDATED_INPUT_FILE_URL)
            .tokenUsage(UPDATED_TOKEN_USAGE)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT);
        QuizHistoryDTO quizHistoryDTO = quizHistoryMapper.toDto(updatedQuizHistory);

        restQuizHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quizHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quizHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the QuizHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuizHistoryToMatchAllProperties(updatedQuizHistory);
    }

    @Test
    @Transactional
    void putNonExistingQuizHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizHistory.setId(longCount.incrementAndGet());

        // Create the QuizHistory
        QuizHistoryDTO quizHistoryDTO = quizHistoryMapper.toDto(quizHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quizHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quizHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuizHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizHistory.setId(longCount.incrementAndGet());

        // Create the QuizHistory
        QuizHistoryDTO quizHistoryDTO = quizHistoryMapper.toDto(quizHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quizHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuizHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizHistory.setId(longCount.incrementAndGet());

        // Create the QuizHistory
        QuizHistoryDTO quizHistoryDTO = quizHistoryMapper.toDto(quizHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuizHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuizHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedQuizHistory = quizHistoryRepository.saveAndFlush(quizHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quizHistory using partial update
        QuizHistory partialUpdatedQuizHistory = new QuizHistory();
        partialUpdatedQuizHistory.setId(quizHistory.getId());

        partialUpdatedQuizHistory
            .prompt(UPDATED_PROMPT)
            .generatedResponse(UPDATED_GENERATED_RESPONSE)
            .generatedFileUrl(UPDATED_GENERATED_FILE_URL)
            .createdAt(UPDATED_CREATED_AT);

        restQuizHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuizHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuizHistory))
            )
            .andExpect(status().isOk());

        // Validate the QuizHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuizHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedQuizHistory, quizHistory),
            getPersistedQuizHistory(quizHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateQuizHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedQuizHistory = quizHistoryRepository.saveAndFlush(quizHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quizHistory using partial update
        QuizHistory partialUpdatedQuizHistory = new QuizHistory();
        partialUpdatedQuizHistory.setId(quizHistory.getId());

        partialUpdatedQuizHistory
            .inputType(UPDATED_INPUT_TYPE)
            .prompt(UPDATED_PROMPT)
            .generatedResponse(UPDATED_GENERATED_RESPONSE)
            .generatedFileUrl(UPDATED_GENERATED_FILE_URL)
            .inputFileUrl(UPDATED_INPUT_FILE_URL)
            .tokenUsage(UPDATED_TOKEN_USAGE)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT);

        restQuizHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuizHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuizHistory))
            )
            .andExpect(status().isOk());

        // Validate the QuizHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuizHistoryUpdatableFieldsEquals(partialUpdatedQuizHistory, getPersistedQuizHistory(partialUpdatedQuizHistory));
    }

    @Test
    @Transactional
    void patchNonExistingQuizHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizHistory.setId(longCount.incrementAndGet());

        // Create the QuizHistory
        QuizHistoryDTO quizHistoryDTO = quizHistoryMapper.toDto(quizHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quizHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quizHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuizHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizHistory.setId(longCount.incrementAndGet());

        // Create the QuizHistory
        QuizHistoryDTO quizHistoryDTO = quizHistoryMapper.toDto(quizHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quizHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuizHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizHistory.setId(longCount.incrementAndGet());

        // Create the QuizHistory
        QuizHistoryDTO quizHistoryDTO = quizHistoryMapper.toDto(quizHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(quizHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuizHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuizHistory() throws Exception {
        // Initialize the database
        insertedQuizHistory = quizHistoryRepository.saveAndFlush(quizHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the quizHistory
        restQuizHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, quizHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return quizHistoryRepository.count();
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

    protected QuizHistory getPersistedQuizHistory(QuizHistory quizHistory) {
        return quizHistoryRepository.findById(quizHistory.getId()).orElseThrow();
    }

    protected void assertPersistedQuizHistoryToMatchAllProperties(QuizHistory expectedQuizHistory) {
        assertQuizHistoryAllPropertiesEquals(expectedQuizHistory, getPersistedQuizHistory(expectedQuizHistory));
    }

    protected void assertPersistedQuizHistoryToMatchUpdatableProperties(QuizHistory expectedQuizHistory) {
        assertQuizHistoryAllUpdatablePropertiesEquals(expectedQuizHistory, getPersistedQuizHistory(expectedQuizHistory));
    }
}
