package school.faang.user_service.controller.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Controller
@Validated
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @Autowired
    public RecommendationRequestController(RecommendationRequestService recommendationRequestService){
        this.recommendationRequestService = recommendationRequestService;
    }

    // Метод для отправки запроса на рекомендацию
    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        // Проверка на пустое сообщение
        if (recommendationRequestDto.getMessage() == null || recommendationRequestDto.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }

        // Извлечение данных из DTO запроса
        Long requesterId = recommendationRequestDto.getRequesterId();
        Long receiverId = recommendationRequestDto.getReceiverId();
        List<String> skills = recommendationRequestDto.getSkills();

        // Создание нового DTO и вызов метода создания запроса в сервисе
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setSkills(skills);
        recommendationRequestService.create(requesterId, receiverId, dto);

        return recommendationRequestDto;
    }

    // Метод для получения списка запросов на рекомендацию
    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        // Получение списка запросов и их преобразование в DTO
        return recommendationRequestService.getRequests(filter)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Приватный вспомогательный метод для преобразования сущности запроса в DTO
    private RecommendationRequestDto mapToDto(RecommendationRequest recommendationRequest) {
        // Создание нового DTO и заполнение его данными из сущности запроса
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(recommendationRequest.getId());
        dto.setRequesterId(recommendationRequest.getRequester().getId());
        dto.setReceiverId(recommendationRequest.getReceiver().getId());
        dto.setMessage(recommendationRequest.getMessage());
        dto.setStatus(recommendationRequest.getStatus().toString());

        // Преобразование дат создания и обновления
        LocalDateTime createdAt = recommendationRequest.getCreatedAt();
        Date createdAtDate = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        dto.setCreatedAt(createdAtDate);

        LocalDateTime updatedAt = recommendationRequest.getUpdatedAt();
        Date updatedAtDate = Date.from(updatedAt.atZone(ZoneId.systemDefault()).toInstant());
        dto.setUpdatedAt(updatedAtDate);

        return dto;
    }

    // Метод для получения конкретного запроса на рекомендацию
    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(@PathVariable long id) {
        // Получение запроса по идентификатору и преобразование его в DTO
        RecommendationRequest recommendationRequest = recommendationRequestService.getRequest(id);
        if (recommendationRequest == null) {
            return ResponseEntity.notFound().build();
        }

        RecommendationRequestDto dto = mapToDto(recommendationRequest);
        return ResponseEntity.ok(dto);
    }

    // Метод для отклонения запроса на рекомендацию
    @PostMapping("/{id}/reject")
    public ResponseEntity<RecommendationRequestDto> rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        // Отклонение запроса по идентификатору и преобразование его в DTO
        RecommendationRequest rejectedRequest = recommendationRequestService.rejectRequest(id, rejection);
        if (rejectedRequest == null) {
            return ResponseEntity.notFound().build();
        }

        RecommendationRequestDto dto = mapToDto(rejectedRequest);
        return ResponseEntity.ok(dto);
    }
}
