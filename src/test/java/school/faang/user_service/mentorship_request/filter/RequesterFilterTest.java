package school.faang.user_service.mentorship_request.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.RequesterFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RequesterFilterTest {

    private final RequesterFilter requesterFilter = new RequesterFilter();
    private final RequestFilterDto filterDto = new RequestFilterDto();
    private List<MentorshipRequest> requests = new ArrayList<>();

    @BeforeEach
    public void prepareRequests() {
        MentorshipRequest firstRequest = new MentorshipRequest();
        User firstRequester = new User();
        firstRequester.setId(1L);
        firstRequest.setRequester(firstRequester);
        MentorshipRequest secondRequest = new MentorshipRequest();
        User secondRequester = new User();
        secondRequester.setId(2L);
        secondRequest.setRequester(secondRequester);
        MentorshipRequest thirdRequest = new MentorshipRequest();
        User thirdRequester = new User();
        thirdRequester.setId(3L);
        thirdRequest.setRequester(thirdRequester);
        requests = List.of(firstRequest, secondRequest, thirdRequest);
    }

    @Test
    public void testRequesterFilterIsApplicable() {
        filterDto.setRequesterId(3L);

        boolean isApplicable = requesterFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testRequesterFilterDoesNotIsApplicable() {

        boolean isApplicable = requesterFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testRequesterFilterApplied() {
        filterDto.setRequesterId(2L);
        User requester = new User();
        requester.setId(2L);
        MentorshipRequest firstRequest = new MentorshipRequest();
        firstRequest.setRequester(requester);
        List<MentorshipRequest> testResultRequests = List.of(firstRequest);

        Stream<MentorshipRequest> filterResult = requesterFilter.apply(requests.stream(), filterDto);
        assertEquals(filterResult.toList(), testResultRequests);
    }
}
