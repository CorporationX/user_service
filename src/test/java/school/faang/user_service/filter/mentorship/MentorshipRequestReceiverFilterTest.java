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

class MentorshipRequestReceiverFilterTest {
    private MentorshipRequestReceiverFilter mentorshipRequestReceiverFilter;

    @BeforeEach
    public void setUp() {
        mentorshipRequestReceiverFilter = new MentorshipRequestReceiverFilter();
    }

    @Test
    @DisplayName("testing isApplicable with null receiver filter")
    public void testIsApplicableWithNullReceiverFilter() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        assertFalse(mentorshipRequestReceiverFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable with non-null receiver filter")
    public void testIsApplicableWithNonNullReceiverFilter() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setReceiverId(1L);
        assertTrue(mentorshipRequestReceiverFilter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    @DisplayName("testing filter with appropriate receiver value")
    public void testFilterWithAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .receiverId(1L).build();
        User user = User.builder()
                .id(1L).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .receiver(user).build();

        Stream<MentorshipRequest> filteredStream =
                mentorshipRequestReceiverFilter.filter(Stream.of(mentorshipRequest), mentorshipRequestFilterDto);
        assertEquals(List.of(mentorshipRequest), filteredStream.toList());
    }

    @Test
    @DisplayName("testing filter with non-appropriate receiver value")
    public void testFilterWithNonAppropriateValue() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .receiverId(1L).build();
        User user = User.builder()
                .id(2L).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .receiver(user).build();

        Stream<MentorshipRequest> filteredStream =
                mentorshipRequestReceiverFilter.filter(Stream.of(mentorshipRequest), mentorshipRequestFilterDto);
        assertTrue(filteredStream.findAny().isEmpty());
    }
}