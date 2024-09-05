package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.request.MentorshipRequestService;
import school.faang.user_service.service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestedEventPublisher eventPublisher;

    MentorshipRequest mentorshipRequest;
    MentorshipRequestDto requestDto;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @BeforeEach
    public void setUp() {
        mentorshipRequest = new MentorshipRequest();
        requestDto = new MentorshipRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setDescription("I need guidance on my project");
    }

    @Test
    public void testRequestMentorship_Success() throws Exception {
        when(mentorshipRequestRepository.create(requestDto.getRequesterId(), requestDto.getReceiverId(), requestDto.getDescription())).thenReturn(new MentorshipRequest());
        mentorshipRequestService.requestMentorship(requestDto);
        verify(mentorshipRequestRepository, times(1)).create(anyLong(), anyLong(), anyString());
    }

    @Test
    public void testRequestMentorship_UserNotFound() throws Exception {
        doThrow(new Exception("Пользователь не найден")).when(mentorshipRequestValidator).validateRequestMentorship(any(MentorshipRequestDto.class));
        assertThrows(Exception.class, () -> mentorshipRequestService.requestMentorship(requestDto));

        verify(mentorshipRequestRepository, never()).save(any(MentorshipRequest.class));
    }

    @Test
    public void testRequestMentorship_SelfRequest() throws Exception {
        doThrow(new Exception("Нельзя назначить себя ментором!")).when(mentorshipRequestValidator).validateRequestMentorship(any(MentorshipRequestDto.class));
        assertThrows(Exception.class, () -> mentorshipRequestService.requestMentorship(requestDto));

        verify(mentorshipRequestRepository, never()).save(any(MentorshipRequest.class));
    }

    @Test
    public void testRequestMentorship_ExistingRequest() {
        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()));
        assertThrows(Exception.class, () -> mentorshipRequestService.requestMentorship(requestDto));

        verify(mentorshipRequestRepository, never()).save(any(MentorshipRequest.class));
    }


    @Test
    public void testAcceptRequest_Success() {
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(1L);

        assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    public void testAcceptRequest_RequestNotFound() {
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> mentorshipRequestService.acceptRequest(1L), "Запрос не найден");

        verify(mentorshipRequestRepository, times(0)).save(any());
    }

    @Test
    public void testAcceptRequest_AlreadyAccepted() {
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        doThrow(new RuntimeException("Запрос не найден")).when(mentorshipRequestValidator).validateAcceptRequest(any(Optional.class));

        assertThrows(RuntimeException.class,
                () -> mentorshipRequestService.acceptRequest(1L), "Запрос не найден");

        verify(mentorshipRequestRepository, times(0)).save(any());
    }

    @Test
    public void testRejectRequest_Success() {
        RejectionDto rejectionDto = new RejectionDto("Not a good fit");

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.rejectRequest(1L, rejectionDto);

        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);

        assert (mentorshipRequest.getStatus().equals(RequestStatus.REJECTED));
        assert (mentorshipRequest.getRejectionReason().equals("Not a good fit"));
    }

    @Test
    public void testRejectRequest_RequestNotFound() {
        RejectionDto rejectionDto = new RejectionDto("Not a good fit");

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> mentorshipRequestService.rejectRequest(1L, rejectionDto), "Запрос не найден");

        verify(mentorshipRequestRepository, times(0)).save(any());
    }

    @Test
    public void testRejectRequest_AlreadyRejected() {
        RejectionDto rejectionDto = new RejectionDto("Not a good fit");

        mentorshipRequest.setStatus(RequestStatus.REJECTED);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        doThrow(new RuntimeException("Запрос уже отклонен")).when(mentorshipRequestValidator).validateRejectRequest(any(Optional.class));

        assertThrows(RuntimeException.class,
                () -> mentorshipRequestService.rejectRequest(1L, rejectionDto), "Запрос уже отклонен");
        verify(mentorshipRequestRepository, times(0)).save(any());
    }

    @Test
    public void testGetRequest_Success() {
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        when(mentorshipRequestRepository.findAll()).thenReturn(Collections.singletonList(mentorshipRequest));
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(requestDto);

        RequestFilterDto filterDto = new RequestFilterDto();
        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filterDto);
        assertEquals(1, result.size());
        assertEquals(requestDto, result.get(0));
    }
}