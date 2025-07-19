package com.hybridata.revisegpt.web.rest;

import static com.hybridata.revisegpt.domain.SessionAsserts.*;
import static com.hybridata.revisegpt.web.rest.TestUtil.createUpdateProxyForBean;
import static com.hybridata.revisegpt.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybridata.revisegpt.IntegrationTest;
import com.hybridata.revisegpt.domain.Session;
import com.hybridata.revisegpt.domain.enumeration.ServiceType;
import com.hybridata.revisegpt.domain.enumeration.SessionStatus;
import com.hybridata.revisegpt.repository.SessionRepository;
import com.hybridata.revisegpt.repository.UserRepository;
import com.hybridata.revisegpt.service.SessionService;
import com.hybridata.revisegpt.service.dto.SessionDTO;
import com.hybridata.revisegpt.service.mapper.SessionMapper;
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
 * Integration tests for the {@link SessionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SessionResourceIT {

    private static final ServiceType DEFAULT_TYPE = ServiceType.CHAT;
    private static final ServiceType UPDATED_TYPE = ServiceType.COURSE;

    private static final SessionStatus DEFAULT_SESSION_STATUS = SessionStatus.ACTIVE;
    private static final SessionStatus UPDATED_SESSION_STATUS = SessionStatus.COMPLETED;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_ENDED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ENDED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepositoryMock;

    @Autowired
    private SessionMapper sessionMapper;

    @Mock
    private SessionService sessionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSessionMockMvc;

    private Session session;

    private Session insertedSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Session createEntity() {
        return new Session()
            .type(DEFAULT_TYPE)
            .sessionStatus(DEFAULT_SESSION_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .endedAt(DEFAULT_ENDED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Session createUpdatedEntity() {
        return new Session()
            .type(UPDATED_TYPE)
            .sessionStatus(UPDATED_SESSION_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .endedAt(UPDATED_ENDED_AT);
    }

    @BeforeEach
    public void initTest() {
        session = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSession != null) {
            sessionRepository.delete(insertedSession);
            insertedSession = null;
        }
    }

    @Test
    @Transactional
    void createSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Session
        SessionDTO sessionDTO = sessionMapper.toDto(session);
        var returnedSessionDTO = om.readValue(
            restSessionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sessionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SessionDTO.class
        );

        // Validate the Session in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSession = sessionMapper.toEntity(returnedSessionDTO);
        assertSessionUpdatableFieldsEquals(returnedSession, getPersistedSession(returnedSession));

        insertedSession = returnedSession;
    }

    @Test
    @Transactional
    void createSessionWithExistingId() throws Exception {
        // Create the Session with an existing ID
        session.setId(1L);
        SessionDTO sessionDTO = sessionMapper.toDto(session);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sessionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSessions() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        // Get all the sessionList
        restSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(session.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].sessionStatus").value(hasItem(DEFAULT_SESSION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))))
            .andExpect(jsonPath("$.[*].endedAt").value(hasItem(sameInstant(DEFAULT_ENDED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSessionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(sessionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sessionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSessionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sessionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sessionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSession() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        // Get the session
        restSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, session.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(session.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.sessionStatus").value(DEFAULT_SESSION_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)))
            .andExpect(jsonPath("$.endedAt").value(sameInstant(DEFAULT_ENDED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingSession() throws Exception {
        // Get the session
        restSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSession() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the session
        Session updatedSession = sessionRepository.findById(session.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSession are not directly saved in db
        em.detach(updatedSession);
        updatedSession
            .type(UPDATED_TYPE)
            .sessionStatus(UPDATED_SESSION_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .endedAt(UPDATED_ENDED_AT);
        SessionDTO sessionDTO = sessionMapper.toDto(updatedSession);

        restSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sessionDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sessionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSessionToMatchAllProperties(updatedSession);
    }

    @Test
    @Transactional
    void putNonExistingSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // Create the Session
        SessionDTO sessionDTO = sessionMapper.toDto(session);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sessionDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // Create the Session
        SessionDTO sessionDTO = sessionMapper.toDto(session);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // Create the Session
        SessionDTO sessionDTO = sessionMapper.toDto(session);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSessionWithPatch() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the session using partial update
        Session partialUpdatedSession = new Session();
        partialUpdatedSession.setId(session.getId());

        partialUpdatedSession.sessionStatus(UPDATED_SESSION_STATUS).createdAt(UPDATED_CREATED_AT);

        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSession))
            )
            .andExpect(status().isOk());

        // Validate the Session in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSessionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSession, session), getPersistedSession(session));
    }

    @Test
    @Transactional
    void fullUpdateSessionWithPatch() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the session using partial update
        Session partialUpdatedSession = new Session();
        partialUpdatedSession.setId(session.getId());

        partialUpdatedSession
            .type(UPDATED_TYPE)
            .sessionStatus(UPDATED_SESSION_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .endedAt(UPDATED_ENDED_AT);

        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSession))
            )
            .andExpect(status().isOk());

        // Validate the Session in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSessionUpdatableFieldsEquals(partialUpdatedSession, getPersistedSession(partialUpdatedSession));
    }

    @Test
    @Transactional
    void patchNonExistingSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // Create the Session
        SessionDTO sessionDTO = sessionMapper.toDto(session);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sessionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // Create the Session
        SessionDTO sessionDTO = sessionMapper.toDto(session);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // Create the Session
        SessionDTO sessionDTO = sessionMapper.toDto(session);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSession() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.saveAndFlush(session);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the session
        restSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, session.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sessionRepository.count();
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

    protected Session getPersistedSession(Session session) {
        return sessionRepository.findById(session.getId()).orElseThrow();
    }

    protected void assertPersistedSessionToMatchAllProperties(Session expectedSession) {
        assertSessionAllPropertiesEquals(expectedSession, getPersistedSession(expectedSession));
    }

    protected void assertPersistedSessionToMatchUpdatableProperties(Session expectedSession) {
        assertSessionAllUpdatablePropertiesEquals(expectedSession, getPersistedSession(expectedSession));
    }
}
