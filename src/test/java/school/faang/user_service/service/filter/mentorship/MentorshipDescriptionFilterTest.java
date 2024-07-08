package school.faang.user_service.service.filter.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipDescriptionFilterTest {

    @InjectMocks
    private MentorshipDescriptionFilter mentorshipDescriptionFilter;

    @Test
    void testIsApplicable_should_return_true_with_pattern_description() {
        var requestFilterDto = new RequestFilterDto();
        requestFilterDto.setDescriptionPattern("mentor");
        assertTrue(mentorshipDescriptionFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicable_should_return_false_without_any_pattern() {
        var requestFilterDto = new RequestFilterDto();
        assertFalse(mentorshipDescriptionFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply_should_filter_if_pattern_is_found() {
        MentorshipRequest req1 = new MentorshipRequest();
        req1.setDescription("Looking for a mentor");
        MentorshipRequest req2 = new MentorshipRequest();
        req2.setDescription("Need help with coding");
        MentorshipRequest req3 = new MentorshipRequest();
        req3.setDescription("Mentor needed for project");
        Stream<MentorshipRequest> requests = Stream.of(req1, req2, req3);
        var requestFilterDto = new RequestFilterDto();
        requestFilterDto.setDescriptionPattern("mentor");

        List<MentorshipRequest> filteredRequests = mentorshipDescriptionFilter
                .apply(requests, requestFilterDto)
                .toList();

        assertEquals(2, filteredRequests.size());
        assertTrue(filteredRequests.contains(req1));
        assertFalse(filteredRequests.contains(req2));
        assertTrue(filteredRequests.contains(req3));
    }
}