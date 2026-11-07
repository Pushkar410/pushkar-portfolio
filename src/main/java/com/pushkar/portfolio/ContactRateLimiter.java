package com.pushkar.portfolio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Sliding-window limit per client address, so the message file cannot be filled by a script. */
@Component
public class ContactRateLimiter {

    private static final Duration WINDOW = Duration.ofHours(1);
    private static final int MAX_TRACKED_CLIENTS = 10_000;

    private final int maxPerWindow;
    private final Map<String, Deque<Instant>> hits = new ConcurrentHashMap<>();

    public ContactRateLimiter(@Value("${portfolio.contact.max-per-hour:5}") int maxPerWindow) {
        this.maxPerWindow = maxPerWindow;
    }

    public boolean tryAcquire(String client) {
        return tryAcquire(client, Instant.now());
    }

    boolean tryAcquire(String client, Instant now) {
        if (hits.size() > MAX_TRACKED_CLIENTS) {
            hits.clear();
        }
        Deque<Instant> window = hits.computeIfAbsent(client, key -> new ArrayDeque<>());
        synchronized (window) {
            Instant cutoff = now.minus(WINDOW);
            while (!window.isEmpty() && window.peekFirst().isBefore(cutoff)) {
                window.pollFirst();
            }
            if (window.size() >= maxPerWindow) {
                return false;
            }
            window.addLast(now);
            return true;
        }
    }
}
