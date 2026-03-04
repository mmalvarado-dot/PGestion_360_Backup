package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ChangeRequestTestSamples.*;
import static com.mycompany.myapp.domain.ItemCatalogueTestSamples.*;
// Quitamos el import de ResponsibleTestSamples porque ya no existe,
// y el generador de User en tests suele venir por defecto o no lo necesitamos aquí para esta prueba simple.
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
    void userTest() { // <-- Cambiado el nombre de la prueba
        ChangeRequest changeRequest = getChangeRequestRandomSampleGenerator();

        // Creamos un User manualmente para la prueba
        User userBack = new User();
        userBack.setId(1L); // Le damos un ID cualquiera para que la prueba pase

        changeRequest.setUser(userBack);
        assertThat(changeRequest.getUser()).isEqualTo(userBack);

        changeRequest.user(null);
        assertThat(changeRequest.getUser()).isNull();
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
