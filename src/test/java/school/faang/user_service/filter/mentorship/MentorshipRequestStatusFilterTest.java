package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorshipRequestStatusFilterTest {
    private MentorshipRequestStatusFilter mentorshipRequestStatusFilter;

    @BeforeEach
    public void setUp() {
        mentorshipRequestStatusFilter = new MentorshipRequestStatusFilter();
    }

    @Test
    public void testIsApplicableWithNullDescription() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        assertFalse(mentorshipRequestStatusFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    public void testIsApplicableWithNonNullDescription() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setStatus(RequestStatus.PENDING);
        assertTrue(mentorshipRequestStatusFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto =
                getMentorshipRequestFilterDtoWithStatus(RequestStatus.PENDING);
        MentorshipRequest mentorshipRequest = getMentorshipRequestWithDescription(RequestStatus.PENDING);
        assertTrue(mentorshipRequestStatusFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithNonAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto =
                getMentorshipRequestFilterDtoWithStatus(RequestStatus.ACCEPTED);
        MentorshipRequest mentorshipRequest = getMentorshipRequestWithDescription(RequestStatus.REJECTED);
        assertFalse(mentorshipRequestStatusFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }

    private MentorshipRequestFilterDto getMentorshipRequestFilterDtoWithStatus(RequestStatus requestStatus) {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setStatus(requestStatus);
        return mentorshipRequestFilterDto;
    }

    private MentorshipRequest getMentorshipRequestWithDescription(RequestStatus requestStatus) {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setStatus(requestStatus);
        return mentorshipRequest;
    }
}