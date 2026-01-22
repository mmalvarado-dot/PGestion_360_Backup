package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.FileRecordAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.FileRecord;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.FileRecordRepository;
import com.mycompany.myapp.service.dto.FileRecordDTO;
import com.mycompany.myapp.service.mapper.FileRecordMapper;
import java.util.Base64;
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
 * Integration tests for the {@link FileRecordResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FileRecordResourceIT {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_FILE_PATH = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FILE_TYPE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/file-records";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FileRecordRepository fileRecordRepository;

    @Autowired
    private FileRecordMapper fileRecordMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private FileRecord fileRecord;

    private FileRecord insertedFileRecord;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileRecord createEntity() {
        return new FileRecord()
            .fileName(DEFAULT_FILE_NAME)
            .filePath(DEFAULT_FILE_PATH)
            .fileType(DEFAULT_FILE_TYPE)
            .content(DEFAULT_CONTENT)
            .contentContentType(DEFAULT_CONTENT_CONTENT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileRecord createUpdatedEntity() {
        return new FileRecord()
            .fileName(UPDATED_FILE_NAME)
            .filePath(UPDATED_FILE_PATH)
            .fileType(UPDATED_FILE_TYPE)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(FileRecord.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        fileRecord = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFileRecord != null) {
            fileRecordRepository.delete(insertedFileRecord).block();
            insertedFileRecord = null;
        }
        deleteEntities(em);
    }

    @Test
    void createFileRecord() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FileRecord
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);
        var returnedFileRecordDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(FileRecordDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the FileRecord in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFileRecord = fileRecordMapper.toEntity(returnedFileRecordDTO);
        assertFileRecordUpdatableFieldsEquals(returnedFileRecord, getPersistedFileRecord(returnedFileRecord));

        insertedFileRecord = returnedFileRecord;
    }

    @Test
    void createFileRecordWithExistingId() throws Exception {
        // Create the FileRecord with an existing ID
        fileRecord.setId(1L);
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FileRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkFileNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileRecord.setFileName(null);

        // Create the FileRecord, which fails.
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFilePathIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileRecord.setFilePath(null);

        // Create the FileRecord, which fails.
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFileTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileRecord.setFileType(null);

        // Create the FileRecord, which fails.
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllFileRecords() {
        // Initialize the database
        insertedFileRecord = fileRecordRepository.save(fileRecord).block();

        // Get all the fileRecordList
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
            .value(hasItem(fileRecord.getId().intValue()))
            .jsonPath("$.[*].fileName")
            .value(hasItem(DEFAULT_FILE_NAME))
            .jsonPath("$.[*].filePath")
            .value(hasItem(DEFAULT_FILE_PATH))
            .jsonPath("$.[*].fileType")
            .value(hasItem(DEFAULT_FILE_TYPE))
            .jsonPath("$.[*].contentContentType")
            .value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE))
            .jsonPath("$.[*].content")
            .value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_CONTENT)));
    }

    @Test
    void getFileRecord() {
        // Initialize the database
        insertedFileRecord = fileRecordRepository.save(fileRecord).block();

        // Get the fileRecord
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, fileRecord.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(fileRecord.getId().intValue()))
            .jsonPath("$.fileName")
            .value(is(DEFAULT_FILE_NAME))
            .jsonPath("$.filePath")
            .value(is(DEFAULT_FILE_PATH))
            .jsonPath("$.fileType")
            .value(is(DEFAULT_FILE_TYPE))
            .jsonPath("$.contentContentType")
            .value(is(DEFAULT_CONTENT_CONTENT_TYPE))
            .jsonPath("$.content")
            .value(is(Base64.getEncoder().encodeToString(DEFAULT_CONTENT)));
    }

    @Test
    void getNonExistingFileRecord() {
        // Get the fileRecord
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingFileRecord() throws Exception {
        // Initialize the database
        insertedFileRecord = fileRecordRepository.save(fileRecord).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileRecord
        FileRecord updatedFileRecord = fileRecordRepository.findById(fileRecord.getId()).block();
        updatedFileRecord
            .fileName(UPDATED_FILE_NAME)
            .filePath(UPDATED_FILE_PATH)
            .fileType(UPDATED_FILE_TYPE)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE);
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(updatedFileRecord);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, fileRecordDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FileRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFileRecordToMatchAllProperties(updatedFileRecord);
    }

    @Test
    void putNonExistingFileRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRecord.setId(longCount.incrementAndGet());

        // Create the FileRecord
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, fileRecordDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FileRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFileRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRecord.setId(longCount.incrementAndGet());

        // Create the FileRecord
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FileRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFileRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRecord.setId(longCount.incrementAndGet());

        // Create the FileRecord
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FileRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFileRecordWithPatch() throws Exception {
        // Initialize the database
        insertedFileRecord = fileRecordRepository.save(fileRecord).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileRecord using partial update
        FileRecord partialUpdatedFileRecord = new FileRecord();
        partialUpdatedFileRecord.setId(fileRecord.getId());

        partialUpdatedFileRecord.filePath(UPDATED_FILE_PATH).fileType(UPDATED_FILE_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFileRecord.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedFileRecord))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FileRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFileRecordUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFileRecord, fileRecord),
            getPersistedFileRecord(fileRecord)
        );
    }

    @Test
    void fullUpdateFileRecordWithPatch() throws Exception {
        // Initialize the database
        insertedFileRecord = fileRecordRepository.save(fileRecord).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileRecord using partial update
        FileRecord partialUpdatedFileRecord = new FileRecord();
        partialUpdatedFileRecord.setId(fileRecord.getId());

        partialUpdatedFileRecord
            .fileName(UPDATED_FILE_NAME)
            .filePath(UPDATED_FILE_PATH)
            .fileType(UPDATED_FILE_TYPE)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFileRecord.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedFileRecord))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FileRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFileRecordUpdatableFieldsEquals(partialUpdatedFileRecord, getPersistedFileRecord(partialUpdatedFileRecord));
    }

    @Test
    void patchNonExistingFileRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRecord.setId(longCount.incrementAndGet());

        // Create the FileRecord
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, fileRecordDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FileRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFileRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRecord.setId(longCount.incrementAndGet());

        // Create the FileRecord
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FileRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFileRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRecord.setId(longCount.incrementAndGet());

        // Create the FileRecord
        FileRecordDTO fileRecordDTO = fileRecordMapper.toDto(fileRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(fileRecordDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FileRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFileRecord() {
        // Initialize the database
        insertedFileRecord = fileRecordRepository.save(fileRecord).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the fileRecord
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, fileRecord.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return fileRecordRepository.count().block();
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

    protected FileRecord getPersistedFileRecord(FileRecord fileRecord) {
        return fileRecordRepository.findById(fileRecord.getId()).block();
    }

    protected void assertPersistedFileRecordToMatchAllProperties(FileRecord expectedFileRecord) {
        // Test fails because reactive api returns an empty object instead of null
        // assertFileRecordAllPropertiesEquals(expectedFileRecord, getPersistedFileRecord(expectedFileRecord));
        assertFileRecordUpdatableFieldsEquals(expectedFileRecord, getPersistedFileRecord(expectedFileRecord));
    }

    protected void assertPersistedFileRecordToMatchUpdatableProperties(FileRecord expectedFileRecord) {
        // Test fails because reactive api returns an empty object instead of null
        // assertFileRecordAllUpdatablePropertiesEquals(expectedFileRecord, getPersistedFileRecord(expectedFileRecord));
        assertFileRecordUpdatableFieldsEquals(expectedFileRecord, getPersistedFileRecord(expectedFileRecord));
    }
}
