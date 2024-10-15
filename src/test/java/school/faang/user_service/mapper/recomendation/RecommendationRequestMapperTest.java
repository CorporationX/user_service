package school.faang.user_service.mapper.recomendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.model.dto.RecommendationRequestDto;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.model.entity.RecommendationRequest;
import school.faang.user_service.model.entity.SkillRequest;
import school.faang.user_service.model.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationRequestMapperTest {

    private RecommendationRequestMapper recommendationRequestMapper;

    @BeforeEach
    void setUp() {
        recommendationRequestMapper = Mappers.getMapper(RecommendationRequestMapper.class);
    }

    @Test
    void testMapToDto_ShouldMapCorrectly() {
        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        SkillRequest skillRequest1 = new SkillRequest();
        skillRequest1.setId(10L);

        SkillRequest skillRequest2 = new SkillRequest();
        skillRequest2.setId(20L);

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        recommendationRequest.setSkills(List.of(skillRequest1, skillRequest2));

        RecommendationRequestDto recommendationRequestDto = recommendationRequestMapper.mapToDto(recommendationRequest);

        assertEquals(1L, recommendationRequestDto.getRequesterId());
        assertEquals(2L, recommendationRequestDto.getReceiverId());
        assertEquals(List.of(10L, 20L), recommendationRequestDto.getSkillsIds());
    }

    @Test
    void testMapToEntity_ShouldMapCorrectly() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setRequesterId(1L);
        recommendationRequestDto.setReceiverId(2L);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.mapToEntity(recommendationRequestDto);

        assertEquals(1L, recommendationRequest.getRequester().getId());
        assertEquals(2L, recommendationRequest.getReceiver().getId());
    }

    @Test
    void testMapToDtoList_ShouldMapCorrectly() {
        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        SkillRequest skillRequest1 = new SkillRequest();
        skillRequest1.setId(10L);

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        recommendationRequest.setSkills(List.of(skillRequest1));

        List<RecommendationRequestDto> result = recommendationRequestMapper.mapToDto(List.of(recommendationRequest));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRequesterId());
        assertEquals(2L, result.get(0).getReceiverId());
        assertEquals(List.of(10L), result.get(0).getSkillsIds());
    }

    @Test
    void testMapToRejectionDto_ShouldMapCorrectly() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setRejectionReason("Not enough details");

        RejectionDto rejectionDto = recommendationRequestMapper.mapToRejectionDto(recommendationRequest);

        assertNotNull(rejectionDto);
        assertEquals("Not enough details", rejectionDto.getRejectionReason());
    }

    @Test
    void testMapSkillsReqsToSkillsReqsIds_ShouldMapCorrectly() {
        SkillRequest skillRequest1 = new SkillRequest();
        skillRequest1.setId(10L);

        SkillRequest skillRequest2 = new SkillRequest();
        skillRequest2.setId(20L);

        List<SkillRequest> skillRequests = List.of(skillRequest1, skillRequest2);

        List<Long> skillIds = RecommendationRequestMapper.mapSkillsReqsToSkillsReqsIds(skillRequests);

        assertNotNull(skillIds);
        assertEquals(2, skillIds.size());
        assertEquals(List.of(10L, 20L), skillIds);
    }
}
