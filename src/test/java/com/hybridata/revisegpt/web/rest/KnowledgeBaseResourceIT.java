package com.hybridata.revisegpt.web.rest;

import static com.hybridata.revisegpt.domain.KnowledgeBaseAsserts.*;
import static com.hybridata.revisegpt.web.rest.TestUtil.createUpdateProxyForBean;
import static com.hybridata.revisegpt.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybridata.revisegpt.IntegrationTest;
import com.hybridata.revisegpt.domain.KnowledgeBase;
import com.hybridata.revisegpt.repository.KnowledgeBaseRepository;
import com.hybridata.revisegpt.repository.UserRepository;
import com.hybridata.revisegpt.service.KnowledgeBaseService;
import com.hybridata.revisegpt.service.dto.KnowledgeBaseDTO;
import com.hybridata.revisegpt.service.mapper.KnowledgeBaseMapper;
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
 * Integration tests for the {@link KnowledgeBaseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class KnowledgeBaseResourceIT {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_EMBED = false;
    private static final Boolean UPDATED_EMBED = true;

    private static final String DEFAULT_VECTOR_ID = "AAAAAAAAAA";
    private static final String UPDATED_VECTOR_ID = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_FILE_URL = "AAAAAAAAAA";
    private static final String UPDATED_FILE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/knowledge-bases";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private KnowledgeBaseRepository knowledgeBaseRepositoryMock;

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Mock
    private KnowledgeBaseService knowledgeBaseServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKnowledgeBaseMockMvc;

    private KnowledgeBase knowledgeBase;

    private KnowledgeBase insertedKnowledgeBase;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeBase createEntity() {
        return new KnowledgeBase()
            .fileName(DEFAULT_FILE_NAME)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .embed(DEFAULT_EMBED)
            .vectorId(DEFAULT_VECTOR_ID)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .fileUrl(DEFAULT_FILE_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeBase createUpdatedEntity() {
        return new KnowledgeBase()
            .fileName(UPDATED_FILE_NAME)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .embed(UPDATED_EMBED)
            .vectorId(UPDATED_VECTOR_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .fileUrl(UPDATED_FILE_URL);
    }

    @BeforeEach
    public void initTest() {
        knowledgeBase = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedKnowledgeBase != null) {
            knowledgeBaseRepository.delete(insertedKnowledgeBase);
            insertedKnowledgeBase = null;
        }
    }

    @Test
    @Transactional
    void createKnowledgeBase() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the KnowledgeBase
        KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseMapper.toDto(knowledgeBase);
        var returnedKnowledgeBaseDTO = om.readValue(
            restKnowledgeBaseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeBaseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            KnowledgeBaseDTO.class
        );

        // Validate the KnowledgeBase in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedKnowledgeBase = knowledgeBaseMapper.toEntity(returnedKnowledgeBaseDTO);
        assertKnowledgeBaseUpdatableFieldsEquals(returnedKnowledgeBase, getPersistedKnowledgeBase(returnedKnowledgeBase));

        insertedKnowledgeBase = returnedKnowledgeBase;
    }

    @Test
    @Transactional
    void createKnowledgeBaseWithExistingId() throws Exception {
        // Create the KnowledgeBase with an existing ID
        knowledgeBase.setId(1L);
        KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseMapper.toDto(knowledgeBase);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restKnowledgeBaseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeBaseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeBase in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllKnowledgeBases() throws Exception {
        // Initialize the database
        insertedKnowledgeBase = knowledgeBaseRepository.saveAndFlush(knowledgeBase);

        // Get all the knowledgeBaseList
        restKnowledgeBaseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(knowledgeBase.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].embed").value(hasItem(DEFAULT_EMBED.booleanValue())))
            .andExpect(jsonPath("$.[*].vectorId").value(hasItem(DEFAULT_VECTOR_ID)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))))
            .andExpect(jsonPath("$.[*].fileUrl").value(hasItem(DEFAULT_FILE_URL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKnowledgeBasesWithEagerRelationshipsIsEnabled() throws Exception {
        when(knowledgeBaseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restKnowledgeBaseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(knowledgeBaseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKnowledgeBasesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(knowledgeBaseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restKnowledgeBaseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(knowledgeBaseRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getKnowledgeBase() throws Exception {
        // Initialize the database
        insertedKnowledgeBase = knowledgeBaseRepository.saveAndFlush(knowledgeBase);

        // Get the knowledgeBase
        restKnowledgeBaseMockMvc
            .perform(get(ENTITY_API_URL_ID, knowledgeBase.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(knowledgeBase.getId().intValue()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.embed").value(DEFAULT_EMBED.booleanValue()))
            .andExpect(jsonPath("$.vectorId").value(DEFAULT_VECTOR_ID))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)))
            .andExpect(jsonPath("$.fileUrl").value(DEFAULT_FILE_URL));
    }

    @Test
    @Transactional
    void getNonExistingKnowledgeBase() throws Exception {
        // Get the knowledgeBase
        restKnowledgeBaseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingKnowledgeBase() throws Exception {
        // Initialize the database
        insertedKnowledgeBase = knowledgeBaseRepository.saveAndFlush(knowledgeBase);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeBase
        KnowledgeBase updatedKnowledgeBase = knowledgeBaseRepository.findById(knowledgeBase.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedKnowledgeBase are not directly saved in db
        em.detach(updatedKnowledgeBase);
        updatedKnowledgeBase
            .fileName(UPDATED_FILE_NAME)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .embed(UPDATED_EMBED)
            .vectorId(UPDATED_VECTOR_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .fileUrl(UPDATED_FILE_URL);
        KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseMapper.toDto(updatedKnowledgeBase);

        restKnowledgeBaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, knowledgeBaseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeBaseDTO))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeBase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKnowledgeBaseToMatchAllProperties(updatedKnowledgeBase);
    }

    @Test
    @Transactional
    void putNonExistingKnowledgeBase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeBase.setId(longCount.incrementAndGet());

        // Create the KnowledgeBase
        KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseMapper.toDto(knowledgeBase);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKnowledgeBaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, knowledgeBaseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeBaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeBase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchKnowledgeBase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeBase.setId(longCount.incrementAndGet());

        // Create the KnowledgeBase
        KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseMapper.toDto(knowledgeBase);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeBaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(knowledgeBaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeBase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKnowledgeBase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeBase.setId(longCount.incrementAndGet());

        // Create the KnowledgeBase
        KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseMapper.toDto(knowledgeBase);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeBaseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(knowledgeBaseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KnowledgeBase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateKnowledgeBaseWithPatch() throws Exception {
        // Initialize the database
        insertedKnowledgeBase = knowledgeBaseRepository.saveAndFlush(knowledgeBase);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeBase using partial update
        KnowledgeBase partialUpdatedKnowledgeBase = new KnowledgeBase();
        partialUpdatedKnowledgeBase.setId(knowledgeBase.getId());

        partialUpdatedKnowledgeBase.title(UPDATED_TITLE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restKnowledgeBaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKnowledgeBase.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKnowledgeBase))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeBase in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKnowledgeBaseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedKnowledgeBase, knowledgeBase),
            getPersistedKnowledgeBase(knowledgeBase)
        );
    }

    @Test
    @Transactional
    void fullUpdateKnowledgeBaseWithPatch() throws Exception {
        // Initialize the database
        insertedKnowledgeBase = knowledgeBaseRepository.saveAndFlush(knowledgeBase);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the knowledgeBase using partial update
        KnowledgeBase partialUpdatedKnowledgeBase = new KnowledgeBase();
        partialUpdatedKnowledgeBase.setId(knowledgeBase.getId());

        partialUpdatedKnowledgeBase
            .fileName(UPDATED_FILE_NAME)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .embed(UPDATED_EMBED)
            .vectorId(UPDATED_VECTOR_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .fileUrl(UPDATED_FILE_URL);

        restKnowledgeBaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKnowledgeBase.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKnowledgeBase))
            )
            .andExpect(status().isOk());

        // Validate the KnowledgeBase in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKnowledgeBaseUpdatableFieldsEquals(partialUpdatedKnowledgeBase, getPersistedKnowledgeBase(partialUpdatedKnowledgeBase));
    }

    @Test
    @Transactional
    void patchNonExistingKnowledgeBase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeBase.setId(longCount.incrementAndGet());

        // Create the KnowledgeBase
        KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseMapper.toDto(knowledgeBase);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKnowledgeBaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, knowledgeBaseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(knowledgeBaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeBase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKnowledgeBase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeBase.setId(longCount.incrementAndGet());

        // Create the KnowledgeBase
        KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseMapper.toDto(knowledgeBase);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeBaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(knowledgeBaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KnowledgeBase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKnowledgeBase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        knowledgeBase.setId(longCount.incrementAndGet());

        // Create the KnowledgeBase
        KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseMapper.toDto(knowledgeBase);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKnowledgeBaseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(knowledgeBaseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KnowledgeBase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteKnowledgeBase() throws Exception {
        // Initialize the database
        insertedKnowledgeBase = knowledgeBaseRepository.saveAndFlush(knowledgeBase);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the knowledgeBase
        restKnowledgeBaseMockMvc
            .perform(delete(ENTITY_API_URL_ID, knowledgeBase.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return knowledgeBaseRepository.count();
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

    protected KnowledgeBase getPersistedKnowledgeBase(KnowledgeBase knowledgeBase) {
        return knowledgeBaseRepository.findById(knowledgeBase.getId()).orElseThrow();
    }

    protected void assertPersistedKnowledgeBaseToMatchAllProperties(KnowledgeBase expectedKnowledgeBase) {
        assertKnowledgeBaseAllPropertiesEquals(expectedKnowledgeBase, getPersistedKnowledgeBase(expectedKnowledgeBase));
    }

    protected void assertPersistedKnowledgeBaseToMatchUpdatableProperties(KnowledgeBase expectedKnowledgeBase) {
        assertKnowledgeBaseAllUpdatablePropertiesEquals(expectedKnowledgeBase, getPersistedKnowledgeBase(expectedKnowledgeBase));
    }
}
