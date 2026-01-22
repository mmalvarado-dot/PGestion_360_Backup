package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ChangeRequestAsserts.*;
import static com.mycompany.myapp.domain.ChangeRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChangeRequestMapperTest {

    private ChangeRequestMapper changeRequestMapper;

    @BeforeEach
    void setUp() {
        changeRequestMapper = new ChangeRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getChangeRequestSample1();
        var actual = changeRequestMapper.toEntity(changeRequestMapper.toDto(expected));
        assertChangeRequestAllPropertiesEquals(expected, actual);
    }
}
