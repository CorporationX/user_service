package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.MentorshipRequestFilter.DescriptionFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private MentorshipRequestRepository requestRepository;
    @Mock
    private MentorshipRequestValidator requestValidator;
    @Mock
    private MentorshipRequestMapper requestMapper;
    @Mock
    private DescriptionFilter descriptionFilter;

    @InjectMocks
    private MentorshipRequestService requestService;

    private List<Filter<MentorshipRequest, RequestFilterDto>> filters;
    private User requester;
    private User receiver;
    private MentorshipRequest firstRequest;
    private MentorshipRequest secondRequest;
    private MentorshipRequestDto firstRequestDto;
    private RequestFilterDto filterDto;
    private Long firstRequestId;
    private RejectionDto rejectionDto;

    @BeforeEach
    void setUp() {
        descriptionFilter = mock(DescriptionFilter.class);
        filters = new ArrayList<>(List.of(descriptionFilter));
        requestService = new MentorshipRequestService(
                userService, requestRepository, requestValidator, requestMapper, filters);

        requester = User.builder().id(1L).build();
        receiver = User.builder().id(2L).build();

        firstRequestDto = MentorshipRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .description("Need help with java.")
                .status(RequestStatus.PENDING)
                .build();

        filterDto = RequestFilterDto.builder()
                .descriptionPattern("HELP")
                .build();

        firstRequest = MentorshipRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .description("Need help with java.")
                .status(RequestStatus.PENDING)
                .build();

        secondRequest = MentorshipRequest.builder()
                .id(2L)
                .requester(receiver)
                .receiver(requester)
                .status(RequestStatus.PENDING)
                .description("Description")
                .build();

        firstRequestId = firstRequest.getId();
        rejectionDto = RejectionDto.builder().reason("Reason").build();
    }

    @Test
    void testServiceRequestMentorshipShouldCreateRequest() {
        MentorshipRequestDto result = requestService.requestMentorship(firstRequestDto);

        verify(requestRepository).create(firstRequestDto.getRequesterId(),
                firstRequestDto.getReceiverId(), firstRequestDto.getDescription());
        assertEquals(result, firstRequestDto);
    }

    @Test
    void testGetRequestsShouldReturnEmptyList() {
        assertTrue(requestService.getRequests(filterDto).isEmpty());
    }

    @Test
    void testGetRequestsShouldReturnFilteredList() {
        when(requestRepository.findAll()).thenReturn(List.of(firstRequest, secondRequest));
        when(descriptionFilter.isApplicable(filterDto)).thenReturn(true);
        when(descriptionFilter.apply(any(), eq(filterDto))).thenAnswer(invocationOnMock -> Stream.of(firstRequest));
        when(requestMapper.toDto(firstRequest)).thenReturn(firstRequestDto);

        List<MentorshipRequestDto> result = requestService.getRequests(filterDto);

        verify(descriptionFilter, times(1)).isApplicable(any(RequestFilterDto.class));
        verify(descriptionFilter, times(1)).apply(any(), any(RequestFilterDto.class));
        verify(requestMapper, times(1)).toDto(any(MentorshipRequest.class));
        verify(requestRepository, times(1)).findAll();
        assertTrue(result.contains(firstRequestDto));
    }

    @Test
    void testAcceptRequestGotNullRequestById() {
        when(requestRepository.findById(firstRequestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.acceptRequest(firstRequestId));
        verify(requestValidator, times(1)).validateMentorshipRequestExists(firstRequestId);
        verify(requestRepository, times(1)).findById(firstRequestId);
    }

    @Test
    void testAcceptRequestSuccessful() {
        requester.setMentors(new ArrayList<>());
        receiver.setMentees(new ArrayList<>());
        firstRequestDto.setStatus(RequestStatus.ACCEPTED);
        when(requestRepository.findById(firstRequestId)).thenReturn(Optional.of(firstRequest));
        when(requestRepository.save(firstRequest)).thenReturn(firstRequest);
        when(requestMapper.toDto(firstRequest)).thenReturn(firstRequestDto);

        MentorshipRequestDto result = requestService.acceptRequest(firstRequestId);

        verify(requestRepository, times(1)).findById(firstRequestId);
        verify(requestRepository, times(1)).save(firstRequest);
        assertEquals(result.getId(), firstRequestId);
        assertEquals(result.getRequesterId(), firstRequest.getRequester().getId());
        assertEquals(result.getReceiverId(), firstRequest.getReceiver().getId());
        assertEquals(result.getStatus(), firstRequest.getStatus());
    }

    @Test
    void testRejectRequestGotNullRequestById() {
        when(requestRepository.findById(firstRequestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> requestService.rejectRequest(firstRequestId, rejectionDto));
        verify(requestRepository, times(0)).save(any(MentorshipRequest.class));
    }

    @Test
    void testRejectRequestSuccessful() {
        firstRequestDto.setStatus(RequestStatus.REJECTED);
        when(requestRepository.findById(firstRequestId)).thenReturn(Optional.of(firstRequest));
        when(requestRepository.save(firstRequest)).thenReturn(firstRequest);
        when(requestMapper.toDto(firstRequest)).thenReturn(firstRequestDto);

        MentorshipRequestDto result = requestService.rejectRequest(firstRequestId, rejectionDto);

        verify(requestRepository, times(1)).findById(firstRequestId);
        verify(requestRepository, times(1)).save(firstRequest);
        assertEquals(result.getStatus(), firstRequestDto.getStatus());
    }
}
