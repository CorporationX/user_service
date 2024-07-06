package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestRequesterFilterTest {
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