package school.faang.user_service.mapper.recomendation;

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
    private final RecommendationRequest recommendationRequestEntity = createEntity(10L, 2L, 5L, RequestStatus.PENDING);
    private final RecommendationRequestMapper recommendationRequestMapper = new RecommendationRequestMapperImpl();


    @Test
    public void testMapToEntity() {
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
    public void testMapToDto() {
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

    @Test
    public void testMapRequestsListToDtoList() {
        List<RecommendationRequest> recommendationRequests = List.of(createEntity(2L, 55L, 60L, RequestStatus.ACCEPTED),
                createEntity(3L, 75L, 68L, RequestStatus.PENDING),
                createEntity(33L, 16L, 71L, RequestStatus.REJECTED));
        List<RecommendationRequestDto> recommendationRequestDtos = recommendationRequestMapper.mapToDto(recommendationRequests);
        assertAll(
                () -> assertEquals(recommendationRequestDtos.get(0).getId(), recommendationRequests.get(0).getId()),
                () -> assertEquals(recommendationRequestDtos.get(1).getId(), recommendationRequests.get(1).getId()),
                () -> assertEquals(recommendationRequestDtos.get(2).getId(), recommendationRequests.get(2).getId()),
                () -> assertEquals(recommendationRequestDtos.get(0).getRequesterId(), recommendationRequests.get(0).getRequester().getId()),
                () -> assertEquals(recommendationRequestDtos.get(1).getRequesterId(), recommendationRequests.get(1).getRequester().getId()),
                () -> assertEquals(recommendationRequestDtos.get(2).getRequesterId(), recommendationRequests.get(2).getRequester().getId()),
                () -> assertEquals(recommendationRequestDtos.get(0).getReceiverId(), recommendationRequests.get(0).getReceiver().getId()),
                () -> assertEquals(recommendationRequestDtos.get(1).getReceiverId(), recommendationRequests.get(1).getReceiver().getId()),
                () -> assertEquals(recommendationRequestDtos.get(2).getReceiverId(), recommendationRequests.get(2).getReceiver().getId()),
                () -> assertEquals(recommendationRequestDtos.get(0).getStatus(), recommendationRequests.get(0).getStatus()),
                () -> assertEquals(recommendationRequestDtos.get(1).getStatus(), recommendationRequests.get(1).getStatus()),
                () -> assertEquals(recommendationRequestDtos.get(2).getStatus(), recommendationRequests.get(2).getStatus())

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

    private RecommendationRequest createEntity(long id, long requesterId, long receiverId, RequestStatus status) {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(id);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setRequester(new User());
        recommendationRequest.getRequester().setId(requesterId);
        recommendationRequest.setReceiver(new User());
        recommendationRequest.getReceiver().setId(receiverId);
        recommendationRequest.setMessage("testMessage");
        recommendationRequest.setStatus(status);
        recommendationRequest.setSkills(List.of(new SkillRequest(), new SkillRequest()));
        return recommendationRequest;
    }
}
