package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ResponsibleAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Responsible;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ResponsibleRepository;
import com.mycompany.myapp.service.dto.ResponsibleDTO;
import com.mycompany.myapp.service.mapper.ResponsibleMapper;
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
 * Integration tests for the {@link ResponsibleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ResponsibleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_POSITION = "AAAAAAAAAA";
    private static final String UPDATED_POSITION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/responsibles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ResponsibleRepository responsibleRepository;

    @Autowired
    private ResponsibleMapper responsibleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Responsible responsible;

    private Responsible insertedResponsible;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Responsible createEntity() {
        return new Responsible().name(DEFAULT_NAME).position(DEFAULT_POSITION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Responsible createUpdatedEntity() {
        return new Responsible().name(UPDATED_NAME).position(UPDATED_POSITION);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Responsible.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        responsible = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedResponsible != null) {
            responsibleRepository.delete(insertedResponsible).block();
            insertedResponsible = null;
        }
        deleteEntities(em);
    }

    @Test
    void createResponsible() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Responsible
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(responsible);
        var returnedResponsibleDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ResponsibleDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Responsible in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedResponsible = responsibleMapper.toEntity(returnedResponsibleDTO);
        assertResponsibleUpdatableFieldsEquals(returnedResponsible, getPersistedResponsible(returnedResponsible));

        insertedResponsible = returnedResponsible;
    }

    @Test
    void createResponsibleWithExistingId() throws Exception {
        // Create the Responsible with an existing ID
        responsible.setId(1L);
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(responsible);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Responsible in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        responsible.setName(null);

        // Create the Responsible, which fails.
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(responsible);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPositionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        responsible.setPosition(null);

        // Create the Responsible, which fails.
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(responsible);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllResponsibles() {
        // Initialize the database
        insertedResponsible = responsibleRepository.save(responsible).block();

        // Get all the responsibleList
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
            .value(hasItem(responsible.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].position")
            .value(hasItem(DEFAULT_POSITION));
    }

    @Test
    void getResponsible() {
        // Initialize the database
        insertedResponsible = responsibleRepository.save(responsible).block();

        // Get the responsible
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, responsible.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(responsible.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.position")
            .value(is(DEFAULT_POSITION));
    }

    @Test
    void getNonExistingResponsible() {
        // Get the responsible
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingResponsible() throws Exception {
        // Initialize the database
        insertedResponsible = responsibleRepository.save(responsible).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the responsible
        Responsible updatedResponsible = responsibleRepository.findById(responsible.getId()).block();
        updatedResponsible.name(UPDATED_NAME).position(UPDATED_POSITION);
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(updatedResponsible);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, responsibleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Responsible in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedResponsibleToMatchAllProperties(updatedResponsible);
    }

    @Test
    void putNonExistingResponsible() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        responsible.setId(longCount.incrementAndGet());

        // Create the Responsible
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(responsible);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, responsibleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Responsible in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchResponsible() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        responsible.setId(longCount.incrementAndGet());

        // Create the Responsible
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(responsible);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Responsible in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamResponsible() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        responsible.setId(longCount.incrementAndGet());

        // Create the Responsible
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(responsible);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Responsible in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateResponsibleWithPatch() throws Exception {
        // Initialize the database
        insertedResponsible = responsibleRepository.save(responsible).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the responsible using partial update
        Responsible partialUpdatedResponsible = new Responsible();
        partialUpdatedResponsible.setId(responsible.getId());

        partialUpdatedResponsible.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResponsible.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedResponsible))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Responsible in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertResponsibleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedResponsible, responsible),
            getPersistedResponsible(responsible)
        );
    }

    @Test
    void fullUpdateResponsibleWithPatch() throws Exception {
        // Initialize the database
        insertedResponsible = responsibleRepository.save(responsible).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the responsible using partial update
        Responsible partialUpdatedResponsible = new Responsible();
        partialUpdatedResponsible.setId(responsible.getId());

        partialUpdatedResponsible.name(UPDATED_NAME).position(UPDATED_POSITION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResponsible.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedResponsible))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Responsible in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertResponsibleUpdatableFieldsEquals(partialUpdatedResponsible, getPersistedResponsible(partialUpdatedResponsible));
    }

    @Test
    void patchNonExistingResponsible() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        responsible.setId(longCount.incrementAndGet());

        // Create the Responsible
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(responsible);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, responsibleDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Responsible in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchResponsible() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        responsible.setId(longCount.incrementAndGet());

        // Create the Responsible
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(responsible);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Responsible in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamResponsible() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        responsible.setId(longCount.incrementAndGet());

        // Create the Responsible
        ResponsibleDTO responsibleDTO = responsibleMapper.toDto(responsible);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(responsibleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Responsible in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteResponsible() {
        // Initialize the database
        insertedResponsible = responsibleRepository.save(responsible).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the responsible
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, responsible.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return responsibleRepository.count().block();
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

    protected Responsible getPersistedResponsible(Responsible responsible) {
        return responsibleRepository.findById(responsible.getId()).block();
    }

    protected void assertPersistedResponsibleToMatchAllProperties(Responsible expectedResponsible) {
        // Test fails because reactive api returns an empty object instead of null
        // assertResponsibleAllPropertiesEquals(expectedResponsible, getPersistedResponsible(expectedResponsible));
        assertResponsibleUpdatableFieldsEquals(expectedResponsible, getPersistedResponsible(expectedResponsible));
    }

    protected void assertPersistedResponsibleToMatchUpdatableProperties(Responsible expectedResponsible) {
        // Test fails because reactive api returns an empty object instead of null
        // assertResponsibleAllUpdatablePropertiesEquals(expectedResponsible, getPersistedResponsible(expectedResponsible));
        assertResponsibleUpdatableFieldsEquals(expectedResponsible, getPersistedResponsible(expectedResponsible));
    }
}
