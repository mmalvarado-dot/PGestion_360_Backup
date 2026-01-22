package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FileRecordDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileRecordDTO.class);
        FileRecordDTO fileRecordDTO1 = new FileRecordDTO();
        fileRecordDTO1.setId(1L);
        FileRecordDTO fileRecordDTO2 = new FileRecordDTO();
        assertThat(fileRecordDTO1).isNotEqualTo(fileRecordDTO2);
        fileRecordDTO2.setId(fileRecordDTO1.getId());
        assertThat(fileRecordDTO1).isEqualTo(fileRecordDTO2);
        fileRecordDTO2.setId(2L);
        assertThat(fileRecordDTO1).isNotEqualTo(fileRecordDTO2);
        fileRecordDTO1.setId(null);
        assertThat(fileRecordDTO1).isNotEqualTo(fileRecordDTO2);
    }
}
