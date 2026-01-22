package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CatalogueTestSamples.*;
import static com.mycompany.myapp.domain.ItemCatalogueTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemCatalogueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemCatalogue.class);
        ItemCatalogue itemCatalogue1 = getItemCatalogueSample1();
        ItemCatalogue itemCatalogue2 = new ItemCatalogue();
        assertThat(itemCatalogue1).isNotEqualTo(itemCatalogue2);

        itemCatalogue2.setId(itemCatalogue1.getId());
        assertThat(itemCatalogue1).isEqualTo(itemCatalogue2);

        itemCatalogue2 = getItemCatalogueSample2();
        assertThat(itemCatalogue1).isNotEqualTo(itemCatalogue2);
    }

    @Test
    void catalogueTest() {
        ItemCatalogue itemCatalogue = getItemCatalogueRandomSampleGenerator();
        Catalogue catalogueBack = getCatalogueRandomSampleGenerator();

        itemCatalogue.setCatalogue(catalogueBack);
        assertThat(itemCatalogue.getCatalogue()).isEqualTo(catalogueBack);

        itemCatalogue.catalogue(null);
        assertThat(itemCatalogue.getCatalogue()).isNull();
    }
}
