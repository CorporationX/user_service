package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MentorshipRequestNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Spy
    private MentorshipRequestMapperImpl requestMapper;
    @Mock
    private MentorshipRequestRepository requestRepository;
    @Mock
    private MentorshipRepository mentorshipRepository;
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
                .id(1L)
                .receiver(receiverDto)
                .requester(requesterDto)
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .description("some description")
                .build();

        filterDto = RequestFilterDto.builder()
                .requester(requesterDto)
                .build();

        requester = new User();
        requester.setId(CORRECT_REQUESTER_ID);
        receiver = new User();
        receiver.setId(CORRECT_RECEIVER_ID);
    }

    @Test
    void testUserValidateWithNotFoundRequester() {
        incorrectRequestDto.setRequester(incorrectUserDto);
        incorrectRequestDto.setReceiver(receiverDto);

        when(mentorshipRepository.existsById(NOT_FOUND_USER_ID)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> requestService.requestMentorship(incorrectRequestDto));
    }

    @Test
    void testUserValidateWithNotFoundReceiver() {
        incorrectRequestDto.setReceiver(incorrectUserDto);
        incorrectRequestDto.setRequester(requesterDto);

        when(mentorshipRepository.existsById(NOT_FOUND_USER_ID)).thenReturn(false);
        when(mentorshipRepository.existsById(CORRECT_REQUESTER_ID)).thenReturn(true);
        assertThrows(UserNotFoundException.class, () -> requestService.requestMentorship(incorrectRequestDto));
    }

    @Test
    void testUserValidateWithSameUser() {
        incorrectRequestDto.setRequester(requesterDto);
        incorrectRequestDto.setReceiver(requesterDto);

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
    void testToDto() {
        latestRequest.setId(CORRECT_REQUEST_ID);
        latestRequest.setDescription("some description");
        latestRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        latestRequest.setRequester(requester);
        latestRequest.setReceiver(receiver);

        MentorshipRequestDto actualDto = requestMapper.toDto(latestRequest);
        assertEquals(correctRequestDto, actualDto);
    }

    @Test
    void testGetRequests() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requester(requesterDto)
                .receiver(receiverDto)
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .description("some description")
                .build();
        latestRequest.setRequester(requester);
        latestRequest.setReceiver(receiver);
        latestRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        latestRequest.setDescription("some description");

        when(requestRepository.findAll()).thenReturn(Collections.singleton(latestRequest));

        List<MentorshipRequestDto> actualList = requestService.getRequests(filterDto);
        List<MentorshipRequestDto> expectedList = List.of(requestDto);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testGetRequestsWithEmptyList() {
        filterDto.setRequester(receiverDto);
        latestRequest.setRequester(requester);
        latestRequest.setReceiver(receiver);
        latestRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        latestRequest.setDescription("some description");

        when(requestRepository.findAll()).thenReturn(Collections.singleton(latestRequest));

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
    void testAcceptRequestWithExistingMentor() {
        requester.setMentors(List.of(receiver));
        latestRequest.setRequester(requester);
        latestRequest.setReceiver(receiver);

        when(requestRepository.findById(CORRECT_REQUEST_ID)).thenReturn(Optional.of(latestRequest));
        assertThrows(IllegalArgumentException.class, () -> requestService.acceptRequest(CORRECT_REQUEST_ID));
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
        assertEquals(RequestStatus.ACCEPTED, latestRequest.getStatus());
    }

    private void doForMentorshipRepository() {
        when(mentorshipRepository.existsById(CORRECT_REQUESTER_ID)).thenReturn(true);
        when(mentorshipRepository.existsById(CORRECT_RECEIVER_ID)).thenReturn(true);
        when(requestRepository.findLatestRequest(CORRECT_REQUESTER_ID, CORRECT_RECEIVER_ID))
                .thenReturn(Optional.of(latestRequest));
    }
}