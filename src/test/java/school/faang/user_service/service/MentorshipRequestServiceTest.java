package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private MentorshipRequestDto incorrectRequestDto;
    private MentorshipRequestDto correctRequestDto;
    private MentorshipRequest latestRequest;
    private UserDto incorrectUserDto;
    private UserDto receiverDto;
    private UserDto requesterDto;

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
        User requester = new User();
        requester.setId(CORRECT_REQUESTER_ID);
        User receiver = new User();
        receiver.setId(CORRECT_RECEIVER_ID);

        latestRequest.setId(1L);
        latestRequest.setDescription("some description");
        latestRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        latestRequest.setRequester(requester);
        latestRequest.setReceiver(receiver);

        MentorshipRequestDto actualDto = requestMapper.toDto(latestRequest);
        assertEquals(correctRequestDto, actualDto);
    }

    private void doForMentorshipRepository() {
        when(mentorshipRepository.existsById(CORRECT_REQUESTER_ID)).thenReturn(true);
        when(mentorshipRepository.existsById(CORRECT_RECEIVER_ID)).thenReturn(true);
        when(requestRepository.findLatestRequest(CORRECT_REQUESTER_ID, CORRECT_RECEIVER_ID))
                .thenReturn(Optional.of(latestRequest));
    }
}