package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResponsibleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResponsibleDTO.class);
        ResponsibleDTO responsibleDTO1 = new ResponsibleDTO();
        responsibleDTO1.setId(1L);
        ResponsibleDTO responsibleDTO2 = new ResponsibleDTO();
        assertThat(responsibleDTO1).isNotEqualTo(responsibleDTO2);
        responsibleDTO2.setId(responsibleDTO1.getId());
        assertThat(responsibleDTO1).isEqualTo(responsibleDTO2);
        responsibleDTO2.setId(2L);
        assertThat(responsibleDTO1).isNotEqualTo(responsibleDTO2);
        responsibleDTO1.setId(null);
        assertThat(responsibleDTO1).isNotEqualTo(responsibleDTO2);
    }
}
