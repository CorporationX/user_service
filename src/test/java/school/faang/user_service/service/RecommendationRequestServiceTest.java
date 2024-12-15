package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.RecommendationRequestNotFoundException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.publisher.RecommendationRequestEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import school.faang.user_service.validator.SkillValidator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private UserService userService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillRequestService skillRequestService;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private RecommendationRequestEventPublisher recommendationRequestEventPublisher;

    @Mock
    private Filter<RecommendationRequest, RequestFilterDto> endDateFilter;
    @Mock
    private Filter<RecommendationRequest, RequestFilterDto> receiverIdFilter;
    @Mock
    private Filter<RecommendationRequest, RequestFilterDto> requesterIdFilter;
    @Mock
    private Filter<RecommendationRequest, RequestFilterDto> skillTitleFilter;
    @Mock
    private Filter<RecommendationRequest, RequestFilterDto> startDateFilter;
    @Mock
    private Filter<RecommendationRequest, RequestFilterDto> statusFilter;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    private RequestFilterDto filterDto;
    private RecommendationRequestValidator recommendationRequestValidator;

    private User requester;
    private User receiver;
    private Skill skill1;
    private Skill skill2;
    private Skill skill3;
    private RecommendationRequest recommendationRequest;
    private RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        RecommendationRequestValidator realValidator = new RecommendationRequestValidator(recommendationRequestRepository, skillValidator);
        recommendationRequestValidator = spy(realValidator);

        recommendationRequestService = new RecommendationRequestService(
                recommendationRequestRepository,
                recommendationRequestMapper,
                skillRequestService,
                userService,
                recommendationRequestValidator,
                List.of(endDateFilter,
                        receiverIdFilter,
                        requesterIdFilter,
                        skillTitleFilter,
                        startDateFilter,
                        statusFilter),
                recommendationRequestEventPublisher
        );

        requester = User.builder()
                .id(1L)
                .build();

        receiver = User.builder()
                .id(2L)
                .build();

        skill1 = Skill.builder()
                .id(1L)
                .title("Java")
                .build();

        skill2 = Skill.builder()
                .id(2L)
                .title("Kotlin")
                .build();

        skill3 = Skill.builder()
                .id(3L)
                .title("Hibernate")
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .message("Пожалуйста, дай мне рекомендацию")
                .build();

        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .skillIdentifiers(Arrays.asList(1L, 2L, 3L))
                .message("Пожалуйста, дай мне рекомендацию")
                .build();
    }

    @Test
    void testCreateRecommendationRequest_Success() {
        doNothing().when(skillValidator).validateSkills(recommendationRequestDto.getSkillIdentifiers());

        when(userService.getUserById(1L)).thenReturn(Optional.of(requester));
        when(userService.getUserById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.empty());
        doNothing().when(skillValidator).validateSkills(recommendationRequestDto.getSkillIdentifiers());

        when(recommendationRequestMapper.toEntity(any(RecommendationRequestDto.class)))
                .thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest savedRequest = invocation.getArgument(0);
                    savedRequest.setId(1L);
                    return savedRequest;
                });
        when(recommendationRequestMapper.toDto(any(RecommendationRequest.class)))
                .thenReturn(recommendationRequestDto);

        when(skillRepository.findAllById(recommendationRequestDto.getSkillIdentifiers()))
                .thenReturn(Arrays.asList(skill1, skill2, skill3));

        RecommendationRequestDto result = recommendationRequestService.create(recommendationRequestDto);

        assertNotNull(result);
        assertEquals(recommendationRequestDto.getMessage(), result.getMessage());
        assertEquals(RequestStatus.PENDING, result.getStatus());
        assertEquals(1L, result.getId());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).getUserById(2L);
        verify(recommendationRequestRepository, times(1))
                .findLatestPendingRequest(1L, 2L);
        verify(skillValidator, times(1)).validateSkills(recommendationRequestDto.getSkillIdentifiers());
        verify(recommendationRequestMapper, times(1))
                .toEntity(recommendationRequestDto);
        verify(recommendationRequestRepository, times(2))
                .save(recommendationRequest);
        verify(recommendationRequestMapper, times(1))
                .toDto(recommendationRequest);
    }

    @Test
    void testCreateRecommendationRequest_RequesterNotFound() {
        doThrow(new IllegalArgumentException("Пользователя, запрашивающего рекомендацию не существует"))
                .when(recommendationRequestValidator).validateUsersExistence(null, receiver);

        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                recommendationRequestService.create(recommendationRequestDto)
        );

        assertEquals("Requester with ID 1 not found", exception.getMessage());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, never()).getUserById(2L);

        verifyNoInteractions(recommendationRequestValidator);
        verifyNoInteractions(skillValidator);
        verifyNoInteractions(recommendationRequestMapper);
        verifyNoInteractions(recommendationRequestRepository);
    }

    @Test
    void testCreateRecommendationRequest_ReceiverNotFound() {
        doThrow(new IllegalArgumentException("Пользователя, получающего рекомендацию не существует"))
                .when(recommendationRequestValidator).validateUsersExistence(requester, null);

        when(userService.getUserById(1L)).thenReturn(Optional.of(requester));
        when(userService.getUserById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                recommendationRequestService.create(recommendationRequestDto)
        );

        assertEquals("Receiver with ID 1 not found", exception.getMessage());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).getUserById(2L);
        verify(recommendationRequestValidator, never()).validateRequestFrequency(anyLong(), anyLong());
        verify(skillValidator, never()).validateSkills(anyList());
        verify(recommendationRequestMapper, never()).toEntity(any());
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    void testCreateRecommendationRequest_RequestLimitExceeded() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(requester));
        when(userService.getUserById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(RecommendationRequest.builder()
                        .createdAt(LocalDateTime.now().minusMonths(3))
                        .build()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestDto)
        );

        assertEquals("Recommendation request must be sent once in 6 months", exception.getMessage());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).getUserById(2L);
        verify(recommendationRequestRepository, times(1))
                .findLatestPendingRequest(1L, 2L);
        verify(skillValidator, never()).validateSkills(anyList());
        verify(recommendationRequestMapper, never()).toEntity(any());
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
        verify(skillRequestService, never()).createSkillRequest(any(), any());
    }

    @Test
    void testCreateRecommendationRequest_SkillsNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(requester));
        when(userService.getUserById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.empty());
        doThrow(new IllegalArgumentException("Some skills are not present in database"))
                .when(skillValidator).validateSkills(recommendationRequestDto.getSkillIdentifiers());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestDto)
        );

        assertEquals("Some skills are not present in database", exception.getMessage());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).getUserById(2L);
        verify(recommendationRequestRepository, times(1))
                .findLatestPendingRequest(1L, 2L);
        verify(skillValidator, times(1)).validateSkills(recommendationRequestDto.getSkillIdentifiers());
        verify(recommendationRequestMapper, never()).toEntity(any());
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
        verify(skillRequestService, never()).createSkillRequest(any(), any());
    }

    @Test
    void testGetRequests_WithAllFilters() {
        RequestFilterDto filter = RequestFilterDto.builder()
                .status(RequestStatus.PENDING)
                .requesterId(1L)
                .receiverId(2L)
                .startDate(LocalDateTime.of(2024, 1, 1, 0, 0))
                .endDate(LocalDateTime.of(2024, 12, 31, 23, 59))
                .skillTitles(Arrays.asList("Java", "Spring"))
                .build();

        RecommendationRequest requestOne = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .createdAt(LocalDateTime.of(2024, 8, 21, 12, 0))
                .skills(List.of(
                        SkillRequest.builder().skill(Skill.builder().title("Java").build()).build(),
                        SkillRequest.builder().skill(Skill.builder().title("Spring").build()).build()
                ))
                .build();

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(requestOne));

        when(statusFilter.isApplicable(filter)).thenReturn(true);
        when(statusFilter.apply(any(), eq(filter))).thenReturn(Stream.of(requestOne));

        when(startDateFilter.isApplicable(filter)).thenReturn(true);
        when(startDateFilter.apply(any(), eq(filter))).thenReturn(Stream.of(requestOne));

        when(endDateFilter.isApplicable(filter)).thenReturn(true);
        when(endDateFilter.apply(any(), eq(filter))).thenReturn(Stream.of(requestOne));

        when(requesterIdFilter.isApplicable(filter)).thenReturn(true);
        when(requesterIdFilter.apply(any(), eq(filter))).thenReturn(Stream.of(requestOne));

        when(receiverIdFilter.isApplicable(filter)).thenReturn(true);
        when(receiverIdFilter.apply(any(), eq(filter))).thenReturn(Stream.of(requestOne));

        when(skillTitleFilter.isApplicable(filter)).thenReturn(true);
        when(skillTitleFilter.apply(any(), eq(filter))).thenReturn(Stream.of(requestOne));

        when(recommendationRequestMapper.toDto(requestOne)).thenReturn(
                RecommendationRequestDto.builder()
                        .id(requestOne.getId())
                        .message("Пожалуйста, дай мне рекомендацию")
                        .status(RequestStatus.PENDING)
                        .skillIdentifiers(List.of(1L, 2L))
                        .requesterId(1L)
                        .receiverId(2L)
                        .createdAt(requestOne.getCreatedAt())
                        .updatedAt(LocalDateTime.of(2024, 8, 21, 12, 30))
                        .rejectionReason(null)
                        .build()
        );

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filter);

        assertNotNull(result);
        assertEquals(requestOne.getId(), result.get(0).getId());

        verify(recommendationRequestRepository, times(1)).findAll();
        verify(statusFilter, times(1)).apply(any(), eq(filter));
        verify(startDateFilter, times(1)).apply(any(), eq(filter));
        verify(endDateFilter, times(1)).apply(any(), eq(filter));
        verify(requesterIdFilter, times(1)).apply(any(), eq(filter));
        verify(receiverIdFilter, times(1)).apply(any(), eq(filter));
        verify(skillTitleFilter, times(1)).apply(any(), eq(filter));
    }

    @Test
    void testGetRequests_NoFilters() {
        filterDto = RequestFilterDto.builder().build();

        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .build();

        List<RecommendationRequest> requestsList = List.of(recommendationRequest);

        when(statusFilter.isApplicable(filterDto)).thenReturn(false);
        when(startDateFilter.isApplicable(filterDto)).thenReturn(false);
        when(endDateFilter.isApplicable(filterDto)).thenReturn(false);
        when(requesterIdFilter.isApplicable(filterDto)).thenReturn(false);
        when(receiverIdFilter.isApplicable(filterDto)).thenReturn(false);
        when(skillTitleFilter.isApplicable(filterDto)).thenReturn(false);

        when(recommendationRequestRepository.findAll()).thenReturn(requestsList);
        when(recommendationRequestMapper.toDto(any(RecommendationRequest.class))).thenReturn(recommendationRequestDto);

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(recommendationRequestDto.getId(), result.get(0).getId());

        verify(statusFilter, never()).apply(any(), eq(filterDto));
        verify(startDateFilter, never()).apply(any(), eq(filterDto));
        verify(endDateFilter, never()).apply(any(), eq(filterDto));
        verify(requesterIdFilter, never()).apply(any(), eq(filterDto));
        verify(receiverIdFilter, never()).apply(any(), eq(filterDto));
        verify(skillTitleFilter, never()).apply(any(), eq(filterDto));
    }

    @Test
    void testGetRequests_NoResults() {
        RequestFilterDto filterDto = RequestFilterDto.builder().status(RequestStatus.REJECTED).build();

        when(recommendationRequestRepository.findAll()).thenReturn(List.of());

        when(statusFilter.isApplicable(filterDto)).thenReturn(true);
        when(statusFilter.apply(any(), eq(filterDto))).thenReturn(Stream.empty());

        when(startDateFilter.isApplicable(filterDto)).thenReturn(false);
        when(endDateFilter.isApplicable(filterDto)).thenReturn(false);
        when(requesterIdFilter.isApplicable(filterDto)).thenReturn(false);
        when(receiverIdFilter.isApplicable(filterDto)).thenReturn(false);
        when(skillTitleFilter.isApplicable(filterDto)).thenReturn(false);

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filterDto);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected no results, but found some");

        verify(recommendationRequestRepository, times(1)).findAll();
        verify(statusFilter, times(1)).isApplicable(filterDto);
        verify(statusFilter, times(1)).apply(any(), eq(filterDto));

        verify(startDateFilter, times(1)).isApplicable(filterDto);
        verify(endDateFilter, times(1)).isApplicable(filterDto);
        verify(requesterIdFilter, times(1)).isApplicable(filterDto);
        verify(receiverIdFilter, times(1)).isApplicable(filterDto);
        verify(skillTitleFilter, times(1)).isApplicable(filterDto);
    }

    @Test
    void getRequest_Success() {
        Long id = 1L;

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.getRequest(id);

        assertNotNull(result);
        assertEquals(recommendationRequestDto.getId(), result.getId());
        assertEquals(recommendationRequestDto.getRequesterId(), result.getRequesterId());
        assertEquals(recommendationRequestDto.getReceiverId(), result.getReceiverId());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, times(1)).toDto(recommendationRequest);
    }


    @Test
    void getRequest_NotFound() {
        Long id = 99L;

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        RecommendationRequestNotFoundException exception =
                assertThrows(RecommendationRequestNotFoundException.class, () ->
                        recommendationRequestService.getRequest(id)
                );

        assertEquals("Recommendation request with this Id was not found", exception.getMessage());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, never()).toDto(any());
    }


    @Test
    void rejectRequest_Success() {
        Long id = 1L;
        RejectionDto rejection = RejectionDto.builder()
                .reason("Просто не хочу. Ты мне неприятен.")
                .build();

        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setRejectionReason(null);

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);

        RecommendationRequestDto rejectedDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .status(RequestStatus.REJECTED)
                .createdAt(recommendationRequest.getCreatedAt())
                .skillIdentifiers(Arrays.asList(1L, 2L, 3L))
                .message("Please give me a recommendation")
                .rejectionReason("Просто не хочу. Ты мне неприятен.")
                .build();

        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(rejectedDto);

        RecommendationRequestDto result = recommendationRequestService.rejectRequest(id, rejection);

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertEquals("Просто не хочу. Ты мне неприятен.", result.getRejectionReason());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(recommendationRequestMapper, times(1)).toDto(recommendationRequest);
    }

    @Test
    void rejectRequest_AlreadyRejected() {
        Long id = 1L;
        RejectionDto rejection = RejectionDto.builder()
                .reason("Запрос уже отклонен")
                .build();

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason("Просто не хочу. Ты мне неприятен.");

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                recommendationRequestService.rejectRequest(id, rejection)
        );

        assertEquals("Impossible to reject recommendation request since it already has status REJECTED", exception.getMessage());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    void rejectRequest_NotFound() {
        Long id = 99L;
        RejectionDto rejection = RejectionDto.builder()
                .reason("Запрос на рекомендацию с таким Id не найден")
                .build();

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        RecommendationRequestNotFoundException exception =
                assertThrows(RecommendationRequestNotFoundException.class, () ->
                        recommendationRequestService.rejectRequest(id, rejection)
                );

        assertEquals("Recommendation request with this Id was not found", exception.getMessage());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }
}


