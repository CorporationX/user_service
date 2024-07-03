package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestReceiverFilterTest {
    @Mock
    private MentorshipRequest mentorshipRequest;

    @InjectMocks
    private MentorshipRequestReceiverFilter mentorshipRequestReceiverFilter;

    @Test
    public void testIsApplicableWithNullDescription() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        assertFalse(mentorshipRequestReceiverFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    public void testIsApplicableWithNonNullDescription() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setReceiverId(1L);
        assertTrue(mentorshipRequestReceiverFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .receiverId(1L).build();
        User user = User.builder()
                .id(1L).build();
        when(mentorshipRequest.getReceiver()).thenReturn(user);
        assertTrue(mentorshipRequestReceiverFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithNonAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .receiverId(1L).build();
        User user = User.builder()
                .id(2L).build();
        when(mentorshipRequest.getReceiver()).thenReturn(user);
        assertFalse(mentorshipRequestReceiverFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }
}