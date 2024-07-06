package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .status(RequestStatus.PENDING).build();
        assertTrue(mentorshipRequestStatusFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    public void testFilterWithAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .status(RequestStatus.PENDING).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .status(RequestStatus.PENDING).build();


        Stream<MentorshipRequest> filteredStream =
                mentorshipRequestStatusFilter.filter(Stream.of(mentorshipRequest), mentorshipRequestFilterDto);
        assertEquals(List.of(mentorshipRequest), filteredStream.toList());
    }

    @Test
    public void testFilterWithNonAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .status(RequestStatus.ACCEPTED).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .status(RequestStatus.REJECTED).build();

        Stream<MentorshipRequest> filteredStream =
                mentorshipRequestStatusFilter.filter(Stream.of(mentorshipRequest), mentorshipRequestFilterDto);
        assertTrue(filteredStream.findAny().isEmpty());
    }
}