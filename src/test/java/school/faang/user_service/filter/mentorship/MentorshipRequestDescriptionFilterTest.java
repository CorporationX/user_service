package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorshipRequestDescriptionFilterTest {
    private MentorshipRequestDescriptionFilter mentorshipRequestDescriptionFilter;

    @BeforeEach
    public void setUp() {
        mentorshipRequestDescriptionFilter = new MentorshipRequestDescriptionFilter();
    }

    @Test
    public void testIsApplicableWithNullDescription() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        assertFalse(mentorshipRequestDescriptionFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    public void testIsApplicableWithNonNullDescription() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setDescription("description");
        assertTrue(mentorshipRequestDescriptionFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = getMentorshipRequestFilterDtoWithDescription("take");
        MentorshipRequest mentorshipRequest = getMentorshipRequestWithDescription("RETAKE");
        assertTrue(mentorshipRequestDescriptionFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithNonAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = getMentorshipRequestFilterDtoWithDescription("got");
        MentorshipRequest mentorshipRequest = getMentorshipRequestWithDescription("FORGET");
        assertFalse(mentorshipRequestDescriptionFilter.filter(mentorshipRequest, mentorshipRequestFilterDto));
    }

    private MentorshipRequestFilterDto getMentorshipRequestFilterDtoWithDescription(String description) {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setDescription(description);
        return mentorshipRequestFilterDto;
    }

    private MentorshipRequest getMentorshipRequestWithDescription(String description) {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setDescription(description);
        return mentorshipRequest;
    }
}