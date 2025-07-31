package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import school.faang.user_service.event.FollowerEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FollowerEventTest {

    @Test
    void testNoArgsConstructor() {
        FollowerEvent event = new FollowerEvent();

        assertNull(event.getFollowerId());
        assertNull(event.getPublisherId());
        assertNull(event.getProjectId());
        assertNull(event.getFollowedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        FollowerEvent event = new FollowerEvent(1L, 2L, 3L, now);

        assertEquals(1L, event.getFollowerId());
        assertEquals(2L, event.getPublisherId());
        assertEquals(3L, event.getProjectId());
        assertEquals(now, event.getFollowedAt());
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        FollowerEvent event = FollowerEvent.builder()
                .followerId(1L)
                .publisherId(2L)
                .projectId(3L)
                .followedAt(now)
                .build();

        assertEquals(1L, event.getFollowerId());
        assertEquals(2L, event.getPublisherId());
        assertEquals(3L, event.getProjectId());
        assertEquals(now, event.getFollowedAt());
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        FollowerEvent event = new FollowerEvent();

        event.setFollowerId(1L);
        event.setPublisherId(2L);
        event.setProjectId(3L);
        event.setFollowedAt(now);

        assertEquals(1L, event.getFollowerId());
        assertEquals(2L, event.getPublisherId());
        assertEquals(3L, event.getProjectId());
        assertEquals(now, event.getFollowedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        FollowerEvent e1 = FollowerEvent.builder()
                .followerId(1L)
                .publisherId(2L)
                .projectId(3L)
                .followedAt(now)
                .build();

        FollowerEvent e2 = FollowerEvent.builder()
                .followerId(1L)
                .publisherId(2L)
                .projectId(3L)
                .followedAt(now)
                .build();

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testToString() {
        FollowerEvent event = FollowerEvent.builder()
                .followerId(1L)
                .publisherId(2L)
                .projectId(3L)
                .build();

        String result = event.toString();
        assertTrue(result.contains("FollowerEvent"));
        assertTrue(result.contains("followerId=1"));
        assertTrue(result.contains("publisherId=2"));
        assertTrue(result.contains("projectId=3"));
    }
}
