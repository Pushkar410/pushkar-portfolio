package com.pushkar.portfolio;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContactRateLimiterTest {

    @Test
    void blocksAfterLimitAndRecoversAfterTheWindow() {
        ContactRateLimiter limiter = new ContactRateLimiter(2);
        Instant t0 = Instant.parse("2026-01-01T00:00:00Z");

        assertTrue(limiter.tryAcquire("a", t0));
        assertTrue(limiter.tryAcquire("a", t0.plusSeconds(1)));
        assertFalse(limiter.tryAcquire("a", t0.plusSeconds(2)));

        assertTrue(limiter.tryAcquire("b", t0.plusSeconds(2)), "other clients are unaffected");
        assertTrue(limiter.tryAcquire("a", t0.plusSeconds(3601)), "window has passed");
    }
}
