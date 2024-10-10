package school.faang.user_service.service.mentorship_request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship.DescriptionFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.RejectionMapper;
import school.faang.user_service.redis.RedisMessageMentorshipRequestsPublisher;
import school.faang.user_service.repository.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestServiceImpl;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceImplTest {
    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestValidator validator;
    @Mock
    private RedisMessageMentorshipRequestsPublisher redisMessageMentorshipRequestsPublisher;
    @Spy
    private RejectionMapper rejectionMapper = Mappers.getMapper(RejectionMapper.class);
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    private MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
    private final Long ID = 1L;

    @Test
    public void testRequestMentorship() {
        when(validator.checkingUsersInRepository(mentorshipRequestDto)).thenReturn(mentorshipRequestDto);
        assertThat(mentorshipRequestService.requestMentorship(mentorshipRequestDto)).isNotNull();
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

        mentorshipRequestService = new MentorshipRequestServiceImpl(mentorshipRequestRepository,
                mentorshipRequestMapper, filters, validator, rejectionMapper, redisMessageMentorshipRequestsPublisher);

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filterDto);

        assertThat(result).isEqualTo(List.of(expected));
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

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        when(mentorshipRequestRepository.findById(ID)).thenReturn(Optional.of(mentorshipRequest));

        assertDoesNotThrow(() -> mentorshipRequestService.getRequestById(ID));
    }

    @Test
    public void testGetRequestByIdException() {
        when(mentorshipRequestRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> mentorshipRequestService.getRequestById(ID));
    }

    @Test
    public void testAcceptRequest() {
        List<MentorshipRequest> requests = prepareRequests();
        MentorshipRequest request = requests.get(0);
        request.setStatus(RequestStatus.PENDING);
        User requester = request.getRequester();
        requester.setMentors(new ArrayList<>());

        when(mentorshipRequestRepository.findById(ID)).thenReturn(Optional.of(request));
        when(mentorshipRequestRepository.existAcceptedRequest(request.getRequester().getId(),
                request.getReceiver().getId())).thenReturn(false);

        mentorshipRequestService.acceptRequest(ID);

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
        String rejectionReason = "The request was not suitable.";
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason(rejectionReason);

        MentorshipRequest entity = new MentorshipRequest();
        entity.setDescription("A valid description");
        entity.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(ID)).thenReturn(Optional.of(entity));

        mentorshipRequestDto.setRejectionReason(rejectionReason);
        mentorshipRequestDto.setStatus(RequestStatus.REJECTED);

        RejectionDto result = mentorshipRequestService.rejectRequest(ID, rejectionDto);

        assertNotNull(result);
        assertEquals(rejectionReason, result.getRejectionReason());
        assertEquals(RequestStatus.REJECTED, entity.getStatus());
        assertEquals(rejectionReason, entity.getRejectionReason());
    }

    @Test
    void rejectRequestNoDescription() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("Some reason");

        MentorshipRequest entity = new MentorshipRequest();
        entity.setDescription(null);

        when(mentorshipRequestRepository.findById(ID)).thenReturn(Optional.of(entity));

        assertThrows(NoSuchElementException.class, () ->
                mentorshipRequestService.rejectRequest(ID, rejectionDto));
    }

    @Test
    void rejectRequestEmptyDescription() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("Some reason");

        MentorshipRequest entity = new MentorshipRequest();
        entity.setDescription(" ");

        when(mentorshipRequestRepository.findById(ID)).thenReturn(Optional.of(entity));
        assertThrows(NoSuchElementException.class, () ->
                mentorshipRequestService.rejectRequest(ID, rejectionDto));
    }

    @Test
    void rejectRequestRequestNotFound() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("Some reason");

        when(mentorshipRequestRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> mentorshipRequestService.rejectRequest(ID, rejectionDto));
    }
}
