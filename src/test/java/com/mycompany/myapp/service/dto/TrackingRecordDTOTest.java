package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrackingRecordDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrackingRecordDTO.class);
        TrackingRecordDTO trackingRecordDTO1 = new TrackingRecordDTO();
        trackingRecordDTO1.setId(1L);
        TrackingRecordDTO trackingRecordDTO2 = new TrackingRecordDTO();
        assertThat(trackingRecordDTO1).isNotEqualTo(trackingRecordDTO2);
        trackingRecordDTO2.setId(trackingRecordDTO1.getId());
        assertThat(trackingRecordDTO1).isEqualTo(trackingRecordDTO2);
        trackingRecordDTO2.setId(2L);
        assertThat(trackingRecordDTO1).isNotEqualTo(trackingRecordDTO2);
        trackingRecordDTO1.setId(null);
        assertThat(trackingRecordDTO1).isNotEqualTo(trackingRecordDTO2);
    }
}
