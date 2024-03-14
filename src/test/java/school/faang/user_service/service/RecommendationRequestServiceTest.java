package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.amazonaws.services.kms.model.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.StringToLongMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private StringToLongMapper stringToLongMapper;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp() {
        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, skillRequestRepository, stringToLongMapper);
    }

    @Test
    void testGetRequests_All() {
        List<RecommendationRequest> expectedRequests = Arrays.asList(
                new RecommendationRequest(),
                new RecommendationRequest()
        );
        when(recommendationRequestRepository.findAll()).thenReturn(expectedRequests);

        List<RecommendationRequest> actualRequests = recommendationRequestService.getRequests(new RequestFilterDto());

        assertEquals(expectedRequests, actualRequests);
    }

    @Test
    void testCreate_WithValidData() {
        Long requesterId = 1L;
        Long receiverId = 2L;
        List<String> skills = Arrays.asList("1", "2", "3");

        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setSkills(skills);

        when(stringToLongMapper.stringToLong(skills)).thenReturn(Arrays.asList(1L, 2L, 3L));

        recommendationRequestService.create(requesterId, receiverId, requestDto);

        verify(recommendationRequestRepository).existsByRequesterIdAndReceiverId(requesterId, receiverId);
        verify(recommendationRequestRepository).createRequest(requesterId, receiverId);
        for (String skill : skills) {
            Long skillId = Long.valueOf(skill);
            verify(skillRequestRepository).create(requesterId, skillId);
        }
    }

    @Test
    void testCreate_WithExistingRequest() {
        Long requesterId = 1L;
        Long receiverId = 2L;
        List<String> skills = Arrays.asList("1", "2", "3");
        when(recommendationRequestRepository.existsByRequesterIdAndReceiverId(requesterId, receiverId)).thenReturn(true);

        RecommendationRequestDto requestDto = new RecommendationRequestDto();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(requesterId, receiverId, requestDto));
        assertEquals("Запрос на рекомендацию уже отправлен и еще не закрыт", exception.getMessage());
        verify(recommendationRequestRepository, never()).createRequest(anyLong(), anyLong());
        verify(skillRequestRepository, never()).create(anyLong(), anyLong());
    }

    @Test
    void testGetRequest_Found() {
        Long requestId = 1L;
        RecommendationRequest expectedRequest = new RecommendationRequest();
        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(expectedRequest));

        RecommendationRequest actualRequest = recommendationRequestService.getRequest(requestId);

        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    void testRejectRequest_Valid() {
        // Создаем заглушку для объекта RejectionDto
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Причина отклонения");

        // Создаем заглушку для объекта RecommendationRequest
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setStatus(RequestStatus.PENDING); // Устанавливаем статус PENDING

        // Мокируем вызовы методов в репозитории
        when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);

        // Вызываем метод rejectRequest
        recommendationRequestService.rejectRequest(1L, rejectionDto);

        // Проверяем, что методы репозитория были вызваны с правильными аргументами
        verify(recommendationRequestRepository).findById(1L);
        verify(recommendationRequestRepository).save(recommendationRequest);

        // Проверяем, что статус отклонения установлен
        assertEquals(RequestStatus.REJECTED, recommendationRequest.getStatus());

        // Проверяем, что причина отклонения установлена
        assertEquals("Причина отклонения", recommendationRequest.getRejectionReason());
    }
}
