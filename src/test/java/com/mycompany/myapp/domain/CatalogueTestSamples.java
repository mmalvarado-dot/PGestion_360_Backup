package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CatalogueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Catalogue getCatalogueSample1() {
        return new Catalogue().id(1L).name("name1").code("code1");
    }

    public static Catalogue getCatalogueSample2() {
        return new Catalogue().id(2L).name("name2").code("code2");
    }

    public static Catalogue getCatalogueRandomSampleGenerator() {
        return new Catalogue().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).code(UUID.randomUUID().toString());
    }
}
