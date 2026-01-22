package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemCatalogueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemCatalogueDTO.class);
        ItemCatalogueDTO itemCatalogueDTO1 = new ItemCatalogueDTO();
        itemCatalogueDTO1.setId(1L);
        ItemCatalogueDTO itemCatalogueDTO2 = new ItemCatalogueDTO();
        assertThat(itemCatalogueDTO1).isNotEqualTo(itemCatalogueDTO2);
        itemCatalogueDTO2.setId(itemCatalogueDTO1.getId());
        assertThat(itemCatalogueDTO1).isEqualTo(itemCatalogueDTO2);
        itemCatalogueDTO2.setId(2L);
        assertThat(itemCatalogueDTO1).isNotEqualTo(itemCatalogueDTO2);
        itemCatalogueDTO1.setId(null);
        assertThat(itemCatalogueDTO1).isNotEqualTo(itemCatalogueDTO2);
    }
}
