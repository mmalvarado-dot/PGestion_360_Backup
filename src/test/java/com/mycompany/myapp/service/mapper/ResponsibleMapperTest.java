package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ResponsibleAsserts.*;
import static com.mycompany.myapp.domain.ResponsibleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponsibleMapperTest {

    private ResponsibleMapper responsibleMapper;

    @BeforeEach
    void setUp() {
        responsibleMapper = new ResponsibleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getResponsibleSample1();
        var actual = responsibleMapper.toEntity(responsibleMapper.toDto(expected));
        assertResponsibleAllPropertiesEquals(expected, actual);
    }
}
