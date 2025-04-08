package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.mentorship.MentorshipRequestDescriptionFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorshipRequestDescriptionFilterTest {
    private MentorshipRequestDescriptionFilter filter;
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        filter = new MentorshipRequestDescriptionFilter();
        requestFilterDto = new RequestFilterDto();
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setDescription("test");

        assertTrue(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalseWhenDescriptionIsNull() {
        filter.isApplicable(requestFilterDto);

        assertFalse(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalseWhenDescriptionIsBlank() {
        requestFilterDto.setDescription("   ");

        assertFalse(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalseWhenDescriptionIsEmpty() {
        requestFilterDto.setDescription("");

        assertFalse(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testApplyReturnsOnlyAcceptedRequestsWhenFilteredByStatus() {
        requestFilterDto.setDescription("test");

        MentorshipRequest mentorshipRequestFirst = new MentorshipRequest();
        mentorshipRequestFirst.setDescription("test");

        MentorshipRequest mentorshipRequestSecond = new MentorshipRequest();
        mentorshipRequestSecond.setDescription("testDescription");

        Stream<MentorshipRequest> mentorshipRequestStream = Stream.of(mentorshipRequestFirst, mentorshipRequestSecond);

        Stream<MentorshipRequest> mentorshipRequestStreamResult = filter
                .apply(mentorshipRequestStream, requestFilterDto);

        assertEquals(
                mentorshipRequestFirst.getDescription(),
                mentorshipRequestStreamResult.findFirst().get().getDescription());
    }

    @Test
    void testApplyReturnsRequestsWhenDescriptionMatchesIgnoreCase() {
        requestFilterDto.setDescription("test");

        MentorshipRequest mentorshipRequestFirst = new MentorshipRequest();
        mentorshipRequestFirst.setDescription("tEsT");

        Stream<MentorshipRequest> mentorshipRequestStream = Stream.of(mentorshipRequestFirst);

        Stream<MentorshipRequest> mentorshipRequestStreamResult = filter
                .apply(mentorshipRequestStream, requestFilterDto);

        assertEquals(
                mentorshipRequestFirst.getDescription(),
                mentorshipRequestStreamResult.findFirst().get().getDescription());
    }
}