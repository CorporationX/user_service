package school.faang.user_service.mapper.recomendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


class RecommendationRequestMapperTest {
    private final RecommendationRequestDto recommendationRequestDto = createDto();
    private final RecommendationRequest recommendationRequestEntity = createEntity();
    private final RecommendationRequestMapper recommendationRequestMapper = new RecommendationRequestMapperImpl();


    @Test
    @DisplayName("Test successful mapToEntity")
    public void testSuccessfulMapToEntity() {
        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.mapToEntity(recommendationRequestDto);
        assertAll(
                () -> assertEquals(recommendationRequestEntity.getCreatedAt(), recommendationRequestDto.getCreatedAt()),
                () -> assertEquals(recommendationRequestEntity.getRequester().getId(), recommendationRequestDto.getRequesterId()),
                () -> assertEquals(recommendationRequestEntity.getReceiver().getId(), recommendationRequestDto.getReceiverId()),
                () -> assertEquals(recommendationRequestEntity.getStatus(), recommendationRequestDto.getStatus()),
                () -> assertEquals(recommendationRequestEntity.getMessage(), recommendationRequestDto.getMessage()),
                () -> assertEquals(recommendationRequestEntity.getId(), recommendationRequestDto.getId())
        );
    }

    @Test
    @DisplayName("Test successful mapToDto")
    public void testSuccessfulMapToDto() {
        RecommendationRequestDto recommendationRequestDto = recommendationRequestMapper.mapToDto(recommendationRequestEntity);
        assertAll(
                () -> assertEquals(recommendationRequestDto.getMessage(), recommendationRequestEntity.getMessage()),
                () -> assertEquals(recommendationRequestDto.getRequesterId(), recommendationRequestEntity.getRequester().getId()),
                () -> assertEquals(recommendationRequestDto.getReceiverId(), recommendationRequestEntity.getReceiver().getId()),
                () -> assertEquals(recommendationRequestDto.getStatus(), recommendationRequestEntity.getStatus()),
                () -> assertEquals(recommendationRequestDto.getMessage(), recommendationRequestEntity.getMessage()),
                () -> assertEquals(recommendationRequestDto.getSkillsIds().get(0), recommendationRequestEntity.getSkills().get(0).getId())
        );
    }

    private RecommendationRequestDto createDto() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setId(1L);
        recommendationRequestDto.setRequesterId(2L);
        recommendationRequestDto.setReceiverId(5L);
        recommendationRequestDto.setCreatedAt(LocalDateTime.now());
        recommendationRequestDto.setMessage("testMessage");
        recommendationRequestDto.setStatus(RequestStatus.PENDING);
        return recommendationRequestDto;
    }

    private RecommendationRequest createEntity() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setRequester(new User());
        recommendationRequest.getRequester().setId(2L);
        recommendationRequest.setReceiver(new User());
        recommendationRequest.getReceiver().setId(5L);
        recommendationRequest.setMessage("testMessage");
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setSkills(List.of(new SkillRequest(), new SkillRequest()));
        return recommendationRequest;
    }
}
