package school.faang.user_service.mentorship.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestFilterTest {

    private final List<MentorshipRequestFilter> mentorshipRequestFilters = new ArrayList<>();

    private MentorshipRequestFilter mentorshipRequestFilter = new MentorshipDescriptionFilter();

    private MentorshipRequest mentorshipRequest = new MentorshipRequest();

    private RequestFilterDto requestFilterDto = new RequestFilterDto();

    List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

    private User user = new User();


    @BeforeEach
    public void init() {
        user.setId(2L);
        user.setUsername("IVAN");
        mentorshipRequestFilters.add(new MentorshipDescriptionFilter());
        mentorshipRequestFilters.add(new MentorshipReceiverFilter());
        mentorshipRequestFilters.add(new MentorshipRequesterFilter());
        mentorshipRequestFilters.add(new MentorshipStatusFilter());
        mentorshipRequests = new ArrayList<>(List.of(mentorshipRequest));
    }

    @Test
    void whenIncorrectDescription() {
        mentorshipRequest.setDescription("One TWO THREE FOUR");
        requestFilterDto.setDescriptionFilter("TW45O");
        mentorshipRequestFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(requestFilterDto))
                .forEach(userFilter -> userFilter.apply(mentorshipRequests, requestFilterDto));
        Assertions.assertEquals(0, mentorshipRequests.size());
    }

    @Test
    void whenCorrectDescription() {
        mentorshipRequest.setDescription("One TWO THREE FOUR");
        requestFilterDto.setDescriptionFilter("TWO");
        mentorshipRequestFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(requestFilterDto))
                .forEach(userFilter -> userFilter.apply(mentorshipRequests, requestFilterDto));
        Assertions.assertEquals(1, mentorshipRequests.size());
    }

    @Test
    void whenIncorrectReceiver() {
        mentorshipRequest.setReceiver(user);
        requestFilterDto.setReceiverFilter(3L);
        mentorshipRequestFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(requestFilterDto))
                .forEach(userFilter -> userFilter.apply(mentorshipRequests, requestFilterDto));
        assertEquals(0, mentorshipRequests.size());
    }

    @Test
    void whenCorrectReceiver() {
        mentorshipRequest.setReceiver(user);
        requestFilterDto.setReceiverFilter(2L);
        mentorshipRequestFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(requestFilterDto))
                .forEach(userFilter -> userFilter.apply(mentorshipRequests, requestFilterDto));
        assertEquals(1, mentorshipRequests.size());
    }

    @Test
    void whenIncorrectRequester() {
        mentorshipRequest.setRequester(user);
        requestFilterDto.setRequesterFilter(4L);
        mentorshipRequestFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(requestFilterDto))
                .forEach(userFilter -> userFilter.apply(mentorshipRequests, requestFilterDto));
        assertEquals(0, mentorshipRequests.size());
    }

    @Test
    void whenCorrectRequester() {
        mentorshipRequest.setRequester(user);
        requestFilterDto.setRequesterFilter(2L);
        mentorshipRequestFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(requestFilterDto))
                .forEach(userFilter -> userFilter.apply(mentorshipRequests, requestFilterDto));
        assertEquals(1, mentorshipRequests.size());
    }

    @Test
    void whenIncorrectStatus() {
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        requestFilterDto.setStatusFilter(RequestStatus.ACCEPTED);
        mentorshipRequestFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(requestFilterDto))
                .forEach(userFilter -> userFilter.apply(mentorshipRequests, requestFilterDto));
        assertEquals(0, mentorshipRequests.size());
    }

    @Test
    void whenCorrectStatus() {
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        requestFilterDto.setStatusFilter(RequestStatus.PENDING);
        mentorshipRequestFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(requestFilterDto))
                .forEach(userFilter -> userFilter.apply(mentorshipRequests, requestFilterDto));
        assertEquals(1, mentorshipRequests.size());
    }

}