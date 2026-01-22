package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrackingRecordAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.TrackingRecordDTO;
import com.mycompany.myapp.service.mapper.TrackingRecordMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link TrackingRecordResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrackingRecordResourceIT {

    private static final LocalDate DEFAULT_CHANGE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CHANGE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tracking-records";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrackingRecordRepository trackingRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackingRecordMapper trackingRecordMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrackingRecord trackingRecord;

    private TrackingRecord insertedTrackingRecord;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrackingRecord createEntity() {
        return new TrackingRecord().changeDate(DEFAULT_CHANGE_DATE).status(DEFAULT_STATUS).comments(DEFAULT_COMMENTS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrackingRecord createUpdatedEntity() {
        return new TrackingRecord().changeDate(UPDATED_CHANGE_DATE).status(UPDATED_STATUS).comments(UPDATED_COMMENTS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrackingRecord.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        trackingRecord = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTrackingRecord != null) {
            trackingRecordRepository.delete(insertedTrackingRecord).block();
            insertedTrackingRecord = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createTrackingRecord() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TrackingRecord
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(trackingRecord);
        var returnedTrackingRecordDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrackingRecordDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrackingRecord in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrackingRecord = trackingRecordMapper.toEntity(returnedTrackingRecordDTO);
        assertTrackingRecordUpdatableFieldsEquals(returnedTrackingRecord, getPersistedTrackingRecord(returnedTrackingRecord));

        insertedTrackingRecord = returnedTrackingRecord;
    }

    @Test
    void createTrackingRecordWithExistingId() throws Exception {
        // Create the TrackingRecord with an existing ID
        trackingRecord.setId(1L);
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(trackingRecord);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrackingRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkChangeDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trackingRecord.setChangeDate(null);

        // Create the TrackingRecord, which fails.
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(trackingRecord);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trackingRecord.setStatus(null);

        // Create the TrackingRecord, which fails.
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(trackingRecord);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTrackingRecords() {
        // Initialize the database
        insertedTrackingRecord = trackingRecordRepository.save(trackingRecord).block();

        // Get all the trackingRecordList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trackingRecord.getId().intValue()))
            .jsonPath("$.[*].changeDate")
            .value(hasItem(DEFAULT_CHANGE_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].comments")
            .value(hasItem(DEFAULT_COMMENTS));
    }

    @Test
    void getTrackingRecord() {
        // Initialize the database
        insertedTrackingRecord = trackingRecordRepository.save(trackingRecord).block();

        // Get the trackingRecord
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trackingRecord.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trackingRecord.getId().intValue()))
            .jsonPath("$.changeDate")
            .value(is(DEFAULT_CHANGE_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS))
            .jsonPath("$.comments")
            .value(is(DEFAULT_COMMENTS));
    }

    @Test
    void getNonExistingTrackingRecord() {
        // Get the trackingRecord
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrackingRecord() throws Exception {
        // Initialize the database
        insertedTrackingRecord = trackingRecordRepository.save(trackingRecord).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trackingRecord
        TrackingRecord updatedTrackingRecord = trackingRecordRepository.findById(trackingRecord.getId()).block();
        updatedTrackingRecord.changeDate(UPDATED_CHANGE_DATE).status(UPDATED_STATUS).comments(UPDATED_COMMENTS);
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(updatedTrackingRecord);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trackingRecordDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrackingRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrackingRecordToMatchAllProperties(updatedTrackingRecord);
    }

    @Test
    void putNonExistingTrackingRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackingRecord.setId(longCount.incrementAndGet());

        // Create the TrackingRecord
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(trackingRecord);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trackingRecordDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrackingRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTrackingRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackingRecord.setId(longCount.incrementAndGet());

        // Create the TrackingRecord
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(trackingRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrackingRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTrackingRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackingRecord.setId(longCount.incrementAndGet());

        // Create the TrackingRecord
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(trackingRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrackingRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTrackingRecordWithPatch() throws Exception {
        // Initialize the database
        insertedTrackingRecord = trackingRecordRepository.save(trackingRecord).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trackingRecord using partial update
        TrackingRecord partialUpdatedTrackingRecord = new TrackingRecord();
        partialUpdatedTrackingRecord.setId(trackingRecord.getId());

        partialUpdatedTrackingRecord.status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrackingRecord.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrackingRecord))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrackingRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrackingRecordUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrackingRecord, trackingRecord),
            getPersistedTrackingRecord(trackingRecord)
        );
    }

    @Test
    void fullUpdateTrackingRecordWithPatch() throws Exception {
        // Initialize the database
        insertedTrackingRecord = trackingRecordRepository.save(trackingRecord).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trackingRecord using partial update
        TrackingRecord partialUpdatedTrackingRecord = new TrackingRecord();
        partialUpdatedTrackingRecord.setId(trackingRecord.getId());

        partialUpdatedTrackingRecord.changeDate(UPDATED_CHANGE_DATE).status(UPDATED_STATUS).comments(UPDATED_COMMENTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrackingRecord.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrackingRecord))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrackingRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrackingRecordUpdatableFieldsEquals(partialUpdatedTrackingRecord, getPersistedTrackingRecord(partialUpdatedTrackingRecord));
    }

    @Test
    void patchNonExistingTrackingRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackingRecord.setId(longCount.incrementAndGet());

        // Create the TrackingRecord
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(trackingRecord);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trackingRecordDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrackingRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTrackingRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackingRecord.setId(longCount.incrementAndGet());

        // Create the TrackingRecord
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(trackingRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrackingRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTrackingRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackingRecord.setId(longCount.incrementAndGet());

        // Create the TrackingRecord
        TrackingRecordDTO trackingRecordDTO = trackingRecordMapper.toDto(trackingRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trackingRecordDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrackingRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTrackingRecord() {
        // Initialize the database
        insertedTrackingRecord = trackingRecordRepository.save(trackingRecord).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the trackingRecord
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trackingRecord.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return trackingRecordRepository.count().block();
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

    protected TrackingRecord getPersistedTrackingRecord(TrackingRecord trackingRecord) {
        return trackingRecordRepository.findById(trackingRecord.getId()).block();
    }

    protected void assertPersistedTrackingRecordToMatchAllProperties(TrackingRecord expectedTrackingRecord) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrackingRecordAllPropertiesEquals(expectedTrackingRecord, getPersistedTrackingRecord(expectedTrackingRecord));
        assertTrackingRecordUpdatableFieldsEquals(expectedTrackingRecord, getPersistedTrackingRecord(expectedTrackingRecord));
    }

    protected void assertPersistedTrackingRecordToMatchUpdatableProperties(TrackingRecord expectedTrackingRecord) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrackingRecordAllUpdatablePropertiesEquals(expectedTrackingRecord, getPersistedTrackingRecord(expectedTrackingRecord));
        assertTrackingRecordUpdatableFieldsEquals(expectedTrackingRecord, getPersistedTrackingRecord(expectedTrackingRecord));
    }
}
