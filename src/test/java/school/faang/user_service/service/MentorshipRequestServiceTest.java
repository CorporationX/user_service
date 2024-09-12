package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;
import school.faang.user_service.dto_mentorship.RejectionDto;
import school.faang.user_service.dto_mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.mentorship.filter.DescriptionFilter;
import school.faang.user_service.repository.mentorship.filter.MentorshipRequestFilter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilters;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    private AutoCloseable autoCloseable;
    private MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();

    @Captor
    private ArgumentCaptor<MentorshipRequest> argumentCaptor;

    @Test
    public void testRequestMentorshipGetDescriptionNotNull() {
        prepareData(null);
    }

    @Test
    public void testRequestMentorshipWithExistingDescription() {
        prepareData("  ");
    }

    private void prepareData(String string) {
        mentorshipRequestDto.setDescription(string);

        assertThrows(NoSuchElementException.class,
                () -> mentorshipRequestService.mentorshipRequestValidation(mentorshipRequestDto));
    }

    @Test
    public void testRequestMentorshipFindByIdRequester() {
        when(userRepository.findById(mentorshipRequestDto.getRequesterId())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () ->  mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testGivenNonExistReceiverWhenFindByIdThenThrowException() {
        mentorshipRequestDto.setDescription("Description");

        when(userRepository.findById(mentorshipRequestDto.getRequesterId())).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(mentorshipRequestDto.getReceiverId())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () ->  mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testCheckingForIdenticalIdsUsers() {
        User user1 = new User();
        User user2 = new User();
        user1.setId(1l);
        user2.setId(1l);
        MentorshipRequest requestEntity = mock(MentorshipRequest.class);

        when(requestEntity.getRequester()).thenReturn(user1);
        when(requestEntity.getReceiver()).thenReturn(user2);

        assertThrows(NoSuchElementException.class, () ->
                        mentorshipRequestService.checkingForIdenticalIdsUsers(requestEntity));

    }

    @Test
    public void testCheckingForIdenticalIdsUsersDoesNotThrowException() {
        User user1 = new User();
        User user2 = new User();
        user1.setId(1l);
        user2.setId(2l);
        MentorshipRequest requestEntity = mock(MentorshipRequest.class);

        when(requestEntity.getRequester()).thenReturn(user1);
        when(requestEntity.getReceiver()).thenReturn(user2);

        assertDoesNotThrow(() ->
                        mentorshipRequestService.checkingForIdenticalIdsUsers(requestEntity));
    }

    @Test
    public void testGetRequestsFilters() {
        List<MentorshipRequest> requests = prepareRequests();
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);

        DescriptionFilter filter = new DescriptionFilter();
        List<MentorshipRequestFilter> filters = new ArrayList<>();
        filters.add(filter);

        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setDescription("First");

        MentorshipRequestDto expected = new MentorshipRequestDto();
        expected.setDescription("First");
        expected.setId(0l);
        expected.setStatus(RequestStatus.ACCEPTED);
        expected.setRequesterId(1l);
        expected.setReceiverId(2l);

        mentorshipRequestService = new MentorshipRequestService(mentorshipRequestRepository, userRepository,
                mentorshipRequestMapper, filters);

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filterDto);

        assertThat(result).isEqualTo(List.of(expected));
    }

    @Test
    public void testSpamCheckNoRequestWithinThreeMonths() {
        mentorshipRequestDto.setRequesterId(1L);

        MentorshipRequest oldRequest = new MentorshipRequest();
        oldRequest.setCreatedAt(LocalDateTime.now().minusMonths(4));

        when(mentorshipRequestRepository.findAllByRequesterId(1L)).thenReturn(Collections.singletonList(oldRequest));
        assertDoesNotThrow(() -> mentorshipRequestService.spamCheck(mentorshipRequestDto));
    }

    @Test
    public void testSpamCheckRequestWithinThreeMonths() {
        mentorshipRequestDto.setRequesterId(1L);

        MentorshipRequest recentRequest = new MentorshipRequest();
        recentRequest.setCreatedAt(LocalDateTime.now().minusMonths(2));

        when(mentorshipRequestRepository.findAllByRequesterId(1L)).thenReturn(Collections.singletonList(recentRequest));
        assertThrows(RuntimeException.class, () -> mentorshipRequestService.spamCheck(mentorshipRequestDto));
    }

    @Test
    public void testSpamCheckNoRequestsFound() {
        mentorshipRequestDto.setRequesterId(1L);

        when(mentorshipRequestRepository.findAllByRequesterId(1L)).thenReturn(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> mentorshipRequestService.spamCheck(mentorshipRequestDto));
    }

    private List<MentorshipRequest> prepareRequests() {
        List<MentorshipRequest> requests = new ArrayList<>();

        User requester1 = new User();
        User receiver1 = new User();
        User requester2 = new User();
        User receiver2 = new User();

        requester1.setId(1L);
        receiver1.setId(2L);
        requester2.setId(3L);
        receiver2.setId(4L);
        requester1.setUsername("asdasd");
        receiver1.setUsername("asdasd");

        MentorshipRequest firstRequest = new MentorshipRequest();
        firstRequest.setRequester(requester1);
        firstRequest.setReceiver(receiver1);
        firstRequest.setDescription("First");
        firstRequest.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequest secondRequest = new MentorshipRequest();
        secondRequest.setRequester(requester2);
        secondRequest.setReceiver(receiver2);
        secondRequest.setDescription("Second");
        secondRequest.setStatus(RequestStatus.ACCEPTED);


        requests.add(firstRequest);
        requests.add(secondRequest);

        return requests;
    }

    @Test
    public void testGetRequestByIdDoesNotException() {
        long id = 1l;
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));

        assertDoesNotThrow(() -> mentorshipRequestService.getRequestById(id));
    }

    @Test
    public void testGetRequestByIdException() {
        long id = 1l;
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> mentorshipRequestService.getRequestById(id));
    }

    @Test
    public void testAcceptRequest() {
        long id = 1l;
        List<MentorshipRequest> requests = prepareRequests();
        MentorshipRequest request = requests.get(0);
        request.setStatus(RequestStatus.PENDING);
        User requester = request.getRequester();
        requester.setMentors(new ArrayList<>());

        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(request));
        when(mentorshipRequestRepository.existAcceptedRequest(request.getRequester().getId(),
                request.getReceiver().getId())).thenReturn(false);

        mentorshipRequestService.acceptRequest(id);

        assertTrue(request.getRequester().getMentors().contains(request.getReceiver()));
        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
    }

    @Test
    public void testAcceptRequestAlreadyAccepted() {
        long id = 1L;
        User requester = new User();
        User receiver = new User();
        requester.setId(1L);
        receiver.setId(2L);

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.existAcceptedRequest(requester.getId(), receiver.getId())).thenReturn(true);
        assertThrows(NoSuchElementException.class, () -> mentorshipRequestService.acceptRequest(id));
    }

    @Test
    void rejectRequestSuccess() {
        long id = 1L;
        String rejectionReason = "The request was not suitable.";
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason(rejectionReason);

        MentorshipRequest entity = new MentorshipRequest();
        entity.setDescription("A valid description");

        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(entity));

        RejectionDto expectedDto = new RejectionDto();
        expectedDto.setRejectionReason(rejectionReason);
        when(mentorshipRequestMapper.toDto(id, entity)).thenReturn(expectedDto);

        RejectionDto result = mentorshipRequestService.rejectRequest(id, rejectionDto);

        assertNotNull(result);
        assertEquals(rejectionReason, result.getRejectionReason());
        assertEquals(RequestStatus.REJECTED, entity.getStatus());
        assertEquals(rejectionReason, entity.getRejectionReason());
    }

    @Test
    void rejectRequestNoDescription() {
        long id = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("Some reason");

        MentorshipRequest entity = new MentorshipRequest();
        entity.setDescription(null);

        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(NoSuchElementException.class, () ->
            mentorshipRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    void rejectRequestEmptyDescription() {
        long id = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("Some reason");

        MentorshipRequest entity = new MentorshipRequest();
        entity.setDescription(" ");

        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(entity));
        assertThrows(NoSuchElementException.class, () ->
            mentorshipRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    void rejectRequestRequestNotFound() {
        long id = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("Some reason");

        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> mentorshipRequestService.rejectRequest(id, rejectionDto));
    }
}
