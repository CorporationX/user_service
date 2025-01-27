package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestStatusFilterTest {
    private static final RequestStatus REQUEST_STATUS = RequestStatus.PENDING;
    private final MentorshipRequestStatusFilter mentorshipRequestStatusFilter = new MentorshipRequestStatusFilter();

    @Test
    public void testIsNotApplicable() {
        boolean isApplicable = mentorshipRequestStatusFilter.isApplicable(new MentorshipRequestFilterDto());
        assertFalse(isApplicable);
    }

    @Test
    public void testIsApplicable() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = prepareDataToDto(REQUEST_STATUS);
        boolean isApplicable = mentorshipRequestStatusFilter.isApplicable(mentorshipRequestFilterDto);
        assertTrue(isApplicable);
    }

    @Test
    public void testApply() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = prepareDataToDto(REQUEST_STATUS);
        Stream<MentorshipRequest> requests = prepareStreamOfRequests();
        List<MentorshipRequest> mentorshipRequests = mentorshipRequestStatusFilter.apply(requests,
                mentorshipRequestFilterDto).toList();
        assertEquals(5, mentorshipRequests.size());
    }

    private Stream<MentorshipRequest> prepareStreamOfRequests() {
        List<MentorshipRequest> mentorshipRequestList = fillListOfRequests();

        return mentorshipRequestList.stream();
    }

    private List<MentorshipRequest> fillListOfRequests() {
        List<MentorshipRequest> mentorshipRequestList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setId((long) i);
            MentorshipRequest mentorshipRequest = new MentorshipRequest();
            mentorshipRequest.setRequester(user);
            if (i < 5 ) {
                mentorshipRequest.setStatus(REQUEST_STATUS);
            }
            mentorshipRequestList.add(mentorshipRequest);
        }
        return mentorshipRequestList;
    }

    private MentorshipRequestFilterDto prepareDataToDto(RequestStatus requestStatus) {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setStatus(requestStatus);
        return mentorshipRequestFilterDto;
    }
}
