package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ChangeRequestTestSamples.*;
import static com.mycompany.myapp.domain.ResponsibleTestSamples.*;
import static com.mycompany.myapp.domain.TrackingRecordTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrackingRecordTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrackingRecord.class);
        TrackingRecord trackingRecord1 = getTrackingRecordSample1();
        TrackingRecord trackingRecord2 = new TrackingRecord();
        assertThat(trackingRecord1).isNotEqualTo(trackingRecord2);

        trackingRecord2.setId(trackingRecord1.getId());
        assertThat(trackingRecord1).isEqualTo(trackingRecord2);

        trackingRecord2 = getTrackingRecordSample2();
        assertThat(trackingRecord1).isNotEqualTo(trackingRecord2);
    }

    @Test
    void responsibleTest() {
        TrackingRecord trackingRecord = getTrackingRecordRandomSampleGenerator();
        Responsible responsibleBack = getResponsibleRandomSampleGenerator();

        trackingRecord.setResponsible(responsibleBack);
        assertThat(trackingRecord.getResponsible()).isEqualTo(responsibleBack);

        trackingRecord.responsible(null);
        assertThat(trackingRecord.getResponsible()).isNull();
    }

    @Test
    void changeRequestTest() {
        TrackingRecord trackingRecord = getTrackingRecordRandomSampleGenerator();
        ChangeRequest changeRequestBack = getChangeRequestRandomSampleGenerator();

        trackingRecord.setChangeRequest(changeRequestBack);
        assertThat(trackingRecord.getChangeRequest()).isEqualTo(changeRequestBack);

        trackingRecord.changeRequest(null);
        assertThat(trackingRecord.getChangeRequest()).isNull();
    }
}
