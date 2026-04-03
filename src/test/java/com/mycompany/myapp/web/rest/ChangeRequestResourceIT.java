package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ChangeRequestAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.enumeration.Impacto;
import com.mycompany.myapp.domain.enumeration.prioridad;
import com.mycompany.myapp.repository.ChangeRequestRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.service.mapper.ChangeRequestMapper;
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
 * Integration tests for the {@link ChangeRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ChangeRequestResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final prioridad DEFAULT_PRIORITY = prioridad.ALTA;
    private static final prioridad UPDATED_PRIORITY = prioridad.MEDIA;

    private static final Impacto DEFAULT_IMPACT = Impacto.ALTO;
    private static final Impacto UPDATED_IMPACT = Impacto.MEDIO;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FECHA_ENTREGA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_ENTREGA = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_OBSERVACIONES = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVACIONES = "BBBBBBBBBB";

    private static final String DEFAULT_SOLICITANTE = "AAAAAAAAAA";
    private static final String UPDATED_SOLICITANTE = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTAMENTO = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTAMENTO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/change-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChangeRequestRepository changeRequestRepository;

    @Autowired
    private ChangeRequestMapper changeRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ChangeRequest changeRequest;

    private ChangeRequest insertedChangeRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChangeRequest createEntity() {
        return new ChangeRequest()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .createdDate(DEFAULT_CREATED_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .priority(DEFAULT_PRIORITY)
            .impact(DEFAULT_IMPACT)
            .status(DEFAULT_STATUS)
            .fechaEntrega(DEFAULT_FECHA_ENTREGA)
            .observaciones(DEFAULT_OBSERVACIONES)
            .solicitante(DEFAULT_SOLICITANTE)
            .departamento(DEFAULT_DEPARTAMENTO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChangeRequest createUpdatedEntity() {
        return new ChangeRequest()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .priority(UPDATED_PRIORITY)
            .impact(UPDATED_IMPACT)
            .status(UPDATED_STATUS)
            .fechaEntrega(UPDATED_FECHA_ENTREGA)
            .observaciones(UPDATED_OBSERVACIONES)
            .solicitante(UPDATED_SOLICITANTE)
            .departamento(UPDATED_DEPARTAMENTO);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ChangeRequest.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        changeRequest = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedChangeRequest != null) {
            changeRequestRepository.delete(insertedChangeRequest).block();
            insertedChangeRequest = null;
        }
        deleteEntities(em);
    }

    @Test
    void createChangeRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChangeRequest
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);
        var returnedChangeRequestDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ChangeRequestDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ChangeRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChangeRequest = changeRequestMapper.toEntity(returnedChangeRequestDTO);
        assertChangeRequestUpdatableFieldsEquals(returnedChangeRequest, getPersistedChangeRequest(returnedChangeRequest));

        insertedChangeRequest = returnedChangeRequest;
    }

    @Test
    void createChangeRequestWithExistingId() throws Exception {
        // Create the ChangeRequest with an existing ID
        changeRequest.setId(1L);
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChangeRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        changeRequest.setTitle(null);

        // Create the ChangeRequest, which fails.
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        changeRequest.setDescription(null);

        // Create the ChangeRequest, which fails.
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        changeRequest.setCreatedDate(null);

        // Create the ChangeRequest, which fails.
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        changeRequest.setStatus(null);

        // Create the ChangeRequest, which fails.
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllChangeRequests() {
        // Initialize the database
        insertedChangeRequest = changeRequestRepository.save(changeRequest).block();

        // Get all the changeRequestList
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
            .value(hasItem(changeRequest.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updatedDate")
            .value(hasItem(DEFAULT_UPDATED_DATE.toString()))
            .jsonPath("$.[*].priority")
            .value(hasItem(DEFAULT_PRIORITY.toString()))
            .jsonPath("$.[*].impact")
            .value(hasItem(DEFAULT_IMPACT.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].fechaEntrega")
            .value(hasItem(DEFAULT_FECHA_ENTREGA.toString()))
            .jsonPath("$.[*].observaciones")
            .value(hasItem(DEFAULT_OBSERVACIONES))
            .jsonPath("$.[*].solicitante")
            .value(hasItem(DEFAULT_SOLICITANTE))
            .jsonPath("$.[*].departamento")
            .value(hasItem(DEFAULT_DEPARTAMENTO));
    }

    @Test
    void getChangeRequest() {
        // Initialize the database
        insertedChangeRequest = changeRequestRepository.save(changeRequest).block();

        // Get the changeRequest
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, changeRequest.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(changeRequest.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.updatedDate")
            .value(is(DEFAULT_UPDATED_DATE.toString()))
            .jsonPath("$.priority")
            .value(is(DEFAULT_PRIORITY.toString()))
            .jsonPath("$.impact")
            .value(is(DEFAULT_IMPACT.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS))
            .jsonPath("$.fechaEntrega")
            .value(is(DEFAULT_FECHA_ENTREGA.toString()))
            .jsonPath("$.observaciones")
            .value(is(DEFAULT_OBSERVACIONES))
            .jsonPath("$.solicitante")
            .value(is(DEFAULT_SOLICITANTE))
            .jsonPath("$.departamento")
            .value(is(DEFAULT_DEPARTAMENTO));
    }

    @Test
    void getNonExistingChangeRequest() {
        // Get the changeRequest
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingChangeRequest() throws Exception {
        // Initialize the database
        insertedChangeRequest = changeRequestRepository.save(changeRequest).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the changeRequest
        ChangeRequest updatedChangeRequest = changeRequestRepository.findById(changeRequest.getId()).block();
        updatedChangeRequest
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .priority(UPDATED_PRIORITY)
            .impact(UPDATED_IMPACT)
            .status(UPDATED_STATUS)
            .fechaEntrega(UPDATED_FECHA_ENTREGA)
            .observaciones(UPDATED_OBSERVACIONES)
            .solicitante(UPDATED_SOLICITANTE)
            .departamento(UPDATED_DEPARTAMENTO);
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(updatedChangeRequest);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, changeRequestDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ChangeRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChangeRequestToMatchAllProperties(updatedChangeRequest);
    }

    @Test
    void putNonExistingChangeRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        changeRequest.setId(longCount.incrementAndGet());

        // Create the ChangeRequest
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, changeRequestDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChangeRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchChangeRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        changeRequest.setId(longCount.incrementAndGet());

        // Create the ChangeRequest
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChangeRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamChangeRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        changeRequest.setId(longCount.incrementAndGet());

        // Create the ChangeRequest
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ChangeRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateChangeRequestWithPatch() throws Exception {
        // Initialize the database
        insertedChangeRequest = changeRequestRepository.save(changeRequest).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the changeRequest using partial update
        ChangeRequest partialUpdatedChangeRequest = new ChangeRequest();
        partialUpdatedChangeRequest.setId(changeRequest.getId());

        partialUpdatedChangeRequest
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .fechaEntrega(UPDATED_FECHA_ENTREGA)
            .observaciones(UPDATED_OBSERVACIONES)
            .solicitante(UPDATED_SOLICITANTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChangeRequest.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedChangeRequest))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ChangeRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChangeRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChangeRequest, changeRequest),
            getPersistedChangeRequest(changeRequest)
        );
    }

    @Test
    void fullUpdateChangeRequestWithPatch() throws Exception {
        // Initialize the database
        insertedChangeRequest = changeRequestRepository.save(changeRequest).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the changeRequest using partial update
        ChangeRequest partialUpdatedChangeRequest = new ChangeRequest();
        partialUpdatedChangeRequest.setId(changeRequest.getId());

        partialUpdatedChangeRequest
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .priority(UPDATED_PRIORITY)
            .impact(UPDATED_IMPACT)
            .status(UPDATED_STATUS)
            .fechaEntrega(UPDATED_FECHA_ENTREGA)
            .observaciones(UPDATED_OBSERVACIONES)
            .solicitante(UPDATED_SOLICITANTE)
            .departamento(UPDATED_DEPARTAMENTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChangeRequest.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedChangeRequest))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ChangeRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChangeRequestUpdatableFieldsEquals(partialUpdatedChangeRequest, getPersistedChangeRequest(partialUpdatedChangeRequest));
    }

    @Test
    void patchNonExistingChangeRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        changeRequest.setId(longCount.incrementAndGet());

        // Create the ChangeRequest
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, changeRequestDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChangeRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchChangeRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        changeRequest.setId(longCount.incrementAndGet());

        // Create the ChangeRequest
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChangeRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamChangeRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        changeRequest.setId(longCount.incrementAndGet());

        // Create the ChangeRequest
        ChangeRequestDTO changeRequestDTO = changeRequestMapper.toDto(changeRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(changeRequestDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ChangeRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteChangeRequest() {
        // Initialize the database
        insertedChangeRequest = changeRequestRepository.save(changeRequest).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the changeRequest
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, changeRequest.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return changeRequestRepository.count().block();
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

    protected ChangeRequest getPersistedChangeRequest(ChangeRequest changeRequest) {
        return changeRequestRepository.findById(changeRequest.getId()).block();
    }

    protected void assertPersistedChangeRequestToMatchAllProperties(ChangeRequest expectedChangeRequest) {
        // Test fails because reactive api returns an empty object instead of null
        // assertChangeRequestAllPropertiesEquals(expectedChangeRequest, getPersistedChangeRequest(expectedChangeRequest));
        assertChangeRequestUpdatableFieldsEquals(expectedChangeRequest, getPersistedChangeRequest(expectedChangeRequest));
    }

    protected void assertPersistedChangeRequestToMatchUpdatableProperties(ChangeRequest expectedChangeRequest) {
        // Test fails because reactive api returns an empty object instead of null
        // assertChangeRequestAllUpdatablePropertiesEquals(expectedChangeRequest, getPersistedChangeRequest(expectedChangeRequest));
        assertChangeRequestUpdatableFieldsEquals(expectedChangeRequest, getPersistedChangeRequest(expectedChangeRequest));
    }
}
