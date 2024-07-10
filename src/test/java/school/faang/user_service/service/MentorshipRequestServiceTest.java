package school.faang.user_service.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
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

    private final MentorshipRequestDto dto = new MentorshipRequestDto();
    private final RejectionDto rejectionDto = new RejectionDto();
    private final RequestFilterDto filterDto = new RequestFilterDto();
    private final MentorshipRequest request = new MentorshipRequest();
    private final User testRequester = new User();
    private final User testReceiver = new User();
    private final List<User> resultMentors = new ArrayList<>();
    private final LocalDateTime lastRequestTime = LocalDateTime.now().minusDays(85);

    private MentorshipRequestDto prepareTestingRequestDto() {
        dto.setId(1L);
        dto.setRequesterId(55L);
        dto.setReceiverId(10L);
        dto.setDescription("abc");
        return dto;
    }

    private MentorshipRequest prepareTestingRequest() {
        request.setCreatedAt(lastRequestTime);
        request.setRequester(prepareTestingRequester());
        request.setReceiver(prepareTestingReceiver());
        return request;
    }

    private User prepareTestingRequester() {
        testRequester.setId(prepareTestingRequestDto().getRequesterId());
        testRequester.setMentors(resultMentors);
        return testRequester;
    }

    private User prepareTestingReceiver() {
        testReceiver.setId(prepareTestingRequestDto().getReceiverId());
        return testReceiver;
    }

    @Test
    public void testRequestMentorshipIfRequesterDoesNotExist() {
        MentorshipRequestDto dto = prepareTestingRequestDto();
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(false);

        assertThrows(RequestException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    public void testRequestMentorshipIfReceiverDoesNotExist() {
        MentorshipRequestDto dto = prepareTestingRequestDto();
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(false);

        assertThrows(RequestException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    public void testRequestMentorshipWithRequestToYourself() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(100L);
        dto.setReceiverId(100L);

        assertThrows(RequestException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    public void testRequestMentorshipWithEarlyRequest() {
        MentorshipRequestDto dto = prepareTestingRequestDto();
        MentorshipRequest request = prepareTestingRequest();
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(dto.getReceiverId())).thenReturn(true);
        when(mentorshipRequestRepository
                .findLatestRequest(dto.getRequesterId(), dto.getReceiverId())).thenReturn(Optional.of(request));

        assertThrows(RequestException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    public void testRequestMentorshipSuccessful() {
        MentorshipRequestDto dto = prepareTestingRequestDto();
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
        Stream<MentorshipRequestDto> requestDtoStream = Stream.of(new MentorshipRequestDto());
        when(filters.get(0).isApplicable(new RequestFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(requestStream);
        when(mapperMock.toDto(requestStream)).thenReturn(requestDtoStream);
        when(mapperMock.toDto(request)).thenReturn(dto);

        List<MentorshipRequestDto> methodResult = mentorshipRequestService.getRequests(filterDto);

        assertEquals(methodResult, List.of(dto));
    }

    @Test
    public void testAcceptRequestIfRequestDoesNotExist() {
        MentorshipRequestDto dto = prepareTestingRequestDto();
        when(mentorshipRequestRepository.existsById(dto.getId())).thenReturn(false);

        assertThrows(RequestException.class, () -> mentorshipRequestService.acceptRequest(dto.getId()));
    }

    @Test
    public void testAcceptRequestIfMentorAlreadyExist() {
        MentorshipRequestDto dto = prepareTestingRequestDto();
        MentorshipRequest mr = prepareTestingRequest();
        User receiver = prepareTestingReceiver();
        resultMentors.add(receiver);
        when(mentorshipRequestRepository.existsById(dto.getId())).thenReturn(true);
        when(mentorshipRequestRepository.getReferenceById(dto.getId())).thenReturn(mr);

        assertThrows(RequestException.class, () -> mentorshipRequestService.acceptRequest(dto.getId()));
    }

    @Test
    public void testAcceptRequestSuccessful() {
        MentorshipRequestDto dto = prepareTestingRequestDto();
        MentorshipRequest mr = prepareTestingRequest();
        when(mentorshipRequestRepository.existsById(dto.getId())).thenReturn(true);
        when(mentorshipRequestRepository.getReferenceById(dto.getId())).thenReturn(mr);

        mentorshipRequestService.acceptRequest(dto.getId());

        assertEquals(mr.getRequester().getMentors(), resultMentors);
        assertEquals(mr.getStatus(), RequestStatus.ACCEPTED);
    }

    @Test
    public void testRejectRequestIfRequestDoesNotExist() {
        MentorshipRequestDto dto = prepareTestingRequestDto();
        when(mentorshipRequestRepository.existsById(dto.getId())).thenReturn(false);

        assertThrows(RequestException.class, () -> mentorshipRequestService.rejectRequest(dto.getId(), rejectionDto));
    }

    @Test
    public void testRejectRequestSuccessful() {
        MentorshipRequestDto dto = prepareTestingRequestDto();
        rejectionDto.setReason("def");
        when(mentorshipRequestRepository.existsById(dto.getId())).thenReturn(true);
        when(mentorshipRequestRepository.getReferenceById(dto.getId())).thenReturn(request);

        mentorshipRequestService.rejectRequest(dto.getId(), rejectionDto);

        assertEquals(request.getRejectionReason(), rejectionDto.getReason());
        assertEquals(request.getStatus(), RequestStatus.REJECTED);
    }
}
