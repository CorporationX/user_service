package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.filter.mentorshipRequestFilter.MentorshipRequestFilter;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.model.filter_dto.MentorshipRequestFilterDto;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.model.enums.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorshipRequestFilter.MentorshipRequestDescriptionFilter;
import school.faang.user_service.filter.mentorshipRequestFilter.MentorshipRequestReceiverFilter;
import school.faang.user_service.filter.mentorshipRequestFilter.MentorshipRequestRequesterFilter;
import school.faang.user_service.filter.mentorshipRequestFilter.MentorshipRequestStatusFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipOfferedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.MentorshipRequestRepository;
import school.faang.user_service.service.impl.MentorshipRequestServiceImpl;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {
    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);
    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilterList;
    @Mock
    private MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;

    private MentorshipRequestDto mentorshipRequestDto;
    private MentorshipRequest mentorshipRequest;
    private MentorshipRequest savedMentorshipRequest;
    private User requester;
    private User receiver;
    private List<MentorshipRequest> requests;
    private MentorshipRequestFilterDto filters;

    @BeforeEach
    public void setUp() {

        requester = new User();
        requester.setId(1L);

        receiver = new User();
        receiver.setId(2L);

        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(requester.getId());
        mentorshipRequestDto.setReceiverId(receiver.getId());
        mentorshipRequestDto.setDescription("Need mentorship on Java.");

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setDescription("Need mentorship on Java.");

        requests = List.of(mentorshipRequest);

        savedMentorshipRequest = new MentorshipRequest();
        savedMentorshipRequest.setId(100L);
        savedMentorshipRequest.setRequester(requester);
        savedMentorshipRequest.setReceiver(receiver);
        savedMentorshipRequest.setStatus(RequestStatus.PENDING);
        savedMentorshipRequest.setDescription("Need mentorship on Java.");
    }

    @Test
    public void requestMentorshipTest_Success() {
        when(mentorshipRequestMapper.toEntity(mentorshipRequestDto)).thenReturn(mentorshipRequest);
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);
        when(mentorshipRequestRepository.save(mentorshipRequest)).thenReturn(savedMentorshipRequest);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        assertNotNull(result);

        assertEquals(mentorshipRequestDto.getRequesterId(), result.getRequesterId());
        assertEquals(mentorshipRequestDto.getReceiverId(), result.getReceiverId());
        assertEquals(mentorshipRequestDto.getDescription(), result.getDescription());
        assertEquals(RequestStatus.PENDING, mentorshipRequest.getStatus());

        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
        verify(mentorshipRequestMapper, times(1)).toDto(mentorshipRequest);
    }

    @Test
    public void requestMentorshipTest_ValidationFail() {
        doThrow(new DataValidationException("Ошибка валидации")).when(mentorshipRequestValidator).descriptionValidation(mentorshipRequestDto);
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        });
        assertEquals("Ошибка валидации", exception.getMessage());

        verify(mentorshipRequestValidator, times(1)).descriptionValidation(mentorshipRequestDto);
        verify(mentorshipRequestValidator, never()).requesterReceiverValidation(any());
        verify(mentorshipRequestValidator, never()).selfRequestValidation(any());
        verify(mentorshipRequestValidator, never()).lastRequestDateValidation(any());
    }

    @Test
    public void getRequestsTest_NoFilters() {
        when(mentorshipRequestFilterList.isEmpty()).thenReturn(true);
        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(new MentorshipRequestFilterDto());
        assertTrue(result.isEmpty());
        verify(mentorshipRequestFilterList, times(1)).isEmpty();
    }

    @Test
    public void getRequestsTest_ValidRequest() {
        List<MentorshipRequestFilter> mentorshipRequestFilterList = List.of(new MentorshipRequestDescriptionFilter(),
                new MentorshipRequestRequesterFilter(), new MentorshipRequestReceiverFilter(), new MentorshipRequestStatusFilter());
        mentorshipRequestService = new MentorshipRequestServiceImpl(mentorshipRequestValidator, mentorshipRequestRepository,
                userRepository, mentorshipRequestMapper, mentorshipRequestFilterList, mentorshipOfferedEventPublisher);
        filters = MentorshipRequestFilterDto.builder().descriptionPattern("Need mentorship on Java.").build();
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        when(mentorshipRequestMapper.toDto(requests.get(0))).thenReturn(mentorshipRequestDto);
        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filters);
        assertEquals(1, result.size(), "Должен вернуть один запрос на менторство");
    }

    @Test
    public void getRequests_NoRequests() {
        mentorshipRequestFilterList.clear();
        when(mentorshipRequestRepository.findAll()).thenReturn(new ArrayList<>());
        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(new MentorshipRequestFilterDto());
        assertTrue(result.isEmpty(), "Список должен быть пустым, если запросов нет");
    }


    @Test
    public void acceptRequestTest_Success() {
        requester.setMentors(new ArrayList<>());
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        mentorshipRequestService.acceptRequest(1L);
        assertEquals(1, requester.getMentors().size());
        assertTrue(requester.getMentors().contains(receiver));
    }

    @Test
    public void acceptRequestTest_UserAlreadyMentor() {
        requester.setMentors(List.of(receiver));
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.acceptRequest(1L);
        });

        assertEquals("Пользователь id2 уже является ментором отправителя id1", exception.getMessage());
    }

    @Test
    public void rejectRequestTest_Success() {
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        MentorshipRequestDto result = mentorshipRequestService.rejectRequest(1L, new RejectionDto(1L, "Not interested"));

        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals("Not interested", mentorshipRequest.getRejectionReason());
        verify(mentorshipRequestRepository, times(1)).findById(1L);
    }
}
