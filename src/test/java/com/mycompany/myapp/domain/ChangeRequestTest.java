package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ChangeRequestTestSamples.*;
import static com.mycompany.myapp.domain.ItemCatalogueTestSamples.*;
import static com.mycompany.myapp.domain.ResponsibleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChangeRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChangeRequest.class);
        ChangeRequest changeRequest1 = getChangeRequestSample1();
        ChangeRequest changeRequest2 = new ChangeRequest();
        assertThat(changeRequest1).isNotEqualTo(changeRequest2);

        changeRequest2.setId(changeRequest1.getId());
        assertThat(changeRequest1).isEqualTo(changeRequest2);

        changeRequest2 = getChangeRequestSample2();
        assertThat(changeRequest1).isNotEqualTo(changeRequest2);
    }

    @Test
    void responsibleTest() {
        ChangeRequest changeRequest = getChangeRequestRandomSampleGenerator();
        Responsible responsibleBack = getResponsibleRandomSampleGenerator();

        changeRequest.setResponsible(responsibleBack);
        assertThat(changeRequest.getResponsible()).isEqualTo(responsibleBack);

        changeRequest.responsible(null);
        assertThat(changeRequest.getResponsible()).isNull();
    }

    @Test
    void itemCatalogueTest() {
        ChangeRequest changeRequest = getChangeRequestRandomSampleGenerator();
        ItemCatalogue itemCatalogueBack = getItemCatalogueRandomSampleGenerator();

        changeRequest.setItemCatalogue(itemCatalogueBack);
        assertThat(changeRequest.getItemCatalogue()).isEqualTo(itemCatalogueBack);

        changeRequest.itemCatalogue(null);
        assertThat(changeRequest.getItemCatalogue()).isNull();
    }
}
