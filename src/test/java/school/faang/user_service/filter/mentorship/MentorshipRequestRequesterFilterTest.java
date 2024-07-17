package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorshipRequestRequesterFilterTest {
    private MentorshipRequestRequesterFilter mentorshipRequestRequesterFilter;

    @BeforeEach
    public void setUp() {
        mentorshipRequestRequesterFilter = new MentorshipRequestRequesterFilter();
    }

    @Test
    @DisplayName("testing isApplicable with null requester filter")
    public void testIsApplicableWithNullRequesterFilter() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        assertFalse(mentorshipRequestRequesterFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable with non-null requester filter")
    public void testIsApplicableWithNonNullRequesterFilter() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setRequesterId(1L);
        assertTrue(mentorshipRequestRequesterFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    @DisplayName("testing filter with appropriate requester value")
    public void testFilterWithAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .requesterId(1L).build();
        User user = User.builder()
                .id(1L).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .requester(user).build();

        Stream<MentorshipRequest> filteredStream =
                mentorshipRequestRequesterFilter.filter(Stream.of(mentorshipRequest), mentorshipRequestFilterDto);
        assertEquals(List.of(mentorshipRequest), filteredStream.toList());
    }

    @Test
    @DisplayName("testing filter with non-appropriate requester value")
    public void testFilterWithNonAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .requesterId(1L).build();
        User user = User.builder()
                .id(2L).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .requester(user).build();

        Stream<MentorshipRequest> filteredStream =
                mentorshipRequestRequesterFilter.filter(Stream.of(mentorshipRequest), mentorshipRequestFilterDto);
        assertTrue(filteredStream.findAny().isEmpty());
    }
}