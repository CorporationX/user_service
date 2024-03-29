package school.faang.user_service.service;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.StringToLongMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final StringToLongMapper stringToLongMapper;

    @Transactional
    public void create(Long requesterId, Long receiverId, RecommendationRequestDto requestDto) {
        // Проверка на существование запроса
        boolean requestExists = recommendationRequestRepository.existsByRequesterIdAndReceiverId(requesterId, receiverId);

        // Если запрос уже существует, выбрасывается исключение
        if (requestExists) {
            throw new IllegalArgumentException("Запрос на рекомендацию уже отправлен и еще не закрыт");
        }

        // Создание запроса на рекомендацию
        recommendationRequestRepository.createRequest(requesterId, receiverId);

        // Преобразование строковых навыков в идентификаторы и сохранение в базе данных
        List<Long> skillIds = stringToLongMapper.stringToLong(requestDto.getSkills());
        for (Long skillId : skillIds) {
            skillRequestRepository.create(requesterId, skillId);
        }
    }

    // Метод для получения списка запросов на рекомендацию
    @Transactional
    public List<RecommendationRequest> getRequests(RequestFilterDto filter) {
        // Извлечение параметров фильтрации
        Long requesterId = filter.getRequesterId();
        Long receiverId = filter.getReceiverId();

        // Получение списка запросов на основе параметров фильтрации
        if (requesterId != null && receiverId != null) {
            return recommendationRequestRepository.findAllByRequesterIdAndReceiverId(requesterId, receiverId);
        } else if (requesterId != null) {
            return recommendationRequestRepository.findAllByRequesterId(requesterId);
        } else if (receiverId != null) {
            return recommendationRequestRepository.findAllByReceiverId(receiverId);
        } else {
            List<RecommendationRequest> allRequests = new ArrayList<>();
            recommendationRequestRepository.findAll().forEach(allRequests::add);
            return allRequests;
        }
    }

    // Метод для получения запроса по идентификатору
    @Transactional
    public RecommendationRequest getRequest(long id) {
        Optional<RecommendationRequest> recommendationRequestOptional = recommendationRequestRepository.findById(id);

        // Если запрос не найден, выбрасывается исключение
        return recommendationRequestOptional.orElseThrow(() -> new NotFoundException("Рекомендация с id " + id + " не найдена"));
    }

    // Метод для отклонения запроса на рекомендацию
    @Transactional
    public RecommendationRequest rejectRequest(long id, RejectionDto rejectionDto) {
        // Поиск запроса по идентификатору
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Рекомендация с id " + id + " не найдена"));

        // Проверка статуса запроса
        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Нельзя отклонить запрос, который уже принят или отклонен");
        }

        // Установка статуса отклонения и причины
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getReason());

        // Сохранение изменений в базе данных
        return recommendationRequestRepository.save(recommendationRequest);
    }
}
