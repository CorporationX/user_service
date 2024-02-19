package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDro;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.filter.mentorship_request.RequestDescriptionFilter;
import school.faang.user_service.filter.mentorship_request.RequestReceiverFilter;
import school.faang.user_service.filter.mentorship_request.RequestStatusFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.publisher.MentorshipEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.MentorshipValidator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    private MentorshipRequestService service;

    @Mock
    private MentorshipRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipRequestValidator requestValidator;

    @Mock
    private MentorshipValidator mentorshipValidator;
    @Mock
    private MentorshipEventPublisher mentorshipEventPublisher;


    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = new MentorshipRequestMapperImpl();

    private List<MentorshipRequestFilter> mentorshipRequestFilters = new ArrayList<>(List.of(
            new RequestReceiverFilter(),
            new RequestDescriptionFilter(),
            new RequestStatusFilter()
    ));

    @BeforeEach
    void setUp() {
        service = new MentorshipRequestService(requestRepository, userRepository, mentorshipRepository,requestValidator,
                mentorshipValidator, mentorshipRequestFilters, mentorshipRequestMapper, mentorshipEventPublisher);
    }


    // Тесты для requestMentorship
    @Test
    void requestMentorship_Successful() {
        long requesterId = 1L;
        long receiverId = 2L;
        MentorshipRequestDto requestDto = new MentorshipRequestDto(1L, 2L, "Description");

        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(userRepository.existsById(receiverId)).thenReturn(true);

        service.requestMentorship(requestDto);

        verify(requestValidator).validateUserIds(requesterId, receiverId);
        verify(requestValidator).validateRequestTime(requesterId, receiverId);
        verify(requestRepository).create(1L, 2L, requestDto.getDescription());
    }

    @Test
    void requestMentorship_UserNotFound() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto(1L, 2L, "Description");
        when(userRepository.existsById(1L)).thenReturn(false);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> service.requestMentorship(requestDto));

        assertEquals("Нет пользователя с таким айди", dataValidationException.getMessage());
    }

    @Test
    void requestMentorship_InvalidRequestTime() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto(1L, 2L, "Description");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        doThrow(new DataValidationException("Запрос можно отправлять раз в три месяца")).when(requestValidator).validateRequestTime(1L, 2L);

        assertThrows(DataValidationException.class, () -> service.requestMentorship(requestDto));
    }

    // Тесты для getRequests
    @Test
    void getRequests_WithFilters() {
        RequestFilterDro filter = new RequestFilterDro();
        filter.setDescriptionPattern("description");
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setDescription("description");
        request2.setDescription("unknown");
        List<MentorshipRequest> requests = Arrays.asList(request1, request2);
        when(requestRepository.findAll()).thenReturn(requests);

        List<MentorshipRequestDto> result = service.getRequests(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRequests_NoFilters() {
        RequestFilterDro filter = new RequestFilterDro();
        List<MentorshipRequest> requests = Collections.emptyList();
        when(requestRepository.findAll()).thenReturn(requests);

        List<MentorshipRequestDto> result = service.getRequests(filter);

        assertTrue(result.isEmpty());
    }

    // Тесты для acceptRequest
    @Test
    void acceptRequest_Successful() {
        long requestId = 1L;
        User requester = mock(User.class);
        User receiver = mock(User.class);
        when(requester.getId()).thenReturn(2L);
        when(receiver.getId()).thenReturn(3L);

        MentorshipRequest foundRequest = new MentorshipRequest();
        foundRequest.setRequester(requester);
        foundRequest.setReceiver(receiver);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(foundRequest));

        service.acceptRequest(requestId);

        verify(requestRepository).save(foundRequest);
        assertEquals(RequestStatus.ACCEPTED, foundRequest.getStatus());
        verify(mentorshipRepository).save(any(Mentorship.class));
    }

    @Test
    void acceptRequest_RequestNotFound() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> service.acceptRequest(1L));

        assertEquals("Такого реквеста не существует", dataValidationException.getMessage());
    }


    // Тесты для rejectRequest
    @Test
    void rejectRequest_SuccessfulRejection() {
        MentorshipRequest mockRequest = mock(MentorshipRequest.class);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(mockRequest));

        RejectionDto rejection = new RejectionDto("Not interested");

        service.rejectRequest(1L, rejection);

        verify(mockRequest).setStatus(RequestStatus.REJECTED);
        verify(mockRequest).setRejectionReason(rejection.getReason());
    }


    @Test
    void rejectRequest_RequestNotFound() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> service.rejectRequest(1L, new RejectionDto()));

        assertEquals("Такого реквеста не существует", dataValidationException.getMessage());
    }

    // тесты validateExistsUsers
    @Test
    void validateExistsUsers_BothUsersExist() {
        long requesterId = 1L;
        long receiverId = 2L;
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(userRepository.existsById(receiverId)).thenReturn(true);

        service.validateExistsUsers(requesterId, receiverId);

        verify(userRepository).existsById(requesterId);
        verify(userRepository).existsById(receiverId);
    }

    @Test
    void validateExistsUsers_RequesterDoesNotExist() {
        long requesterId = 1L;
        long receiverId = 2L;
        when(userRepository.existsById(requesterId)).thenReturn(false);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> service.validateExistsUsers(requesterId, receiverId));

        assertEquals("Нет пользователя с таким айди", dataValidationException.getMessage());
    }

    @Test
    void validateExistsUsers_ReceiverDoesNotExist() {
        long requesterId = 1L;
        long receiverId = 2L;
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(userRepository.existsById(receiverId)).thenReturn(false);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> service.validateExistsUsers(requesterId, receiverId));

        assertEquals("Нет пользователя с таким айди", dataValidationException.getMessage());
    }

    @Test
    void validateExistsUsers_NeitherUserExists() {
        long requesterId = 1L;
        long receiverId = 2L;
        Mockito.lenient().when(userRepository.existsById(requesterId)).thenReturn(false);
        Mockito.lenient().when(userRepository.existsById(receiverId)).thenReturn(false);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> service.validateExistsUsers(requesterId, receiverId));

        assertEquals("Нет пользователя с таким айди", dataValidationException.getMessage());
    }
}
