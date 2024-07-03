package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    public void setUp() {
        mentorshipRequestReceiverFilter = new MentorshipRequestReceiverFilter();
    }

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
        MentorshipRequestFilterDto mentorshipRequestFilterDto = getMentorshipRequestFilterDtoWithReceiverId(1L);
        User user = getUserWithId(1L);
        when(mentorshipRequest.getReceiver()).thenReturn(user);
        assertTrue(mentorshipRequestReceiverFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithNonAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = getMentorshipRequestFilterDtoWithReceiverId(10L);
        User user = getUserWithId(20L);
        when(mentorshipRequest.getReceiver()).thenReturn(user);
        assertFalse(mentorshipRequestReceiverFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }

    private MentorshipRequestFilterDto getMentorshipRequestFilterDtoWithReceiverId(long receiverId) {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setReceiverId(receiverId);
        return mentorshipRequestFilterDto;
    }

    private User getUserWithId(long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }
}