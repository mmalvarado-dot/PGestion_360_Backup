package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ItemCatalogueAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ItemCatalogue;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ItemCatalogueRepository;
import com.mycompany.myapp.service.dto.ItemCatalogueDTO;
import com.mycompany.myapp.service.mapper.ItemCatalogueMapper;
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
 * Integration tests for the {@link ItemCatalogueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ItemCatalogueResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CATALOGUE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CATALOGUE_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/item-catalogues";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ItemCatalogueRepository itemCatalogueRepository;

    @Autowired
    private ItemCatalogueMapper itemCatalogueMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ItemCatalogue itemCatalogue;

    private ItemCatalogue insertedItemCatalogue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemCatalogue createEntity() {
        return new ItemCatalogue().name(DEFAULT_NAME).code(DEFAULT_CODE).catalogueCode(DEFAULT_CATALOGUE_CODE).active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemCatalogue createUpdatedEntity() {
        return new ItemCatalogue().name(UPDATED_NAME).code(UPDATED_CODE).catalogueCode(UPDATED_CATALOGUE_CODE).active(UPDATED_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ItemCatalogue.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        itemCatalogue = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedItemCatalogue != null) {
            itemCatalogueRepository.delete(insertedItemCatalogue).block();
            insertedItemCatalogue = null;
        }
        deleteEntities(em);
    }

    @Test
    void createItemCatalogue() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ItemCatalogue
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);
        var returnedItemCatalogueDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ItemCatalogueDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ItemCatalogue in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedItemCatalogue = itemCatalogueMapper.toEntity(returnedItemCatalogueDTO);
        assertItemCatalogueUpdatableFieldsEquals(returnedItemCatalogue, getPersistedItemCatalogue(returnedItemCatalogue));

        insertedItemCatalogue = returnedItemCatalogue;
    }

    @Test
    void createItemCatalogueWithExistingId() throws Exception {
        // Create the ItemCatalogue with an existing ID
        itemCatalogue.setId(1L);
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemCatalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        itemCatalogue.setName(null);

        // Create the ItemCatalogue, which fails.
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        itemCatalogue.setCode(null);

        // Create the ItemCatalogue, which fails.
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCatalogueCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        itemCatalogue.setCatalogueCode(null);

        // Create the ItemCatalogue, which fails.
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllItemCatalogues() {
        // Initialize the database
        insertedItemCatalogue = itemCatalogueRepository.save(itemCatalogue).block();

        // Get all the itemCatalogueList
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
            .value(hasItem(itemCatalogue.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].catalogueCode")
            .value(hasItem(DEFAULT_CATALOGUE_CODE))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE));
    }

    @Test
    void getItemCatalogue() {
        // Initialize the database
        insertedItemCatalogue = itemCatalogueRepository.save(itemCatalogue).block();

        // Get the itemCatalogue
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, itemCatalogue.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(itemCatalogue.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.catalogueCode")
            .value(is(DEFAULT_CATALOGUE_CODE))
            .jsonPath("$.active")
            .value(is(DEFAULT_ACTIVE));
    }

    @Test
    void getNonExistingItemCatalogue() {
        // Get the itemCatalogue
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingItemCatalogue() throws Exception {
        // Initialize the database
        insertedItemCatalogue = itemCatalogueRepository.save(itemCatalogue).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the itemCatalogue
        ItemCatalogue updatedItemCatalogue = itemCatalogueRepository.findById(itemCatalogue.getId()).block();
        updatedItemCatalogue.name(UPDATED_NAME).code(UPDATED_CODE).catalogueCode(UPDATED_CATALOGUE_CODE).active(UPDATED_ACTIVE);
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(updatedItemCatalogue);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, itemCatalogueDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ItemCatalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedItemCatalogueToMatchAllProperties(updatedItemCatalogue);
    }

    @Test
    void putNonExistingItemCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCatalogue.setId(longCount.incrementAndGet());

        // Create the ItemCatalogue
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, itemCatalogueDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemCatalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchItemCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCatalogue.setId(longCount.incrementAndGet());

        // Create the ItemCatalogue
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemCatalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamItemCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCatalogue.setId(longCount.incrementAndGet());

        // Create the ItemCatalogue
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ItemCatalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateItemCatalogueWithPatch() throws Exception {
        // Initialize the database
        insertedItemCatalogue = itemCatalogueRepository.save(itemCatalogue).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the itemCatalogue using partial update
        ItemCatalogue partialUpdatedItemCatalogue = new ItemCatalogue();
        partialUpdatedItemCatalogue.setId(itemCatalogue.getId());

        partialUpdatedItemCatalogue.catalogueCode(UPDATED_CATALOGUE_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedItemCatalogue.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedItemCatalogue))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ItemCatalogue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertItemCatalogueUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedItemCatalogue, itemCatalogue),
            getPersistedItemCatalogue(itemCatalogue)
        );
    }

    @Test
    void fullUpdateItemCatalogueWithPatch() throws Exception {
        // Initialize the database
        insertedItemCatalogue = itemCatalogueRepository.save(itemCatalogue).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the itemCatalogue using partial update
        ItemCatalogue partialUpdatedItemCatalogue = new ItemCatalogue();
        partialUpdatedItemCatalogue.setId(itemCatalogue.getId());

        partialUpdatedItemCatalogue.name(UPDATED_NAME).code(UPDATED_CODE).catalogueCode(UPDATED_CATALOGUE_CODE).active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedItemCatalogue.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedItemCatalogue))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ItemCatalogue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertItemCatalogueUpdatableFieldsEquals(partialUpdatedItemCatalogue, getPersistedItemCatalogue(partialUpdatedItemCatalogue));
    }

    @Test
    void patchNonExistingItemCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCatalogue.setId(longCount.incrementAndGet());

        // Create the ItemCatalogue
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, itemCatalogueDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemCatalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchItemCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCatalogue.setId(longCount.incrementAndGet());

        // Create the ItemCatalogue
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemCatalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamItemCatalogue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCatalogue.setId(longCount.incrementAndGet());

        // Create the ItemCatalogue
        ItemCatalogueDTO itemCatalogueDTO = itemCatalogueMapper.toDto(itemCatalogue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(itemCatalogueDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ItemCatalogue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteItemCatalogue() {
        // Initialize the database
        insertedItemCatalogue = itemCatalogueRepository.save(itemCatalogue).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the itemCatalogue
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, itemCatalogue.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return itemCatalogueRepository.count().block();
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

    protected ItemCatalogue getPersistedItemCatalogue(ItemCatalogue itemCatalogue) {
        return itemCatalogueRepository.findById(itemCatalogue.getId()).block();
    }

    protected void assertPersistedItemCatalogueToMatchAllProperties(ItemCatalogue expectedItemCatalogue) {
        // Test fails because reactive api returns an empty object instead of null
        // assertItemCatalogueAllPropertiesEquals(expectedItemCatalogue, getPersistedItemCatalogue(expectedItemCatalogue));
        assertItemCatalogueUpdatableFieldsEquals(expectedItemCatalogue, getPersistedItemCatalogue(expectedItemCatalogue));
    }

    protected void assertPersistedItemCatalogueToMatchUpdatableProperties(ItemCatalogue expectedItemCatalogue) {
        // Test fails because reactive api returns an empty object instead of null
        // assertItemCatalogueAllUpdatablePropertiesEquals(expectedItemCatalogue, getPersistedItemCatalogue(expectedItemCatalogue));
        assertItemCatalogueUpdatableFieldsEquals(expectedItemCatalogue, getPersistedItemCatalogue(expectedItemCatalogue));
    }
}
