package com.mycompany.myapp.web.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mycompany.myapp.config.TestSecurityConfiguration;
import com.mycompany.myapp.config.WebConfigurer;
import com.mycompany.myapp.domain.enumeration.TrackingActionType;
import com.mycompany.myapp.repository.TrackingRecordRepository; // <--- IMPORTANTE: Importamos el repositorio
import com.mycompany.myapp.service.TrackingRecordService;
import com.mycompany.myapp.service.dto.TrackingRecordDTO;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.config.JHipsterProperties;

@WebFluxTest(controllers = TrackingRecordResource.class)
@Import(TestSecurityConfiguration.class)
@EnableConfigurationProperties(value = JHipsterProperties.class)
class TrackingRecordResourceIT {

    private static final Instant DEFAULT_CHANGE_DATE = Instant.ofEpochMilli(0L);
    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final TrackingActionType DEFAULT_ACTION_TYPE = TrackingActionType.EDICION;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TrackingRecordService trackingRecordService;

    // --- AGREGADO: Simulamos el Repositorio porque el controlador lo pide ---
    @MockBean
    private TrackingRecordRepository trackingRecordRepository;

    // ------------------------------------------------------------------------

    @MockBean
    private ReactiveUserDetailsService userDetailsService;

    @MockBean
    private WebConfigurer webConfigurer;

    private TrackingRecordDTO trackingRecordDTO;

    @BeforeEach
    void initTest() {
        trackingRecordDTO = new TrackingRecordDTO();
        trackingRecordDTO.setId(1L);
        trackingRecordDTO.setChangeDate(DEFAULT_CHANGE_DATE);
        trackingRecordDTO.setStatus(DEFAULT_STATUS);
        trackingRecordDTO.setComments(DEFAULT_COMMENTS);
        trackingRecordDTO.setActionType(DEFAULT_ACTION_TYPE);
    }

    @Test
    @WithMockUser
    void createTrackingRecord() {
        // Simulamos que el servicio responde bien
        when(trackingRecordService.save(any(TrackingRecordDTO.class))).thenReturn(Mono.just(trackingRecordDTO));

        TrackingRecordDTO inputDto = new TrackingRecordDTO();
        inputDto.setStatus(DEFAULT_STATUS);
        inputDto.setComments(DEFAULT_COMMENTS);
        inputDto.setActionType(DEFAULT_ACTION_TYPE);

        webTestClient
            .mutateWith(csrf())
            .post()
            .uri("/api/tracking-records")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(inputDto)
            .exchange()
            .expectStatus()
            .isCreated();
    }

    @Test
    @WithMockUser
    void getAllTrackingRecords() {
        when(trackingRecordService.countAll()).thenReturn(Mono.just(1L));
        when(trackingRecordService.findAll(any(Pageable.class))).thenReturn(Flux.just(trackingRecordDTO));

        webTestClient
            .get()
            .uri("/api/tracking-records?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].status")
            .value(org.hamcrest.Matchers.hasItem(DEFAULT_STATUS));
    }

    @Test
    @WithMockUser
    void getTrackingRecord() {
        when(trackingRecordService.findOne(1L)).thenReturn(Mono.just(trackingRecordDTO));

        webTestClient
            .get()
            .uri("/api/tracking-records/{id}", 1L)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.status")
            .isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @WithMockUser
    void deleteTrackingRecord() {
        when(trackingRecordService.delete(1L)).thenReturn(Mono.empty());

        webTestClient
            .mutateWith(csrf())
            .delete()
            .uri("/api/tracking-records/{id}", 1L)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();
    }
}
