package school.faang.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestedEvent;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MentorshipRequestNotFoundException;
import school.faang.user_service.exception.RequestAlreadyAcceptedException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilter;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterByDescription;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterByReceiver;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterByRequestStatus;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterByRequester;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterByUpdatedTime;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Spy
    private MentorshipRequestMapperImpl requestMapper;

    @Mock
    private MentorshipRequestRepository requestRepository;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipRequestedEventPublisher publish;

    @InjectMocks
    private MentorshipRequestService requestService;

    private final long NOT_FOUND_USER_ID = 55555L;
    private final long CORRECT_REQUESTER_ID = 1L;
    private final long CORRECT_RECEIVER_ID = 2L;
    private final long CORRECT_REQUEST_ID = 1L;
    private MentorshipRequestDto incorrectRequestDto;
    private MentorshipRequestDto correctRequestDto;
    private MentorshipRequest latestRequest;
    private UserDto incorrectUserDto;
    private UserDto receiverDto;
    private UserDto requesterDto;
    private RequestFilterDto filterDto;
    private User requester;
    private User receiver;
    private RejectionDto rejectionDto;

    @BeforeEach
    void initData() {
        incorrectUserDto = UserDto.builder()
                .id(NOT_FOUND_USER_ID)
                .build();
        receiverDto = UserDto.builder()
                .id(CORRECT_RECEIVER_ID)
                .build();
        requesterDto = UserDto.builder()
                .id(CORRECT_REQUESTER_ID)
                .build();

        incorrectRequestDto = MentorshipRequestDto.builder().build();
        latestRequest = new MentorshipRequest();

        correctRequestDto = MentorshipRequestDto.builder()
                .id(CORRECT_REQUEST_ID)
                .receiver(CORRECT_RECEIVER_ID)
                .requester(CORRECT_REQUESTER_ID)
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .description("some description")
                .build();

        filterDto = RequestFilterDto.builder()
                .requester(CORRECT_REQUESTER_ID)
                .build();

        requester = new User();
        requester.setId(CORRECT_REQUESTER_ID);
        receiver = new User();
        receiver.setId(CORRECT_RECEIVER_ID);

        rejectionDto = RejectionDto.builder()
                .reason("reason")
                .build();
    }

    @Test
    void testUserValidateWithNotFoundRequester() {
        incorrectRequestDto.setRequester(NOT_FOUND_USER_ID);
        incorrectRequestDto.setReceiver(CORRECT_RECEIVER_ID);

        when(mentorshipRepository.existsById(NOT_FOUND_USER_ID)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> requestService.requestMentorship(incorrectRequestDto));
    }

    @Test
    void testUserValidateWithNotFoundReceiver() {
        incorrectRequestDto.setReceiver(NOT_FOUND_USER_ID);
        incorrectRequestDto.setRequester(CORRECT_REQUESTER_ID);

        when(mentorshipRepository.existsById(NOT_FOUND_USER_ID)).thenReturn(false);
        when(mentorshipRepository.existsById(CORRECT_REQUESTER_ID)).thenReturn(true);
        assertThrows(UserNotFoundException.class, () -> requestService.requestMentorship(incorrectRequestDto));
    }

    @Test
    void testUserValidateWithSameUser() {
        incorrectRequestDto.setRequester(CORRECT_REQUESTER_ID);
        incorrectRequestDto.setReceiver(CORRECT_REQUESTER_ID);

        when(mentorshipRepository.existsById(CORRECT_REQUESTER_ID)).thenReturn(true);
        assertThrows(DataValidationException.class, () -> requestService.requestMentorship(incorrectRequestDto));
    }

    @Test
    void testDataValidateWithRequestWithLessThen3Month() {
        latestRequest.setUpdatedAt(LocalDateTime.now().minusMonths(1));

        doForMentorshipRepository();
        assertThrows(DataValidationException.class, () -> requestService.requestMentorship(correctRequestDto));
    }

    @Test
    void testRequestMentorshipWithCorrectData() {
        latestRequest.setUpdatedAt(LocalDateTime.now().minusMonths(4));

        doForMentorshipRepository();
        requestService.requestMentorship(correctRequestDto);

        verify(requestRepository, times(1))
                .create(CORRECT_REQUESTER_ID, CORRECT_RECEIVER_ID, "some description");
    }

    @Test
    void testGetRequests() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requester(CORRECT_REQUESTER_ID)
                .receiver(CORRECT_RECEIVER_ID)
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .description("some description")
                .build();
        latestRequest.setRequester(requester);
        latestRequest.setReceiver(receiver);
        latestRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        latestRequest.setDescription("some description");

        when(requestRepository.findAll()).thenReturn(Collections.singleton(latestRequest));
        List<MentorshipRequestFilter> filters = getFilters();
        requestService = new MentorshipRequestService(requestRepository, mentorshipRepository, requestMapper, publish, getFilters());

        List<MentorshipRequestDto> actualList = requestService.getRequests(filterDto);
        List<MentorshipRequestDto> expectedList = List.of(requestDto);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testGetRequestsWithEmptyList() {
        filterDto.setRequester(CORRECT_RECEIVER_ID);
        latestRequest.setRequester(requester);
        latestRequest.setReceiver(receiver);
        latestRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        latestRequest.setDescription("some description");

        when(requestRepository.findAll()).thenReturn(Collections.singleton(latestRequest));
        List<MentorshipRequestFilter> filters = getFilters();
        requestService = new MentorshipRequestService(requestRepository, mentorshipRepository, requestMapper, publish, getFilters());

        List<MentorshipRequestDto> actualList = requestService.getRequests(filterDto);
        List<MentorshipRequestDto> expectedList = new ArrayList<>();

        assertEquals(expectedList, actualList);
    }

    @Test
    void testAcceptRequestWithoutRequest() {
        when(requestRepository.findById(CORRECT_REQUEST_ID)).thenReturn(Optional.empty());
        assertThrows(MentorshipRequestNotFoundException.class, () -> requestService.acceptRequest(CORRECT_REQUEST_ID));
    }

    @Test
    void testAcceptRequestWithAlreadyAcceptedRequest() {
        latestRequest.setStatus(ACCEPTED);

        when(requestRepository.findById(CORRECT_REQUEST_ID)).thenReturn(Optional.of(latestRequest));
        assertThrows(RequestAlreadyAcceptedException.class, () -> requestService.acceptRequest(CORRECT_REQUEST_ID));
    }

    @Test
    void testAcceptRequestWithExistingMentor() {
        requester.setMentors(List.of(receiver));
        latestRequest.setRequester(requester);
        latestRequest.setReceiver(receiver);

        when(requestRepository.findById(CORRECT_REQUEST_ID)).thenReturn(Optional.of(latestRequest));
        assertThrows(RequestAlreadyAcceptedException.class, () -> requestService.acceptRequest(CORRECT_REQUEST_ID));
    }

    @Test
    void testAcceptRequest() {
        latestRequest.setReceiver(receiver);
        latestRequest.setRequester(requester);

        when(requestRepository.findById(CORRECT_REQUEST_ID)).thenReturn(Optional.of(latestRequest));
        requestService.acceptRequest(CORRECT_REQUEST_ID);

        List<User> actualMentorList = requester.getMentors();
        List<User> expectedUserList = List.of(receiver);

        assertEquals(expectedUserList, actualMentorList);
        assertEquals(ACCEPTED, latestRequest.getStatus());
    }

    @Test
    void testRejectRequestWithoutRequest() {
        when(requestRepository.findById(CORRECT_REQUEST_ID)).thenReturn(Optional.empty());
        assertThrows(MentorshipRequestNotFoundException.class, () -> requestService.rejectRequest(CORRECT_REQUEST_ID, rejectionDto));
    }

    @Test
    void testRejectRequestWithAlreadyAcceptedRequest() {
        latestRequest.setStatus(RequestStatus.REJECTED);

        when(requestRepository.findById(CORRECT_REQUEST_ID)).thenReturn(Optional.of(latestRequest));
        assertThrows(RequestAlreadyAcceptedException.class, () -> requestService.rejectRequest(CORRECT_REQUEST_ID, rejectionDto));
    }

    @Test
    void testRejectRequest() {
        latestRequest.setReceiver(receiver);
        latestRequest.setRequester(requester);
        requester.setMentors(new ArrayList<>(List.of(receiver)));
        receiver.setMentees(new ArrayList<>(List.of(requester)));

        when(requestRepository.findById(CORRECT_REQUEST_ID)).thenReturn(Optional.of(latestRequest));
        requestService.rejectRequest(CORRECT_REQUEST_ID, rejectionDto);

        List<User> actualMentorList = requester.getMentors();
        List<User> expectedMenotrList = new ArrayList<>();
        List<User> actualMenteeList = receiver.getMentees();
        List<User> expectedMenteeList = new ArrayList<>();

        assertEquals(expectedMenotrList, actualMentorList);
        assertEquals(expectedMenteeList, actualMenteeList);
        assertEquals(RequestStatus.REJECTED, latestRequest.getStatus());
    }

    private void doForMentorshipRepository() {
        when(mentorshipRepository.existsById(CORRECT_REQUESTER_ID)).thenReturn(true);
        when(mentorshipRepository.existsById(CORRECT_RECEIVER_ID)).thenReturn(true);
        when(requestRepository.findLatestRequest(CORRECT_REQUESTER_ID, CORRECT_RECEIVER_ID))
                .thenReturn(Optional.of(latestRequest));
    }

    private List<MentorshipRequestFilter> getFilters() {
        return List.of(new MentorshipRequestFilterByDescription(), new MentorshipRequestFilterByReceiver(),
                new MentorshipRequestFilterByRequester(), new MentorshipRequestFilterByRequestStatus(),
                new MentorshipRequestFilterByUpdatedTime());
    }

    @Test
    void publishEventMentorshipTest() {
        when(mentorshipRepository.existsById(CORRECT_REQUESTER_ID)).thenReturn(true);
        when(mentorshipRepository.existsById(CORRECT_RECEIVER_ID)).thenReturn(true);
        MentorshipRequestedEvent event = new MentorshipRequestedEvent(1L, 2L, LocalDateTime.now());
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .id(CORRECT_REQUEST_ID)
                .receiver(CORRECT_RECEIVER_ID)
                .requester(CORRECT_REQUESTER_ID)
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .description("some description")
                .build();
        MentorshipRequestedEventPublisher publisher = mock(MentorshipRequestedEventPublisher.class);
        MentorshipRequestService service = new MentorshipRequestService(requestRepository, mentorshipRepository, requestMapper, publisher, getFilters());
        service.requestMentorship(dto);
        verify(publisher).publish(event);
    }
}