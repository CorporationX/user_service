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

public class ReceiverIdFilterTest {
    private final ReceiverIdFilter receiverIdFilter = new ReceiverIdFilter();

    @Test
    @DisplayName("isApplicable returns true when receiverId is not null")
    void isApplicable_shouldReturnTrue() {
        MentorshipFilterDto dto = new MentorshipFilterDto(null, null, 10L, null);
        assertTrue(receiverIdFilter.isApplicable(dto));
    }

    @Test
    @DisplayName("isApplicable returns false when receiverId is null")
    void isApplicable_shouldReturnFalse() {
        MentorshipFilterDto dto = new MentorshipFilterDto(null, null, null, null);
        assertFalse(receiverIdFilter.isApplicable(dto));
    }

    @Test
    @DisplayName("apply filters requests by receiverId")
    void apply_shoudFilterByReceiverId() {
        User receiver1 = new User();
        receiver1.setId(10L);
        User receiver2 = new User();
        receiver2.setId(20L);

        MentorshipRequest r1 = MentorshipRequest.builder().receiver(receiver1).build();
        MentorshipRequest r2 = MentorshipRequest.builder().receiver(receiver2).build();

        MentorshipFilterDto dto = new MentorshipFilterDto(null, null, 10L, null);

        List<MentorshipRequest> result = receiverIdFilter.apply(Stream.of(r1, r2), dto).toList();
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getReceiver().getId());
    }
}