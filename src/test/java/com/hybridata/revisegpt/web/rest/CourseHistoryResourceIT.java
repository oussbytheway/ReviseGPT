package com.hybridata.revisegpt.web.rest;

import static com.hybridata.revisegpt.domain.CourseHistoryAsserts.*;
import static com.hybridata.revisegpt.web.rest.TestUtil.createUpdateProxyForBean;
import static com.hybridata.revisegpt.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybridata.revisegpt.IntegrationTest;
import com.hybridata.revisegpt.domain.CourseHistory;
import com.hybridata.revisegpt.repository.CourseHistoryRepository;
import com.hybridata.revisegpt.repository.UserRepository;
import com.hybridata.revisegpt.service.CourseHistoryService;
import com.hybridata.revisegpt.service.dto.CourseHistoryDTO;
import com.hybridata.revisegpt.service.mapper.CourseHistoryMapper;
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
 * Integration tests for the {@link CourseHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseHistoryResourceIT {

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

    private static final String ENTITY_API_URL = "/api/course-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseHistoryRepository courseHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CourseHistoryRepository courseHistoryRepositoryMock;

    @Autowired
    private CourseHistoryMapper courseHistoryMapper;

    @Mock
    private CourseHistoryService courseHistoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseHistoryMockMvc;

    private CourseHistory courseHistory;

    private CourseHistory insertedCourseHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseHistory createEntity() {
        return new CourseHistory()
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
    public static CourseHistory createUpdatedEntity() {
        return new CourseHistory()
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
        courseHistory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCourseHistory != null) {
            courseHistoryRepository.delete(insertedCourseHistory);
            insertedCourseHistory = null;
        }
    }

    @Test
    @Transactional
    void createCourseHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CourseHistory
        CourseHistoryDTO courseHistoryDTO = courseHistoryMapper.toDto(courseHistory);
        var returnedCourseHistoryDTO = om.readValue(
            restCourseHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseHistoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseHistoryDTO.class
        );

        // Validate the CourseHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCourseHistory = courseHistoryMapper.toEntity(returnedCourseHistoryDTO);
        assertCourseHistoryUpdatableFieldsEquals(returnedCourseHistory, getPersistedCourseHistory(returnedCourseHistory));

        insertedCourseHistory = returnedCourseHistory;
    }

    @Test
    @Transactional
    void createCourseHistoryWithExistingId() throws Exception {
        // Create the CourseHistory with an existing ID
        courseHistory.setId(1L);
        CourseHistoryDTO courseHistoryDTO = courseHistoryMapper.toDto(courseHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CourseHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCourseHistories() throws Exception {
        // Initialize the database
        insertedCourseHistory = courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList
        restCourseHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].generatedResponse").value(hasItem(DEFAULT_GENERATED_RESPONSE)))
            .andExpect(jsonPath("$.[*].generatedFileUrl").value(hasItem(DEFAULT_GENERATED_FILE_URL)))
            .andExpect(jsonPath("$.[*].inputFileUrl").value(hasItem(DEFAULT_INPUT_FILE_URL)))
            .andExpect(jsonPath("$.[*].tokenUsage").value(hasItem(DEFAULT_TOKEN_USAGE)))
            .andExpect(jsonPath("$.[*].responseTimeMs").value(hasItem(DEFAULT_RESPONSE_TIME_MS)))
            .andExpect(jsonPath("$.[*].feedbackRating").value(hasItem(DEFAULT_FEEDBACK_RATING)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseHistoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseHistoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseHistoryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourseHistory() throws Exception {
        // Initialize the database
        insertedCourseHistory = courseHistoryRepository.saveAndFlush(courseHistory);

        // Get the courseHistory
        restCourseHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, courseHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseHistory.getId().intValue()))
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
    void getNonExistingCourseHistory() throws Exception {
        // Get the courseHistory
        restCourseHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourseHistory() throws Exception {
        // Initialize the database
        insertedCourseHistory = courseHistoryRepository.saveAndFlush(courseHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseHistory
        CourseHistory updatedCourseHistory = courseHistoryRepository.findById(courseHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourseHistory are not directly saved in db
        em.detach(updatedCourseHistory);
        updatedCourseHistory
            .generatedResponse(UPDATED_GENERATED_RESPONSE)
            .generatedFileUrl(UPDATED_GENERATED_FILE_URL)
            .inputFileUrl(UPDATED_INPUT_FILE_URL)
            .tokenUsage(UPDATED_TOKEN_USAGE)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT);
        CourseHistoryDTO courseHistoryDTO = courseHistoryMapper.toDto(updatedCourseHistory);

        restCourseHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the CourseHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseHistoryToMatchAllProperties(updatedCourseHistory);
    }

    @Test
    @Transactional
    void putNonExistingCourseHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseHistory.setId(longCount.incrementAndGet());

        // Create the CourseHistory
        CourseHistoryDTO courseHistoryDTO = courseHistoryMapper.toDto(courseHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourseHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseHistory.setId(longCount.incrementAndGet());

        // Create the CourseHistory
        CourseHistoryDTO courseHistoryDTO = courseHistoryMapper.toDto(courseHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourseHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseHistory.setId(longCount.incrementAndGet());

        // Create the CourseHistory
        CourseHistoryDTO courseHistoryDTO = courseHistoryMapper.toDto(courseHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedCourseHistory = courseHistoryRepository.saveAndFlush(courseHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseHistory using partial update
        CourseHistory partialUpdatedCourseHistory = new CourseHistory();
        partialUpdatedCourseHistory.setId(courseHistory.getId());

        partialUpdatedCourseHistory
            .generatedFileUrl(UPDATED_GENERATED_FILE_URL)
            .tokenUsage(UPDATED_TOKEN_USAGE)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .feedbackRating(UPDATED_FEEDBACK_RATING);

        restCourseHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseHistory))
            )
            .andExpect(status().isOk());

        // Validate the CourseHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCourseHistory, courseHistory),
            getPersistedCourseHistory(courseHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateCourseHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedCourseHistory = courseHistoryRepository.saveAndFlush(courseHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseHistory using partial update
        CourseHistory partialUpdatedCourseHistory = new CourseHistory();
        partialUpdatedCourseHistory.setId(courseHistory.getId());

        partialUpdatedCourseHistory
            .generatedResponse(UPDATED_GENERATED_RESPONSE)
            .generatedFileUrl(UPDATED_GENERATED_FILE_URL)
            .inputFileUrl(UPDATED_INPUT_FILE_URL)
            .tokenUsage(UPDATED_TOKEN_USAGE)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT);

        restCourseHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseHistory))
            )
            .andExpect(status().isOk());

        // Validate the CourseHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseHistoryUpdatableFieldsEquals(partialUpdatedCourseHistory, getPersistedCourseHistory(partialUpdatedCourseHistory));
    }

    @Test
    @Transactional
    void patchNonExistingCourseHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseHistory.setId(longCount.incrementAndGet());

        // Create the CourseHistory
        CourseHistoryDTO courseHistoryDTO = courseHistoryMapper.toDto(courseHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourseHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseHistory.setId(longCount.incrementAndGet());

        // Create the CourseHistory
        CourseHistoryDTO courseHistoryDTO = courseHistoryMapper.toDto(courseHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourseHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseHistory.setId(longCount.incrementAndGet());

        // Create the CourseHistory
        CourseHistoryDTO courseHistoryDTO = courseHistoryMapper.toDto(courseHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourseHistory() throws Exception {
        // Initialize the database
        insertedCourseHistory = courseHistoryRepository.saveAndFlush(courseHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the courseHistory
        restCourseHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, courseHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseHistoryRepository.count();
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

    protected CourseHistory getPersistedCourseHistory(CourseHistory courseHistory) {
        return courseHistoryRepository.findById(courseHistory.getId()).orElseThrow();
    }

    protected void assertPersistedCourseHistoryToMatchAllProperties(CourseHistory expectedCourseHistory) {
        assertCourseHistoryAllPropertiesEquals(expectedCourseHistory, getPersistedCourseHistory(expectedCourseHistory));
    }

    protected void assertPersistedCourseHistoryToMatchUpdatableProperties(CourseHistory expectedCourseHistory) {
        assertCourseHistoryAllUpdatablePropertiesEquals(expectedCourseHistory, getPersistedCourseHistory(expectedCourseHistory));
    }
}
