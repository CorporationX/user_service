package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDtoForRequest;
import school.faang.user_service.dto.MentorshipRequestDtoForResponse;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.RequestException;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    MentorshipRequestRepository mentorshipRequestRepository = Mockito.mock(MentorshipRequestRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    MentorshipRequestMapper mapperMock = Mockito.mock(MentorshipRequestMapper.class);
    MentorshipRequestFilter filterMock = Mockito.mock(MentorshipRequestFilter.class);
    List<MentorshipRequestFilter> filters = List.of(filterMock);

    MentorshipRequestService mentorshipRequestService =
            new MentorshipRequestService(mentorshipRequestRepository,
                    userRepository,
                    mapperMock,
                    filters);

    private final MentorshipRequestDtoForRequest requestDto = new MentorshipRequestDtoForRequest();
    private final MentorshipRequestDtoForResponse responseDto = new MentorshipRequestDtoForResponse();
    private final RejectionDto rejectionDto = new RejectionDto();
    private final RequestFilterDto filterDto = new RequestFilterDto();
    private final MentorshipRequest request = new MentorshipRequest();
    private final User testRequester = new User();
    private final User testReceiver = new User();
    private final List<User> resultMentors = new ArrayList<>();
    private final LocalDateTime lastRequestTime = LocalDateTime.now().minusDays(85);

    private MentorshipRequestDtoForRequest prepareTestingRequestDtoForRequest() {
        requestDto.setId(1L);
        requestDto.setRequesterId(55L);
        requestDto.setReceiverId(10L);
        requestDto.setDescription("abc");
        return requestDto;
    }

    private MentorshipRequestDtoForResponse prepareTestingRequestDtoForResponse() {
        responseDto.setId(1L);
        responseDto.setRequesterId(55L);
        responseDto.setReceiverId(10L);
        responseDto.setDescription("abc");
        return responseDto;
    }

    private MentorshipRequest prepareTestingRequest() {
        request.setCreatedAt(lastRequestTime);
        request.setRequester(prepareTestingRequester());
        request.setReceiver(prepareTestingReceiver());
        return request;
    }

    private User prepareTestingRequester() {
        testRequester.setId(prepareTestingRequestDtoForRequest().getRequesterId());
        testRequester.setMentors(resultMentors);
        return testRequester;
    }

    private User prepareTestingReceiver() {
        testReceiver.setId(prepareTestingRequestDtoForRequest().getReceiverId());
        return testReceiver;
    }

    @Test
    public void testRequestMentorshipWithRequestToYourself() {
        MentorshipRequestDtoForRequest dto = new MentorshipRequestDtoForRequest();
        dto.setRequesterId(100L);
        dto.setReceiverId(100L);

        assertThrows(RequestException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    public void testRequestMentorshipIfRequesterDoesNotExist() {
        MentorshipRequestDtoForRequest dto = prepareTestingRequestDtoForRequest();
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(false);

        assertThrows(RequestException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    public void testRequestMentorshipIfReceiverDoesNotExist() {
        MentorshipRequestDtoForRequest dto = prepareTestingRequestDtoForRequest();
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(false);

        assertThrows(RequestException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    public void testRequestMentorshipWithEarlyRequest() {
        MentorshipRequestDtoForRequest dto = prepareTestingRequestDtoForRequest();
        MentorshipRequest request = prepareTestingRequest();
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(dto.getReceiverId())).thenReturn(true);
        when(mentorshipRequestRepository
                .findLatestRequest(dto.getRequesterId(), dto.getReceiverId())).thenReturn(Optional.of(request));

        assertThrows(RequestException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    public void testRequestMentorshipSuccessful() {
        MentorshipRequestDtoForRequest dto = prepareTestingRequestDtoForRequest();
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(dto.getReceiverId())).thenReturn(true);

        mentorshipRequestService.requestMentorship(dto);

        verify(mentorshipRequestRepository, times(1))
                .create(dto.getRequesterId(),
                        dto.getReceiverId(),
                        dto.getDescription());
    }

    @Test
    public void testGetRequestApplyDescriptionFilter() {
        Stream<MentorshipRequest> requestStream = Stream.of(new MentorshipRequest());
        Stream<MentorshipRequestDtoForResponse> requestDtoStream = Stream.of(new MentorshipRequestDtoForResponse());
        MentorshipRequestDtoForResponse responseDto = prepareTestingRequestDtoForResponse();
        when(filters.get(0).isApplicable(new RequestFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(requestStream);
        when(mapperMock.toDto(requestStream)).thenReturn(requestDtoStream);
        when(mapperMock.toDto(request)).thenReturn(responseDto);

        List<MentorshipRequestDtoForResponse> methodResult = mentorshipRequestService.getRequests(filterDto);

        assertEquals(methodResult, List.of(responseDto));
    }

    @Test
    public void testAcceptRequestIfRequestDoesNotFind() {
        MentorshipRequestDtoForRequest dto = prepareTestingRequestDtoForRequest();
        Optional<MentorshipRequest> opt = mentorshipRequestRepository.findById(dto.getId());
        when(opt).thenReturn(null);

        assertTrue(opt.isEmpty());
    }

    @Test
    public void testAcceptRequestIfMentorAlreadyExist() {
        MentorshipRequestDtoForRequest dto = prepareTestingRequestDtoForRequest();
        MentorshipRequest mr = prepareTestingRequest();
        User receiver = prepareTestingReceiver();
        resultMentors.add(receiver);
        Optional<MentorshipRequest> opt = mentorshipRequestRepository.findById(dto.getId());
        when(mentorshipRequestRepository.findById(dto.getId())).thenReturn(Optional.of(mr));

        assertThrows(RequestException.class, () -> mentorshipRequestService.acceptRequest(dto.getId()));
    }

    @Test
    public void testAcceptRequestSuccessful() {
        MentorshipRequestDtoForRequest dto = prepareTestingRequestDtoForRequest();
        MentorshipRequest mr = prepareTestingRequest();
        when(mentorshipRequestRepository.findById(dto.getId())).thenReturn(Optional.of(mr));

        mentorshipRequestService.acceptRequest(dto.getId());

        assertEquals(mr.getRequester().getMentors(), resultMentors);
        assertEquals(mr.getStatus(), RequestStatus.ACCEPTED);
    }

    @Test
    public void testRejectRequestIfRequestDoesNotFind() {
        MentorshipRequestDtoForRequest dto = prepareTestingRequestDtoForRequest();
        Optional<MentorshipRequest> opt = mentorshipRequestRepository.findById(dto.getId());
        when(opt).thenReturn(null);

        assertTrue(opt.isEmpty());
    }

    @Test
    public void testRejectRequestSuccessful() {
        MentorshipRequestDtoForRequest dto = prepareTestingRequestDtoForRequest();
        rejectionDto.setReason("def");
        MentorshipRequest mr = prepareTestingRequest();
        when(mentorshipRequestRepository.findById(dto.getId())).thenReturn(Optional.of(mr));

        mentorshipRequestService.rejectRequest(dto.getId(), rejectionDto);

        assertEquals(request.getRejectionReason(), rejectionDto.getReason());
        assertEquals(request.getStatus(), RequestStatus.REJECTED);
    }
}
