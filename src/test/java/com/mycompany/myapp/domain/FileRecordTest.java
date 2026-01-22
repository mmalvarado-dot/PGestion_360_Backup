package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ChangeRequestTestSamples.*;
import static com.mycompany.myapp.domain.FileRecordTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FileRecordTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileRecord.class);
        FileRecord fileRecord1 = getFileRecordSample1();
        FileRecord fileRecord2 = new FileRecord();
        assertThat(fileRecord1).isNotEqualTo(fileRecord2);

        fileRecord2.setId(fileRecord1.getId());
        assertThat(fileRecord1).isEqualTo(fileRecord2);

        fileRecord2 = getFileRecordSample2();
        assertThat(fileRecord1).isNotEqualTo(fileRecord2);
    }

    @Test
    void changeRequestTest() {
        FileRecord fileRecord = getFileRecordRandomSampleGenerator();
        ChangeRequest changeRequestBack = getChangeRequestRandomSampleGenerator();

        fileRecord.setChangeRequest(changeRequestBack);
        assertThat(fileRecord.getChangeRequest()).isEqualTo(changeRequestBack);

        fileRecord.changeRequest(null);
        assertThat(fileRecord.getChangeRequest()).isNull();
    }
}
