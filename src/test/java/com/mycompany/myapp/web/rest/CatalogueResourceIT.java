package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.CatalogueAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Catalogue;
import com.mycompany.myapp.repository.CatalogueRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.service.dto.CatalogueDTO;
import com.mycompany.myapp.service.mapper.CatalogueMapper;
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
 * Integration tests for the {@link CatalogueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CatalogueResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/catalogues";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CatalogueRepository catalogueRepository;

    @Autowired
    private CatalogueMapper catalogueMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Catalogue catalogue;

    private Catalogue insertedCatalogue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Catalogue createEntity() {
        return new Catalogue().name(DEFAULT_NAME).code(DEFAULT_CODE).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Catalogue createUpdatedEntity() {
        return new Catalogue().name(UPDATED_NAME).code(UPDATED_CODE).status(UPDATED_STATUS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Catalogue.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        catalogue = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCatalogue != null) {
            catalogueRepository.delete(insertedCatalogue).block();
            insertedCatalogue = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCatalogue() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Catalogue
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(catalogue);
        var returnedCatalogueDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CatalogueDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Catalogue in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCatalogue = catalogueMapper.toEntity(returnedCatalogueDTO);
        assertCatalogueUpdatableFieldsEquals(returnedCatalogue, getPersistedCatalogue(returnedCatalogue));

        insertedCatalogue = returnedCatalogue;
    }

    @Test
    void createCatalogueWithExistingId() throws Exception {
        // Create the Catalogue with an existing ID
        catalogue.setId(1L);
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(catalogue);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Catalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        catalogue.setName(null);

        // Create the Catalogue, which fails.
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(catalogue);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        catalogue.setCode(null);

        // Create the Catalogue, which fails.
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(catalogue);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCatalogues() {
        // Initialize the database
        insertedCatalogue = catalogueRepository.save(catalogue).block();

        // Get all the catalogueList
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
            .value(hasItem(catalogue.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS));
    }

    @Test
    void getCatalogue() {
        // Initialize the database
        insertedCatalogue = catalogueRepository.save(catalogue).block();

        // Get the catalogue
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, catalogue.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(catalogue.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS));
    }

    @Test
    void getNonExistingCatalogue() {
        // Get the catalogue
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCatalogue() throws Exception {
        // Initialize the database
        insertedCatalogue = catalogueRepository.save(catalogue).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the catalogue
        Catalogue updatedCatalogue = catalogueRepository.findById(catalogue.getId()).block();
        updatedCatalogue.name(UPDATED_NAME).code(UPDATED_CODE).status(UPDATED_STATUS);
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(updatedCatalogue);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, catalogueDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Catalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCatalogueToMatchAllProperties(updatedCatalogue);
    }

    @Test
    void putNonExistingCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogue.setId(longCount.incrementAndGet());

        // Create the Catalogue
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(catalogue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, catalogueDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Catalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogue.setId(longCount.incrementAndGet());

        // Create the Catalogue
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(catalogue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Catalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogue.setId(longCount.incrementAndGet());

        // Create the Catalogue
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(catalogue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Catalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCatalogueWithPatch() throws Exception {
        // Initialize the database
        insertedCatalogue = catalogueRepository.save(catalogue).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the catalogue using partial update
        Catalogue partialUpdatedCatalogue = new Catalogue();
        partialUpdatedCatalogue.setId(catalogue.getId());

        partialUpdatedCatalogue.code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCatalogue.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCatalogue))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Catalogue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCatalogueUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCatalogue, catalogue),
            getPersistedCatalogue(catalogue)
        );
    }

    @Test
    void fullUpdateCatalogueWithPatch() throws Exception {
        // Initialize the database
        insertedCatalogue = catalogueRepository.save(catalogue).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the catalogue using partial update
        Catalogue partialUpdatedCatalogue = new Catalogue();
        partialUpdatedCatalogue.setId(catalogue.getId());

        partialUpdatedCatalogue.name(UPDATED_NAME).code(UPDATED_CODE).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCatalogue.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCatalogue))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Catalogue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCatalogueUpdatableFieldsEquals(partialUpdatedCatalogue, getPersistedCatalogue(partialUpdatedCatalogue));
    }

    @Test
    void patchNonExistingCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogue.setId(longCount.incrementAndGet());

        // Create the Catalogue
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(catalogue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, catalogueDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Catalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogue.setId(longCount.incrementAndGet());

        // Create the Catalogue
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(catalogue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Catalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogue.setId(longCount.incrementAndGet());

        // Create the Catalogue
        CatalogueDTO catalogueDTO = catalogueMapper.toDto(catalogue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(catalogueDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Catalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCatalogue() {
        // Initialize the database
        insertedCatalogue = catalogueRepository.save(catalogue).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the catalogue
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, catalogue.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return catalogueRepository.count().block();
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

    protected Catalogue getPersistedCatalogue(Catalogue catalogue) {
        return catalogueRepository.findById(catalogue.getId()).block();
    }

    protected void assertPersistedCatalogueToMatchAllProperties(Catalogue expectedCatalogue) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCatalogueAllPropertiesEquals(expectedCatalogue, getPersistedCatalogue(expectedCatalogue));
        assertCatalogueUpdatableFieldsEquals(expectedCatalogue, getPersistedCatalogue(expectedCatalogue));
    }

    protected void assertPersistedCatalogueToMatchUpdatableProperties(Catalogue expectedCatalogue) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCatalogueAllUpdatablePropertiesEquals(expectedCatalogue, getPersistedCatalogue(expectedCatalogue));
        assertCatalogueUpdatableFieldsEquals(expectedCatalogue, getPersistedCatalogue(expectedCatalogue));
    }
}
