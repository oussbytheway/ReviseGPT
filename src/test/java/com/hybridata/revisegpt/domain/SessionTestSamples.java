package com.hybridata.revisegpt.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Session getSessionSample1() {
        return new Session().id(1L);
    }

    public static Session getSessionSample2() {
        return new Session().id(2L);
    }

    public static Session getSessionRandomSampleGenerator() {
        return new Session().id(longCount.incrementAndGet());
    }
}
