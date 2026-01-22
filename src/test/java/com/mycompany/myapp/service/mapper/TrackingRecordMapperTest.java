package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrackingRecordAsserts.*;
import static com.mycompany.myapp.domain.TrackingRecordTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrackingRecordMapperTest {

    private TrackingRecordMapper trackingRecordMapper;

    @BeforeEach
    void setUp() {
        trackingRecordMapper = new TrackingRecordMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrackingRecordSample1();
        var actual = trackingRecordMapper.toEntity(trackingRecordMapper.toDto(expected));
        assertTrackingRecordAllPropertiesEquals(expected, actual);
    }
}
