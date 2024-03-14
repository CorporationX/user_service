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

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
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

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private RecommendationRequestDto mapToDto(RecommendationRequest recommendationRequest) {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(recommendationRequest.getId());
        dto.setRequesterId(recommendationRequest.getRequester().getId());
        dto.setReceiverId(recommendationRequest.getReceiver().getId());
        dto.setMessage(recommendationRequest.getMessage());
        dto.setStatus(recommendationRequest.getStatus().toString());

        LocalDateTime createdAt = recommendationRequest.getCreatedAt();
        Date createdAtDate = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        dto.setCreatedAt(createdAtDate);

        LocalDateTime updatedAt = recommendationRequest.getUpdatedAt();
        Date updatedAtDate = Date.from(updatedAt.atZone(ZoneId.systemDefault()).toInstant());
        dto.setUpdatedAt(updatedAtDate);

        return dto;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(@PathVariable long id) {
        RecommendationRequest recommendationRequest = recommendationRequestService.getRequest(id);
        if (recommendationRequest == null) {
            return ResponseEntity.notFound().build();
        }

        RecommendationRequestDto dto = mapToDto(recommendationRequest);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<RecommendationRequestDto> rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        RecommendationRequest rejectedRequest = recommendationRequestService.rejectRequest(id, rejection);
        if (rejectedRequest == null) {
            return ResponseEntity.notFound().build();
        }

        RecommendationRequestDto dto = mapToDto(rejectedRequest);
        return ResponseEntity.ok(dto);
    }
}