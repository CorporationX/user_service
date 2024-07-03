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
class MentorshipRequestRequesterFilterTest {
    @Mock
    private MentorshipRequest mentorshipRequest;

    @InjectMocks
    private MentorshipRequestRequesterFilter mentorshipRequestRequesterFilter;

    @BeforeEach
    public void setUp() {
        mentorshipRequestRequesterFilter = new MentorshipRequestRequesterFilter();
    }

    @Test
    public void testIsApplicableWithNullDescription() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        assertFalse(mentorshipRequestRequesterFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    public void testIsApplicableWithNonNullDescription() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setRequesterId(1L);
        assertTrue(mentorshipRequestRequesterFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = getMentorshipRequestFilterDtoWithRequesterId(1L);
        User user = getUserWithId(1L);
        when(mentorshipRequest.getRequester()).thenReturn(user);
        assertTrue(mentorshipRequestRequesterFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithNonAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = getMentorshipRequestFilterDtoWithRequesterId(10L);
        User user = getUserWithId(20L);
        when(mentorshipRequest.getRequester()).thenReturn(user);
        assertFalse(mentorshipRequestRequesterFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }

    private MentorshipRequestFilterDto getMentorshipRequestFilterDtoWithRequesterId(long requesterId) {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setRequesterId(requesterId);
        return mentorshipRequestFilterDto;
    }

    private User getUserWithId(long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }
}