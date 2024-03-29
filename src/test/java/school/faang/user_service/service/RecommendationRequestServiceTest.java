package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private StringToLongMapper stringToLongMapper; // Инициализация маппера

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp() {
        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, skillRequestRepository, stringToLongMapper);
    }

    @Test
    void testGetRequests_All() {
        // Создание ожидаемого списка запросов
        List<RecommendationRequest> expectedRequests = Arrays.asList(
                new RecommendationRequest(),
                new RecommendationRequest()
        );
        // Мокирование метода findAll репозитория
        when(recommendationRequestRepository.findAll()).thenReturn(expectedRequests);

        // Вызов тестируемого метода
        List<RecommendationRequest> actualRequests = recommendationRequestService.getRequests(new RequestFilterDto());

        // Проверка результатов
        assertEquals(expectedRequests, actualRequests);
    }

    @Test
    void testCreate_WithValidData() {
        // Подготовка данных для теста
        Long requesterId = 1L;
        Long receiverId = 2L;
        List<String> skills = Arrays.asList("1", "2", "3");

        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setSkills(skills);

        // Мокирование метода stringToLong маппера
        when(stringToLongMapper.stringToLong(skills)).thenReturn(Arrays.asList(1L, 2L, 3L));

        // Вызов тестируемого метода
        recommendationRequestService.create(requesterId, receiverId, requestDto);

        // Проверка вызовов методов репозитория
        verify(recommendationRequestRepository).existsByRequesterIdAndReceiverId(requesterId, receiverId);
        verify(recommendationRequestRepository).createRequest(requesterId, receiverId);
        for (String skill : skills) {
            Long skillId = Long.valueOf(skill);
            verify(skillRequestRepository).create(requesterId, skillId);
        }
    }

    // Тест для случая существующего запроса
    @Test
    void testCreate_WithExistingRequest() {
        // Подготовка данных для теста
        Long requesterId = 1L;
        Long receiverId = 2L;
        List<String> skills = Arrays.asList("1", "2", "3");
        when(recommendationRequestRepository.existsByRequesterIdAndReceiverId(requesterId, receiverId)).thenReturn(true);

        RecommendationRequestDto requestDto = new RecommendationRequestDto();

        // Вызов тестируемого метода и проверка исключения
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(requesterId, receiverId, requestDto));
        assertEquals("Запрос на рекомендацию уже отправлен и еще не закрыт", exception.getMessage());
        verify(recommendationRequestRepository, never()).createRequest(anyLong(), anyLong());
        verify(skillRequestRepository, never()).create(anyLong(), anyLong());
    }

    @Test
    void testGetRequest_Found() {
        // Подготовка данных для теста
        Long requestId = 1L;
        RecommendationRequest expectedRequest = new RecommendationRequest();
        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(expectedRequest));

        // Вызов тестируемого метода
        RecommendationRequest actualRequest = recommendationRequestService.getRequest(requestId);

        // Проверка результатов
        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    void testRejectRequest_Valid() {
        // Подготовка данных для теста
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Причина отклонения");

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setStatus(RequestStatus.PENDING); // Устанавливаем статус PENDING

        // Мокирование вызовов методов в репозитории
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
