package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ItemCatalogueAsserts.*;
import static com.mycompany.myapp.domain.ItemCatalogueTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemCatalogueMapperTest {

    private ItemCatalogueMapper itemCatalogueMapper;

    @BeforeEach
    void setUp() {
        itemCatalogueMapper = new ItemCatalogueMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getItemCatalogueSample1();
        var actual = itemCatalogueMapper.toEntity(itemCatalogueMapper.toDto(expected));
        assertItemCatalogueAllPropertiesEquals(expected, actual);
    }
}
