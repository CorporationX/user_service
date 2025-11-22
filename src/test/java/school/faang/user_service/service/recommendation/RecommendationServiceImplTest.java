package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = Mappers
            .getMapper(RecommendationRequestMapper.class);

    @Spy
    private UserMapper userMapper = Mappers
            .getMapper(UserMapper.class);

    @Mock
    private UserContext userContext;

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    private static final Long TEST_REQUESTER_ID = 1L;
    private static final Long TEST_RECEIVER_ID = 5L;
    private static final Long TEST_RECOMMENDATION_REQUEST_ID = 0L;
    private static final Long RECOMMENDATION_MONTHS_LIMIT = 6L;
    private static final String TEST_MESSAGE_TEXT = "Test text";
    private static final LocalDateTime TEST_CREATED_AT = LocalDateTime.now()
            .minusMonths(RECOMMENDATION_MONTHS_LIMIT + 1);
    private static final LocalDateTime INVALID_CREATED_AT = LocalDateTime.now();
    private final User testRequester = new User();
    private final User testReceiver = new User();

    RecommendationRequest recommendationRequest1 = new RecommendationRequest();
    RecommendationRequest recommendationRequest2 = new RecommendationRequest();
    RecommendationRequest recommendationRequest3 = new RecommendationRequest();

    CreateRecommendationRequestDto createRecommendationRequestDto
            = new CreateRecommendationRequestDto(TEST_MESSAGE_TEXT, TEST_RECEIVER_ID);
    RejectionDto rejectionDto = new RejectionDto("Rejection text");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                recommendationRequestService, "maxMonths", 6
        );
        testRequester.setId(TEST_REQUESTER_ID);
        testReceiver.setId(TEST_RECEIVER_ID);

        recommendationRequest1.setRequester(testRequester);
        recommendationRequest1.setReceiver(testReceiver);
        recommendationRequest1.setMessage(TEST_MESSAGE_TEXT);
        recommendationRequest1.setCreatedAt(TEST_CREATED_AT);
        recommendationRequest1.setStatus(RequestStatus.PENDING);
    }

    @Test
    void testCreateSuccess() {
        UserDto requesterDto = userMapper.toUserDto(testRequester);
        UserDto receiverDto = userMapper.toUserDto(testReceiver);

        Mockito.when(userContext.getUserId()).thenReturn(TEST_REQUESTER_ID);
        Mockito.when(recommendationRequestRepository
                        .findLatestPendingRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID))
                .thenReturn(Optional.of(recommendationRequest1));
        Mockito.when(userRepository.getByIdOrThrow(TEST_REQUESTER_ID)).thenReturn(testRequester);
        Mockito.when(userRepository.getByIdOrThrow(TEST_RECEIVER_ID)).thenReturn(testReceiver);
        Mockito.when(recommendationRequestRepository.save(Mockito.any(RecommendationRequest.class)))
                .thenReturn(recommendationRequest1);

        RecommendationRequestDto expectedDto = new RecommendationRequestDto(TEST_RECOMMENDATION_REQUEST_ID,
                TEST_MESSAGE_TEXT, requesterDto, receiverDto, RequestStatus.PENDING);
        RecommendationRequestDto resultDto = recommendationRequestService.create(createRecommendationRequestDto);
        assertEquals(expectedDto, resultDto);
    }

    @Test
    void testCreateInvalidReceiverId() {
        CreateRecommendationRequestDto invalidReceiverIdDto
                = new CreateRecommendationRequestDto(TEST_MESSAGE_TEXT, TEST_REQUESTER_ID);
        Mockito.when(userContext.getUserId()).thenReturn(TEST_REQUESTER_ID);
        assertThrows(ForbiddenException.class, () -> recommendationRequestService.create(invalidReceiverIdDto),
                "Requesting a recommendation from self is not allowed!");
    }

    @Test
    void testCreateRecentRecommendationRequestAlreadyExists() {
        recommendationRequest1.setCreatedAt(INVALID_CREATED_AT);
        Mockito.when(userContext.getUserId()).thenReturn(TEST_REQUESTER_ID);
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID))
                .thenReturn(Optional.of(recommendationRequest1));
        assertThrows(ForbiddenException.class, () -> recommendationRequestService
                        .create(createRecommendationRequestDto),
                "Last recommendation request for this user "
                        + "was created later than 6 months ago");
    }

    @Test
    void testGetByFilters() {
        recommendationRequest2.setRequester(testRequester);
        recommendationRequest2.setReceiver(testReceiver);
        recommendationRequest2.setMessage(TEST_MESSAGE_TEXT);
        recommendationRequest2.setStatus(RequestStatus.PENDING);
        recommendationRequest3.setRequester(testRequester);
        recommendationRequest3.setReceiver(testReceiver);
        recommendationRequest3.setMessage(TEST_MESSAGE_TEXT);
        recommendationRequest3.setStatus(RequestStatus.PENDING);
        List<RecommendationRequest> testRecommendationRequestList = List.of(
                recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RecommendationRequestFilterDto testFilterDto = new RecommendationRequestFilterDto(
                TEST_REQUESTER_ID, TEST_RECEIVER_ID,
                "Test text", RequestStatus.PENDING);

        Mockito.when(recommendationRequestRepository.getByFilters(testFilterDto))
                .thenReturn(testRecommendationRequestList);
        List<RecommendationRequestDto> expectedList = testRecommendationRequestList
                .stream().map(r -> recommendationRequestMapper
                        .toRecommendationRequestDto(r)).toList();


        List<RecommendationRequestDto> resultList = recommendationRequestService.getByFilters(testFilterDto);
        assertEquals(expectedList, resultList);
    }

    @Test
    void testGetById() {
        UserDto requesterDto = userMapper.toUserDto(testRequester);
        UserDto receiverDto = userMapper.toUserDto(testReceiver);

        RecommendationRequestDto expectedDto = new RecommendationRequestDto(TEST_RECOMMENDATION_REQUEST_ID,
                TEST_MESSAGE_TEXT, requesterDto, receiverDto, RequestStatus.PENDING);
        Mockito.when(recommendationRequestRepository.getByIdOrThrow(TEST_RECOMMENDATION_REQUEST_ID))
                .thenReturn(recommendationRequest1);
        RecommendationRequestDto resultDto = recommendationRequestService.getById(TEST_RECOMMENDATION_REQUEST_ID);
        assertEquals(expectedDto, resultDto);
    }

    @Test
    void testAcceptSuccess() {

        Mockito.when(userContext.getUserId()).thenReturn(TEST_RECEIVER_ID);
        Mockito.when(recommendationRequestRepository.findById(TEST_RECOMMENDATION_REQUEST_ID))
                .thenReturn(Optional.of(recommendationRequest1));
        recommendationRequestService.accept(TEST_RECOMMENDATION_REQUEST_ID);
        Mockito.verify(recommendationRequestRepository).save(recommendationRequest1);
    }

    @Test
    void testAcceptInvalidReceiverId() {
        Mockito.when(userContext.getUserId()).thenReturn(TEST_REQUESTER_ID);
        Mockito.when(recommendationRequestRepository.findById(TEST_RECOMMENDATION_REQUEST_ID))
                .thenReturn(Optional.of(recommendationRequest1));
        assertThrows(ForbiddenException.class, () -> recommendationRequestService
                        .accept(TEST_RECOMMENDATION_REQUEST_ID),
                "ID mismatch,"
                        + " accepting/declining this recommendation request is not allowed for this user!");
    }

    @Test
    void testAcceptInvalidRequestStatus() {
        recommendationRequest1.setStatus(RequestStatus.ACCEPTED);
        Mockito.when(userContext.getUserId()).thenReturn(TEST_RECEIVER_ID);
        Mockito.when(recommendationRequestRepository.findById(TEST_RECOMMENDATION_REQUEST_ID))
                .thenReturn(Optional.of(recommendationRequest1));
        assertThrows(ForbiddenException.class, () -> recommendationRequestService
                        .accept(TEST_RECOMMENDATION_REQUEST_ID),
                "Only PENDING recommendation requests can be accepted/declined.");
    }

    @Test
    void testRejectSuccess() {

        Mockito.when(userContext.getUserId()).thenReturn(TEST_RECEIVER_ID);
        Mockito.when(recommendationRequestRepository.findById(TEST_RECOMMENDATION_REQUEST_ID))
                .thenReturn(Optional.of(recommendationRequest1));
        recommendationRequestService.reject(TEST_RECOMMENDATION_REQUEST_ID, rejectionDto);
        Mockito.verify(recommendationRequestRepository).save(recommendationRequest1);
    }

    @Test
    void testRejectInvalidReceiverId() {
        Mockito.when(userContext.getUserId()).thenReturn(TEST_REQUESTER_ID);
        Mockito.when(recommendationRequestRepository.findById(TEST_RECOMMENDATION_REQUEST_ID))
                .thenReturn(Optional.of(recommendationRequest1));
        assertThrows(ForbiddenException.class, () -> recommendationRequestService
                        .reject(TEST_RECOMMENDATION_REQUEST_ID, rejectionDto),
                "ID mismatch,"
                        + " accepting/declining this recommendation request is not allowed for this user!");
    }

    @Test
    void testRejectInvalidRequestStatus() {
        recommendationRequest1.setStatus(RequestStatus.ACCEPTED);
        Mockito.when(userContext.getUserId()).thenReturn(TEST_RECEIVER_ID);
        Mockito.when(recommendationRequestRepository.findById(TEST_RECOMMENDATION_REQUEST_ID))
                .thenReturn(Optional.of(recommendationRequest1));
        assertThrows(ForbiddenException.class, () -> recommendationRequestService
                        .reject(TEST_RECOMMENDATION_REQUEST_ID, rejectionDto),
                "Only PENDING recommendation requests can be accepted/declined.");
    }
}