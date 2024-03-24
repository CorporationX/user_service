package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.RecommendationRequestService;
import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;

    @Autowired
    public RecommendationRequestController(RecommendationRequestService recommendationRequestService, RecommendationRequestMapper recommendationRequestMapper){
        this.recommendationRequestService = recommendationRequestService;
        this.recommendationRequestMapper = recommendationRequestMapper;
    }

    // Метод для отправки запроса на рекомендацию
    @PostMapping("/request")
    public RecommendationRequestDto requestRecommendation(@Valid @RequestBody RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto.getMessage() == null || recommendationRequestDto.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }

        Long requesterId = recommendationRequestDto.getRequesterId();
        Long receiverId = recommendationRequestDto.getReceiverId();
        List<String> skills = recommendationRequestDto.getSkills();

        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setSkills(skills);
        recommendationRequestService.create(requesterId, receiverId, dto);

        return recommendationRequestDto;
    }

    // Метод для получения списка запросов на рекомендацию
    @GetMapping
    public List<RecommendationRequestDto> getRecommendationRequests(@Valid @RequestBody RequestFilterDto filter) {
        return recommendationRequestMapper.toDtoList(recommendationRequestService.getRequests(filter));
    }

    // Метод для получения конкретного запроса на рекомендацию
    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(@Valid @PathVariable long id) {
        RecommendationRequest recommendationRequest = recommendationRequestService.getRequest(id);
        RecommendationRequestDto dto = recommendationRequestMapper.toDto(recommendationRequest);
        return ResponseEntity.ok(dto);
    }

    // Метод для отклонения запроса на рекомендацию
    @PostMapping("/{id}/reject")
    public ResponseEntity<RecommendationRequestDto> rejectRequest(@Valid @PathVariable long id, @Valid @RequestBody RejectionDto rejection) {
        RecommendationRequest rejectedRequest = recommendationRequestService.rejectRequest(id, rejection);
        if (rejectedRequest == null) {
            return ResponseEntity.notFound().build();
        }
        RecommendationRequestDto dto = recommendationRequestMapper.toDto(rejectedRequest);
        return ResponseEntity.ok(dto);
    }
}
