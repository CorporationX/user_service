package school.faang.user_service.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.AdjustableRecommendationRequestAnswer;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.filter.recommendation.ReturnEmptyStreamRecommendationRequestAnswer;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    public static final int RECOMMENDATION_REQUEST_MIN_DISTANCE_MONTHS = 1;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserServiceImpl userService;
    @Mock
    private SkillRequestService skillRequestService;
    @Mock
    private SkillService skillService;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = Mappers.getMapper(
            RecommendationRequestMapper.class);
    @Spy
    private SkillRequestMapper skillRequestMapper = Mappers.getMapper(SkillRequestMapper.class);

    @Mock
    private RecommendationRequestFilter recommendationRequestFilter1;

    @Mock
    private RecommendationRequestFilter recommendationRequestFilter2;

    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp() {
        recommendationRequestService = new RecommendationRequestService(
                userService,
                recommendationRequestRepository,
                skillRequestService,
                skillService,
                recommendationRequestMapper,
                skillRequestMapper,
                List.of(recommendationRequestFilter1, recommendationRequestFilter2)
        );

        ReflectionTestUtils.setField(
                recommendationRequestService,
                "recommendationRequestMinDistanceMonths",
                RECOMMENDATION_REQUEST_MIN_DISTANCE_MONTHS);
    }

    @Test
    public void testCreate_RequestEarlierRequiredMonths_Throws() {
        var recommendationRequest = createRecommendationRequestDto();
        var latestRequest = RecommendationRequest.builder()
                .createdAt(LocalDateTime.now())
                .build();
        setLatestRequest(recommendationRequest, Optional.of(latestRequest));

        assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.create(recommendationRequest));
    }

    @Test
    public void testCreate_NullSkills_Throws() {
        var recommendationRequest = createRecommendationRequestDto();
        setLatestRequest(recommendationRequest, Optional.empty());

        assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.create(recommendationRequest));
    }

    @Test
    public void testCreate_EmptySkills_Throws() {
        var recommendationRequest = createRecommendationRequestDto();
        recommendationRequest.setSkills(List.of());
        setLatestRequest(recommendationRequest, Optional.empty());

        assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.create(recommendationRequest));
    }

    @Test
    public void testCreate_HasMissingSkills_Throws() {
        // Arrange
        var recommendationRequest = createRecommendationRequestDto();

        var skillId = 3L;
        var skillRequest = new SkillRequestDto(skillId);
        recommendationRequest.setSkills(List.of(skillRequest));

        setLatestRequest(recommendationRequest, Optional.empty());
        when(skillService.doesSkillExists(skillId)).thenReturn(false);

        // Act + Assert
        assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.create(recommendationRequest));
    }

    @Test
    public void testCreate_RequestElderRequiredMonths_CallDataSavingMethod() {
        // Arrange
        var recommendationRequest = createRecommendationRequestDto();
        var requester = prepareRequester(recommendationRequest);
        var receiver = prepareReceiver(recommendationRequest);

        var skillId = 3L;
        var skillRequest = new SkillRequestDto(skillId);
        var skillRequests = List.of(skillRequest);
        recommendationRequest.setSkills(skillRequests);

        var latestRequest = RecommendationRequest.builder()
                .createdAt(LocalDateTime.now().minusMonths(RECOMMENDATION_REQUEST_MIN_DISTANCE_MONTHS + 1))
                .build();
        setLatestRequest(recommendationRequest, Optional.of(latestRequest));
        when(skillService.doesSkillExists(skillId)).thenReturn(true);

        var expectedResult = RecommendationRequest.builder()
                .id(5L)
                .requester(requester)
                .receiver(receiver)
                .message(recommendationRequest.getMessage())
                .build();
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(expectedResult);

        // Act
        var result = recommendationRequestService.create(recommendationRequest);

        // Assert
        verify(skillRequestService, times(skillRequests.size()))
                .createSkillRequest(anyLong(), anyLong());
        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getMessage(), result.getMessage());
        checkUsersEqual(expectedResult.getRequester(), requester);
        checkUsersEqual(expectedResult.getReceiver(), receiver);
    }

    @Test
    public void testGetRequests_AllFiltersAreNotApplicable_ReturnsOriginalRequests() {
        // Arrange
        var filterDto = new RequestFilterDto();

        var recommendationRequests = List.of(
                RecommendationRequest.builder().message("Java").build(),
                RecommendationRequest.builder().message("Kotlin").build());
        when(recommendationRequestRepository.findAllWithSkills()).thenReturn(recommendationRequests);

        when(recommendationRequestFilter1.isApplicable(filterDto)).thenReturn(false);
        when(recommendationRequestFilter2.isApplicable(filterDto)).thenReturn(false);

        // Act
        var result = recommendationRequestService.getRequests(filterDto);

        // Assert
        var expectedResult = recommendationRequestMapper.toDtos(recommendationRequests);
        assertIterableEquals(
                expectedResult.stream().map(RecommendationRequestDto::getId).toList(),
                result.stream().map(RecommendationRequestDto::getId).toList());
        assertIterableEquals(
                expectedResult.stream().map(RecommendationRequestDto::getMessage).toList(),
                result.stream().map(RecommendationRequestDto::getMessage).toList());
    }

    @Test
    public void testGetRequests_AllRequestsAreNotMatched_ReturnsEmptyList() {
        // Arrange
        var filterDto = new RequestFilterDto();
        filterDto.setMessagePattern("Test");

        var recommendationRequests = List.of(
                RecommendationRequest.builder().message("Java").build(),
                RecommendationRequest.builder().message("Kotlin").build());
        when(recommendationRequestRepository.findAllWithSkills()).thenReturn(recommendationRequests);

        when(recommendationRequestFilter1.isApplicable(filterDto)).thenReturn(true);
        when(recommendationRequestFilter2.isApplicable(filterDto)).thenReturn(true);

        when(recommendationRequestFilter1.apply(any(), any())).thenAnswer(
                new ReturnEmptyStreamRecommendationRequestAnswer());
        when(recommendationRequestFilter2.apply(any(), any())).thenAnswer(
                new ReturnEmptyStreamRecommendationRequestAnswer());

        // Act
        var result = recommendationRequestService.getRequests(filterDto);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetRequests_SomeRequestsAreMatched_ReturnsNotEmptyList() {
        // Arrange
        var messagePatternToSearch = "Java";
        var statusToSearch = RequestStatus.PENDING;
        var filterDto = new RequestFilterDto();
        filterDto.setMessagePattern(messagePatternToSearch);
        filterDto.setStatus(statusToSearch);

        var recommendationRequests = List.of(
                RecommendationRequest.builder()
                        .id(1L)
                        .message("JavaScript")
                        .status(RequestStatus.PENDING)
                        .build(),
                RecommendationRequest.builder()
                        .id(2L)
                        .message("Java")
                        .status(RequestStatus.PENDING)
                        .build(),
                RecommendationRequest.builder()
                        .id(3L)
                        .message("Python")
                        .status(RequestStatus.PENDING)
                        .build());
        when(recommendationRequestRepository.findAllWithSkills()).thenReturn(recommendationRequests);

        when(recommendationRequestFilter1.isApplicable(filterDto)).thenReturn(true);
        when(recommendationRequestFilter2.isApplicable(filterDto)).thenReturn(true);

        when(recommendationRequestFilter1.apply(any(), any())).thenAnswer(
                new AdjustableRecommendationRequestAnswer(
                        request -> request.getMessage().contains(messagePatternToSearch)));
        when(recommendationRequestFilter2.apply(any(), any())).thenAnswer(
                new AdjustableRecommendationRequestAnswer(
                        request -> request.getStatus() == statusToSearch));

        List<RecommendationRequestDto> expectedResult = new ArrayList<>();
        expectedResult.add(recommendationRequestMapper.toDto(recommendationRequests.get(0)));
        expectedResult.add(recommendationRequestMapper.toDto(recommendationRequests.get(1)));

        // Act
        var result = recommendationRequestService.getRequests(filterDto);

        // Assert
        assertIterableEquals(
                expectedResult.stream().map(RecommendationRequestDto::getId).toList(),
                result.stream().map(RecommendationRequestDto::getId).toList());
        assertIterableEquals(
                expectedResult.stream().map(RecommendationRequestDto::getMessage).toList(),
                result.stream().map(RecommendationRequestDto::getMessage).toList());
        assertIterableEquals(
                expectedResult.stream().map(RecommendationRequestDto::getStatus).toList(),
                result.stream().map(RecommendationRequestDto::getStatus).toList());
    }

    @Test
    public void testGetRequest_RequestNotFound_Throws() {
        var requestId = 1L;
        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(
                DataRetrievalFailureException.class,
                () -> recommendationRequestService.getRequest(requestId));
    }

    @Test
    public void testGetRequest_RequestFound_ReturnsRecommendationRequest() {
        // Arrange
        var requestId = 1L;
        var recommendationRequest = RecommendationRequest.builder()
                .id(requestId)
                .message("Test message")
                .build();
        var skillRequest = new SkillRequest();
        var skill = Skill.builder()
                .id(3L)
                .build();
        skillRequest.setSkill(skill);
        var skillRequests = List.of(skillRequest);
        recommendationRequest.setSkills(skillRequests);

        when(recommendationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(recommendationRequest));

        when(skillRequestService.getSkillRequestsByRequestId(requestId)).thenReturn(skillRequests);

        // Act
        var result = recommendationRequestService.getRequest(requestId);

        // Assert
        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(recommendationRequest.getMessage(), result.getMessage());
        assertNotNull(result.getSkills());
        assertEquals(skillRequests.size(), result.getSkills().size());
        assertEquals(skillRequests.get(0).getSkill().getId(), result.getSkills().get(0).skillId());
        verify(skillRequestService, times(1))
                .getSkillRequestsByRequestId(requestId);
    }

    @Test
    public void testRejectRequest_RequestNotFound_Throws() {
        var rejection = createRejectionDto();
        var requestId = 1L;
        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(
                DataRetrievalFailureException.class,
                () -> recommendationRequestService.rejectRequest(requestId, rejection));
    }

    @ParameterizedTest
    @MethodSource("testRejectionProhibitedSource")
    public void testRejectRequest_InvalidStatus_ReturnsFalse(RequestStatus requestStatus) {
        var rejection = createRejectionDto();
        var requestId = 1L;
        prepareRecommendationRequestForRejectionTest(requestStatus, requestId);

        var result = recommendationRequestService.rejectRequest(requestId, rejection);

        assertFalse(result);
    }

    public static Stream<RequestStatus> testRejectionProhibitedSource() {
        return Stream.of(RequestStatus.REJECTED, RequestStatus.ACCEPTED);
    }

    @Test
    public void testRejectRequest_ValidStatus_ReturnsTrue() {
        var rejection = createRejectionDto();
        var requestId = 1L;
        var recommendationRequest = prepareRecommendationRequestForRejectionTest(RequestStatus.PENDING, requestId);

        var result = recommendationRequestService.rejectRequest(requestId, rejection);

        assertTrue(result);
        assertEquals(RequestStatus.REJECTED, recommendationRequest.getStatus());
        assertEquals(rejection.reason(), recommendationRequest.getRejectionReason());
        verify(recommendationRequestRepository, times(1))
                .save(recommendationRequest);
    }

    private static void checkUsersEqual(User expectedUser, User user) {
        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getUsername(), user.getUsername());
    }

    private void setLatestRequest(
            RecommendationRequestDto recommendationRequest,
            Optional<RecommendationRequest> latestRequest) {
        when(recommendationRequestRepository.findLatestRequest(
                recommendationRequest.getRequesterId(),
                recommendationRequest.getReceiverId()))
                .thenReturn(latestRequest);
    }

    private User prepareRequester(RecommendationRequestDto recommendationRequest) {
        var requester = User.builder()
                .id(recommendationRequest.getRequesterId())
                .username("Requester")
                .build();
        when(userService.getUserById(requester.getId())).thenReturn(requester);

        return requester;
    }

    private User prepareReceiver(RecommendationRequestDto recommendationRequest) {
        var receiver = User.builder()
                .id(recommendationRequest.getReceiverId())
                .username("Receiver")
                .build();
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        return receiver;
    }

    private RecommendationRequestDto createRecommendationRequestDto() {
        return new RecommendationRequestDto("Test message", 1L, 2L);
    }

    private static @NotNull RejectionDto createRejectionDto() {
        return new RejectionDto("Test reason");
    }

    private RecommendationRequest prepareRecommendationRequestForRejectionTest(
            RequestStatus requestStatus,
            long requestId) {
        var recommendationRequest = RecommendationRequest.builder()
                .status(requestStatus)
                .build();
        when(recommendationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(recommendationRequest));

        return recommendationRequest;
    }
}