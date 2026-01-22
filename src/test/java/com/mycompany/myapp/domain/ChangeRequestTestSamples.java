package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ChangeRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ChangeRequest getChangeRequestSample1() {
        return new ChangeRequest()
            .id(1L)
            .title("title1")
            .description("description1")
            .status("status1")
            .solicitante("solicitante1")
            .departamento("departamento1");
    }

    public static ChangeRequest getChangeRequestSample2() {
        return new ChangeRequest()
            .id(2L)
            .title("title2")
            .description("description2")
            .status("status2")
            .solicitante("solicitante2")
            .departamento("departamento2");
    }

    public static ChangeRequest getChangeRequestRandomSampleGenerator() {
        return new ChangeRequest()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .solicitante(UUID.randomUUID().toString())
            .departamento(UUID.randomUUID().toString());
    }
}
