package com.tempo.challenge.util;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterTest {

    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiter();
    }

    @Test
    void getBucket_withNewIp_shouldCreateNewBucket() {
        String ip = "192.168.1.1";

        Bucket bucket = rateLimiter.getBucket(ip);

        assertNotNull(bucket);
        assertTrue(bucket.tryConsume(1)); // Should allow first request
    }

    @Test
    void getBucket_withSameIp_shouldReturnSameBucket() {
        String ip = "192.168.1.1";

        Bucket bucket1 = rateLimiter.getBucket(ip);
        Bucket bucket2 = rateLimiter.getBucket(ip);

        assertSame(bucket1, bucket2); // Should be the exact same instance
    }

    @Test
    void getBucket_withDifferentIps_shouldCreateDifferentBuckets() {
        String ip1 = "192.168.1.1";
        String ip2 = "192.168.1.2";

        Bucket bucket1 = rateLimiter.getBucket(ip1);
        Bucket bucket2 = rateLimiter.getBucket(ip2);

        assertNotSame(bucket1, bucket2); // Should be different instances
    }

    @Test
    void getBucket_shouldRespectRateLimit() {
        String ip = "192.168.1.1";
        Bucket bucket = rateLimiter.getBucket(ip);

        // Should allow 3 requests (limit is 3 per minute)
        assertTrue(bucket.tryConsume(1));
        assertTrue(bucket.tryConsume(1));
        assertTrue(bucket.tryConsume(1));

        // Fourth request should be rejected
        assertFalse(bucket.tryConsume(1));
    }



    @Test
    void getBucket_withEmptyIp_shouldCreateBucket() {
        String emptyIp = "";

        Bucket bucket = rateLimiter.getBucket(emptyIp);

        assertNotNull(bucket);
        assertTrue(bucket.tryConsume(1));
    }

    @Test
    void getBucket_concurrentAccess_shouldHandleMultipleIps() {
        // Test with multiple different IPs to ensure concurrent map works
        String[] ips = {"192.168.1.1", "192.168.1.2", "192.168.1.3", "10.0.0.1", "127.0.0.1"};

        for (String ip : ips) {
            Bucket bucket = rateLimiter.getBucket(ip);
            assertNotNull(bucket);
            assertTrue(bucket.tryConsume(1)); // Each IP should have its own fresh bucket
        }

        // Verify each IP still gets the same bucket on second call
        for (String ip : ips) {
            Bucket bucket1 = rateLimiter.getBucket(ip);
            Bucket bucket2 = rateLimiter.getBucket(ip);
            assertSame(bucket1, bucket2);
        }
    }

    @Test
    void getBucket_checkBandwidthConfiguration() {
        String ip = "192.168.1.1";
        Bucket bucket = rateLimiter.getBucket(ip);

        // Verify the bucket allows exactly 3 tokens initially
        assertTrue(bucket.tryConsume(3)); // Should consume all 3 tokens
        assertFalse(bucket.tryConsume(1)); // Should fail as no tokens left
    }
}
