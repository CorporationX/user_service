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
class MentorshipRequestRequesterFilterTest {
    @Mock
    private MentorshipRequest mentorshipRequest;

    @InjectMocks
    private MentorshipRequestRequesterFilter mentorshipRequestRequesterFilter;

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
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .requesterId(1L).build();
        User user = User.builder()
                .id(1L).build();
        when(mentorshipRequest.getRequester()).thenReturn(user);
        assertTrue(mentorshipRequestRequesterFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithNonAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .requesterId(1L).build();
        User user = User.builder()
                .id(2L).build();
        when(mentorshipRequest.getRequester()).thenReturn(user);
        assertFalse(mentorshipRequestRequesterFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }
}