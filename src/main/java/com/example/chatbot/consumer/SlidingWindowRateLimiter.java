package com.example.chatbot.consumer;

import java.time.Instant;
import java.util.LinkedList;
public class SlidingWindowRateLimiter {
    private final int maxRequests;
    private final long windowSizeInMillis;
    private final LinkedList<Long> requests = new LinkedList<>();
    public SlidingWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }
    public synchronized boolean isAllowed() {
        final long now = Instant.now().toEpochMilli();
        while (!requests.isEmpty() && (now - requests.peekFirst() > windowSizeInMillis)) {
            requests.removeFirst();
        }
        if (requests.size() < maxRequests) {
            requests.addLast(now);
            return true;
        } else {
            return false;
        }
    }
}
