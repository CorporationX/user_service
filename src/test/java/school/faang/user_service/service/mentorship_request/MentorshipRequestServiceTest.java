package school.faang.user_service.service.mentorship_request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.entity.RequestStatus.PENDING;
import static school.faang.user_service.entity.RequestStatus.REJECTED;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.REQUEST_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private List<RequestFilter> requestFilters;
    @Mock
    private MentorshipRequestParametersChecker validator;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    private RequestFilterDto requestFilterDto;
    private final List<RequestFilter> filters = List.of(
            new MentorshipRequestDescriptionFilter(),
            new MentorshipRequestRequesterFilter(),
            new MentorshipRequestReceiverFilter(),
            new MentorshipRequestStatusFilter()
    );
    private User user1;
    private User user2;
    private User user3;


    @BeforeEach
    void setUp() {
        user1 = createUser(1L);
        user2 = createUser(2L);
        user3 = createUser(3L);
    }

    @Test
    void testMentorshipRequestIsSaved() {
        long requesterId = 1L;
        long receiverId = 2L;
        String description = "description";

        mentorshipRequestService.requestMentorship(requesterId, receiverId, description);

        verify(validator, times(1))
                .checkRequestParams(requesterId, receiverId, description);
        verify(mentorshipRequestRepository, times(1))
                .create(requesterId, receiverId, description);
    }

    @Test
    void testGetRequests() {
        List<MentorshipRequest> requests = List.of(
                createMentorshipRequest(user1, user3, REJECTED),
                createMentorshipRequest(user1, user2, PENDING),
                createMentorshipRequest(user2, user3, ACCEPTED)
        );
        whenRepositoryFindAllAndRequestFilterStream(requests);
        setElementsToRequestFilterDto(1L, 2L, null);

        List<MentorshipRequest> result = mentorshipRequestService.getRequests(requestFilterDto);

        assertEquals(1, result.size());
        MentorshipRequest expected = requests.get(1);
        assertEquals(expected, result.get(0));
    }

    @Test
    void testGetRequestsWhenEmptyList() {
        List<MentorshipRequest> requests = List.of(
                createMentorshipRequest(user1, user3, REJECTED),
                createMentorshipRequest(user1, user2, PENDING),
                createMentorshipRequest(user2, user3, REJECTED)
        );
        whenRepositoryFindAllAndRequestFilterStream(requests);
        setElementsToRequestFilterDto(null, 3L, ACCEPTED);

        List<MentorshipRequest> result = mentorshipRequestService.getRequests(requestFilterDto);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testRequestByIdNotFoundWhenAcceptRequest() {
        long mentorshipRequestId = 1L;
        forTestRequestByIdNotFound(mentorshipRequestId,
                () -> mentorshipRequestService.acceptRequest(mentorshipRequestId));
    }

    @Test
    void testRequestByIdNotFoundWhenRejectRequest() {
        long mentorshipRequestId = 1L;
        String reason = "reason";
        forTestRequestByIdNotFound(mentorshipRequestId,
                () -> mentorshipRequestService.rejectRequest(mentorshipRequestId, reason));
    }

    @Test
    void testAcceptRequest() {
        user1.setMentors(new ArrayList<>());
        MentorshipRequest mentorshipRequest = createMentorshipRequest(user1, user2, PENDING);
        long mentorshipRequestId = 1L;
        whenMentorshipRequestRepositoryFindById(Optional.of(mentorshipRequest), mentorshipRequestId);

        mentorshipRequestService.acceptRequest(mentorshipRequestId);

        verify(validator, times(1))
                .checkExistAcceptedRequest(user1.getId(), user2.getId());
        assertTrue(user1.getMentors().contains(user2));
        assertEquals(mentorshipRequest.getStatus(), ACCEPTED);
    }

    @Test
    void testRejectRequest() {
        MentorshipRequest mentorshipRequest = createMentorshipRequest(user1, user2, PENDING);
        long mentorshipRequestId = 1L;
        String reason = "Some reason";
        whenMentorshipRequestRepositoryFindById(Optional.of(mentorshipRequest), mentorshipRequestId);

        mentorshipRequestService.rejectRequest(mentorshipRequestId, reason);

        assertEquals(mentorshipRequest.getStatus(), REJECTED);
        assertEquals(mentorshipRequest.getRejectionReason(), reason);
    }

    private void forTestRequestByIdNotFound(long id, Executable executable) {
        whenMentorshipRequestRepositoryFindById(Optional.empty(), id);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
        String expected = String.format(REQUEST_NOT_FOUND, id);
        assertEquals(expected, exception.getMessage());
    }

    private MentorshipRequest createMentorshipRequest(User requester, User receiver, RequestStatus status) {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(status);
        return mentorshipRequest;
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private void setElementsToRequestFilterDto(Long requesterId, Long receiverId, RequestStatus status) {
        requestFilterDto = new RequestFilterDto();
        requestFilterDto.setRequesterId(requesterId);
        requestFilterDto.setReceiverId(receiverId);
        requestFilterDto.setStatus(status);
    }

    private void whenRepositoryFindAllAndRequestFilterStream(List<MentorshipRequest> requests) {
        when(mentorshipRequestRepository
                .findAll())
                .thenReturn(requests);
        when(requestFilters.stream())
                .thenReturn(filters.stream());
    }

    private void whenMentorshipRequestRepositoryFindById(Optional<MentorshipRequest> optional, long id) {
        when(mentorshipRequestRepository
                .findById(id))
                .thenReturn(optional);
    }
}