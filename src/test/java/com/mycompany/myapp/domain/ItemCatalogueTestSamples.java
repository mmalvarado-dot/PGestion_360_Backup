package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ItemCatalogueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ItemCatalogue getItemCatalogueSample1() {
        return new ItemCatalogue().id(1L).name("name1").code("code1").catalogueCode("catalogueCode1");
    }

    public static ItemCatalogue getItemCatalogueSample2() {
        return new ItemCatalogue().id(2L).name("name2").code("code2").catalogueCode("catalogueCode2");
    }

    public static ItemCatalogue getItemCatalogueRandomSampleGenerator() {
        return new ItemCatalogue()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .catalogueCode(UUID.randomUUID().toString());
    }
}
