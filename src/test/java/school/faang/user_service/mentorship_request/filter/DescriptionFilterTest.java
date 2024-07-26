package school.faang.user_service.mentorship_request.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.util.filter.mentorship.DescriptionFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DescriptionFilterTest {

    private final DescriptionFilter descriptionFilter = new DescriptionFilter();
    private final RequestFilterDto filterDto = new RequestFilterDto();
    private List<MentorshipRequest> requests = new ArrayList<>();

    @BeforeEach
    public void prepareRequests() {
        MentorshipRequest firstRequest = new MentorshipRequest();
        firstRequest.setDescription("abcde");
        MentorshipRequest secondRequest = new MentorshipRequest();
        secondRequest.setDescription("defgh");
        MentorshipRequest thirdRequest = new MentorshipRequest();
        thirdRequest.setDescription("njut");
        requests = List.of(firstRequest, secondRequest, thirdRequest);
    }

    @Test
    public void testDescriptionFilterIsApplicable() {
        filterDto.setDescriptionPattern("de");

        boolean isApplicable = descriptionFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testDescriptionFilterDoesNotIsApplicable() {

        boolean isApplicable = descriptionFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testDescriptionFilterApplied() {
        filterDto.setDescriptionPattern("de");
        MentorshipRequest firstRequest = new MentorshipRequest();
        firstRequest.setDescription("abcde");
        MentorshipRequest secondRequest = new MentorshipRequest();
        secondRequest.setDescription("defgh");
        List<MentorshipRequest> testResultRequests = List.of(firstRequest, secondRequest);

        Stream<MentorshipRequest> filterResult = descriptionFilter.apply(requests.stream(), filterDto);
        assertEquals(filterResult.toList(), testResultRequests);
    }
}
