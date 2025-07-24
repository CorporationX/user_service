package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatusFilterTest {
    private final StatusFilter statusFilter = new StatusFilter();

    @Test
    @DisplayName("isApplicable returns true when status is not null")
    void isApplicable_shouldReturnTrue() {
        MentorshipFilterDto dto = new MentorshipFilterDto(null, null, null, RequestStatus.ACCEPTED);
        assertTrue(statusFilter.isApplicable(dto));
    }

    @Test
    @DisplayName("isApplicable returns false when receiverId is null")
    void isApplicable_shouldReturnFalse() {
        MentorshipFilterDto dto = new MentorshipFilterDto(null, null, null, null);
        assertFalse(statusFilter.isApplicable(dto));
    }

    @Test
    @DisplayName("apply filters requests by receiverId")
    void apply_shoudFilterByReceiverId() {

        MentorshipRequest r1 = MentorshipRequest.builder().status(RequestStatus.ACCEPTED).build();
        MentorshipRequest r2 = MentorshipRequest.builder().status(RequestStatus.REJECTED).build();

        MentorshipFilterDto dto = new MentorshipFilterDto(null, null, 10L, RequestStatus.ACCEPTED);

        List<MentorshipRequest> result = statusFilter.apply(Stream.of(r1, r2), dto).toList();
        assertEquals(1, result.size());
        assertEquals(RequestStatus.ACCEPTED, result.get(0).getStatus());
    }
}
