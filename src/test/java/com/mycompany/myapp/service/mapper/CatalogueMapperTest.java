package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.CatalogueAsserts.*;
import static com.mycompany.myapp.domain.CatalogueTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogueMapperTest {

    private CatalogueMapper catalogueMapper;

    @BeforeEach
    void setUp() {
        catalogueMapper = new CatalogueMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCatalogueSample1();
        var actual = catalogueMapper.toEntity(catalogueMapper.toDto(expected));
        assertCatalogueAllPropertiesEquals(expected, actual);
    }
}
