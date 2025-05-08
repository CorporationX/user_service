package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.NotFoundRequestException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendationrequest.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    private static final int RECOMMENDATION_REQUEST_INTERVAL_MONTHS = 6;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Spy
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    public void testCanRequestRecommendation_NoPreviousRequest() {
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        when(recommendationRequestRepository.findLatestPendingRequest(
                requester.getId(), receiver.getId())
        ).thenReturn(Optional.empty());
        boolean result = recommendationRequestService.canRequestRecommendation(requester, receiver);

        assertTrue(result);
    }

    @Test
    public void testCanRequestRecommendation_EnoughTimePassed() {
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        LocalDateTime pastTime = LocalDateTime.now()
                .minusMonths(RECOMMENDATION_REQUEST_INTERVAL_MONTHS).minusMonths(1);
        RecommendationRequest request = new RecommendationRequest();
        request.setCreatedAt(pastTime);

        when(recommendationRequestRepository.findLatestPendingRequest(
                requester.getId(), receiver.getId())
        ).thenReturn(Optional.of(request));
        boolean result = recommendationRequestService.canRequestRecommendation(requester, receiver);

        assertTrue(result);
    }

    @Test
    public void testCanRequestRecommendation_TooSoon() {
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        LocalDateTime pastTime = LocalDateTime.now()
                .minusMonths(RECOMMENDATION_REQUEST_INTERVAL_MONTHS).plusMonths(1);
        RecommendationRequest request = new RecommendationRequest();
        request.setCreatedAt(pastTime);

        when(recommendationRequestRepository.findLatestPendingRequest(
                requester.getId(), receiver.getId())
        ).thenReturn(Optional.of(request));
        boolean result = recommendationRequestService.canRequestRecommendation(requester, receiver);

        assertFalse(result);
    }

    @Test
    public void testAllSkillsExist_AllExist() {
        SkillRequestDto skill1 = new SkillRequestDto();
        skill1.setId(1L);
        SkillRequestDto skill2 = new SkillRequestDto();
        skill2.setId(2L);
        List<SkillRequestDto> skills = List.of(skill1, skill2);

        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);
        boolean result = recommendationRequestService.allSkillsExist(skills);

        assertTrue(result);
    }

    @Test
    public void testAllSkillsExist_OneDoesNotExist() {
        SkillRequestDto skill1 = new SkillRequestDto();
        skill1.setId(1L);
        SkillRequestDto skill2 = new SkillRequestDto();
        skill2.setId(2L);
        List<SkillRequestDto> skills = List.of(skill1, skill2);

        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(false);
        boolean result = recommendationRequestService.allSkillsExist(skills);

        assertFalse(result);
    }

    @Test
    void create_EmptyMessageThrowsException() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setMessage("   ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(dto));
        Assertions.assertEquals("Сообщение не может быть пустым", exception.getMessage());
    }


    @Test
    void create_ReturnsNullIfUsersNotFound() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setMessage("Пожалуйста, напишите рекомендацию");
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        RecommendationRequestDto result = recommendationRequestService.create(dto);
        assertNull(result);
        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    void testGetRequestsReturnsMappedDtoList() {
        RequestFilterDto filterDto = new RequestFilterDto();

        RecommendationRequest request1 = new RecommendationRequest();
        RecommendationRequest request2 = new RecommendationRequest();
        List<RecommendationRequest> requestsList = Arrays.asList(request1, request2);
        Iterable<RecommendationRequest> iterable = requestsList;

        when(recommendationRequestRepository.findAll()).thenReturn((List<RecommendationRequest>) iterable);

        RecommendationRequestService spyService = spy(recommendationRequestService);
        doReturn(true).when(spyService)
                .filterByCondition(any(RecommendationRequest.class), eq(filterDto));

        RecommendationRequestDto dto1 = new RecommendationRequestDto();
        RecommendationRequestDto dto2 = new RecommendationRequestDto();
        List<RecommendationRequestDto> mappedList = Arrays.asList(dto1, dto2);
        when(recommendationRequestMapper.toRecommendationRequestDtoList(requestsList)).thenReturn(mappedList);

        List<RecommendationRequestDto> result = spyService.getRequests(filterDto);
        assertEquals(mappedList, result);
    }

    @Test
    void testGetRequestFound() {
        long requestId = 1L;
        RecommendationRequest request = new RecommendationRequest();
        RecommendationRequestDto dto = new RecommendationRequestDto();

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(recommendationRequestMapper.toRecommendationRequestDto(request)).thenReturn(dto);

        RecommendationRequestDto result = recommendationRequestService.getRequest(requestId);
        assertEquals(dto, result);
    }

    @Test
    void testGetRequestNotFound() {
        long requestId = 1L;
        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundRequestException exception = assertThrows(
                NotFoundRequestException.class,
                () -> recommendationRequestService.getRequest(requestId)
        );
        assertEquals(
                String.format("Запрос не найден c id: ", requestId),
                exception.getMessage()
        );
    }

    @Test
    void testRejectRequestNullReasonThrowsNullPointer() {
        long requestId = 1L;
        RejectionDto rejection = new RejectionDto();
        rejection.setReason(null);

        assertThrows(NullPointerException.class,
                () -> recommendationRequestService.rejectRequest(requestId, rejection));
    }

    @Test
    void testRejectRequestEmptyReasonThrowsIllegalArgument() {
        long requestId = 1L;
        RejectionDto rejection = new RejectionDto();
        rejection.setReason("   ");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(requestId, rejection));
        assertEquals("Сообщение не может быть пустым", exception.getMessage());
    }

    @Test
    void testRejectRequestNotFoundThrowsNotFoundException() {
        long requestId = 1L;
        RejectionDto rejection = new RejectionDto();
        rejection.setReason("Некорректные данные");

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundRequestException exception = assertThrows(
                NotFoundRequestException.class,
                () -> recommendationRequestService.rejectRequest(requestId, rejection)
        );
        assertEquals(String.format("Запрос не найден c id: ", requestId), exception.getMessage());
    }

    @Test
    void testRejectRequestSuccessForPendingRequest() {
        long requestId = 1L;
        RejectionDto rejection = new RejectionDto();
        rejection.setReason("Неверные данные");

        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.PENDING);

        RecommendationRequestDto expectedDto = new RecommendationRequestDto();

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(recommendationRequestMapper.toRecommendationRequestDto(request)).thenReturn(expectedDto);

        RecommendationRequestDto result = recommendationRequestService.rejectRequest(requestId, rejection);

        assertEquals(RequestStatus.REJECTED, request.getStatus());
        assertEquals("Неверные данные", request.getRejectionReason());

        verify(recommendationRequestRepository).save(request);
        assertEquals(expectedDto, result);
    }
}