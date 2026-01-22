package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ResponsibleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResponsibleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Responsible.class);
        Responsible responsible1 = getResponsibleSample1();
        Responsible responsible2 = new Responsible();
        assertThat(responsible1).isNotEqualTo(responsible2);

        responsible2.setId(responsible1.getId());
        assertThat(responsible1).isEqualTo(responsible2);

        responsible2 = getResponsibleSample2();
        assertThat(responsible1).isNotEqualTo(responsible2);
    }
}
