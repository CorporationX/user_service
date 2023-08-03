package school.faang.user_service.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    SkillRequestRepository skillRequestRepository;
    @Spy
    RecommendationRequestMapperImpl mapper;
    @InjectMocks
    RecommendationRequestService recommendationRequestService;


    @ParameterizedTest
    @MethodSource("getId")
    @DisplayName("Requester and receiver are the same")
    void requesterAndReceiverAreTheSame(long id) {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(id);
        dto.setReceiverId(id);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(dto));
        assertEquals("Requester and receiver are the same", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("getIds")
    @DisplayName("Receiver or requester not found")
    void receiverOrRequesterNotFound(long receiverId, long requesterId) {
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.empty());
        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(new User()));
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setReceiverId(receiverId);
        dto.setRequesterId(requesterId);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(dto));
        assertEquals("Requester or receiver not found", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("getIntFrom1To5")
    @DisplayName("Request is pending")
    void requestIsPending(int month) {
        LocalDateTime now = LocalDateTime.now();

        RecommendationRequest lastRequest = new RecommendationRequest();
        lastRequest.setUpdatedAt(now.minusMonths(month));

        RecommendationRequestDto curRequest = new RecommendationRequestDto();
        curRequest.setRequesterId(1L);
        curRequest.setReceiverId(2L);
        curRequest.setCreatedAt(now);

        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong())).
                thenReturn(Optional.of(lastRequest));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        DateTimeException exception = assertThrows(DateTimeException.class,
                () -> recommendationRequestService.create(curRequest));
        assertEquals("Request is pending", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("getId")
    @DisplayName("Get recommendation request by id")
    void getRecommendationRequestById(long id) {
        RecommendationRequest request = new RecommendationRequest();
        request.setId(id);
        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(request));
        RecommendationRequestDto requestDto = recommendationRequestService.getRequest(id);
        assertEquals(id, requestDto.getId());
    }

    @ParameterizedTest
    @MethodSource("getId")
    @DisplayName("Skill not found")
    void skillNotFound(long skillId) {
        SkillRequestDto skillDto = new SkillRequestDto();
        skillDto.setSkillId(skillId);

        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setReceiverId(1L);
        requestDto.setRequesterId(2L);
        requestDto.setSkillRequests(List.of(skillDto));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        when(skillRequestRepository.existsById(skillId))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(requestDto));
        assertEquals("Skill not found", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("getIds")
    @DisplayName("Verify skill request repository creating request")
    void verifySkillRequestRepositoryCreatingRequest(long skillId, long skillRequestId) {
        SkillRequestDto skillDto = new SkillRequestDto();
        skillDto.setId(skillRequestId);
        skillDto.setSkillId(skillId);

        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setSkillRequests(List.of(skillDto));

        when(skillRequestRepository.existsById(skillId))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        recommendationRequestService.create(requestDto);

        verify(skillRequestRepository)
                .create(skillRequestId, skillId);
    }

    @ParameterizedTest
    @MethodSource("getRequestData")
    @DisplayName("Verify recommendation request repository creating request")
    void verifyRecommendationRequestRepositoryCreatingRequest(long requesterId, long receiverId, String message) {
        User receiver = new User();
        User requester = new User();
        receiver.setId(receiverId);
        requester.setId(requesterId);
        RecommendationRequest request = new RecommendationRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setMessage(message);

        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setSkillRequests(new ArrayList<>());
        requestDto.setRequesterId(requesterId);
        requestDto.setReceiverId(receiverId);
        requestDto.setMessage(message);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        when(recommendationRequestRepository.create(requesterId, receiverId, message))
                .thenReturn(request);

        RecommendationRequestDto created = recommendationRequestService.create(requestDto);

        assertAll(() -> {
            assertEquals(requesterId, created.getRequesterId());
            assertEquals(receiverId, created.getReceiverId());
            assertEquals(message, created.getMessage());
        });
    }

    @ParameterizedTest
    @MethodSource("getId")
    @DisplayName("Recommendation request not found")
    void getRecommendationRequestByIdNotFound(long id) {
        when(recommendationRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.getRequest(id));
        assertEquals("Request not found", exception.getMessage());
    }

    private static Stream<Arguments> getRequestData() {
        return Stream.of(
                Arguments.of(29L, 10L, "Java"),
                Arguments.of(100L, 28746L, "Knife"),
                Arguments.of(321312L, 1L, "Kotlin"),
                Arguments.of(38L, 32L, "CI/CD")
        );
    }

    private static Stream<Arguments> getId() {
        return Stream.of(
                Arguments.of(1L),
                Arguments.of(3L),
                Arguments.of(5L),
                Arguments.of(100L),
                Arguments.of(5213L)
        );
    }

    private static Stream<Arguments> getIntFrom1To5() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(1),
                Arguments.of(2),
                Arguments.of(3),
                Arguments.of(4),
                Arguments.of(5)
        );
    }

    private static Stream<Arguments> getIds() {
        return Stream.of(
                Arguments.of(1L, 2L),
                Arguments.of(2L, 1L),
                Arguments.of(29L, 30L),
                Arguments.of(1L, 100L),
                Arguments.of(1321312, 432432),
                Arguments.of(100L, 1L)
        );
    }
}