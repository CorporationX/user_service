package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequesterIdFilterTest {
    private final RequesterIdFilter requesterIdFilter = new RequesterIdFilter();

    @Test
    @DisplayName("isApplicable returns true when requesterId is not null")
    void isApplicable_shouldReturnTrue() {
        MentorshipFilterDto dto = new MentorshipFilterDto(null, 10L, null, null);
        assertTrue(requesterIdFilter.isApplicable(dto));
    }

    @Test
    @DisplayName("isApplicable returns false when requesterId is false")
    void isApplicable_shouldReturnFalse() {
        MentorshipFilterDto dto = new MentorshipFilterDto(null, null, null, null);
        assertFalse(requesterIdFilter.isApplicable(dto));
    }

    @Test
    @DisplayName("apply filters requests by requesterId")
    void apply_shoudFilterByReceiverId() {
        User requester1 = new User();
        requester1.setId(10L);
        User requester2 = new User();
        requester2.setId(20L);

        MentorshipRequest r1 = MentorshipRequest.builder().requester(requester1).build();
        MentorshipRequest r2 = MentorshipRequest.builder().requester(requester2).build();

        MentorshipFilterDto dto = new MentorshipFilterDto(null, 10L, null, null);

        List<MentorshipRequest> result = requesterIdFilter.apply(Stream.of(r1, r2), dto).toList();
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getRequester().getId());
    }
}
