package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.model.Skill;
import school.faang.user_service.model.User;
import school.faang.user_service.model.recommendation.RecommendationRequest;
import school.faang.user_service.model.recommendation.SkillRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecommendationRequestMapperTest {
    private RecommendationRequestMapper recommendationRequestMapper = new RecommendationRequestMapperImpl();
    private User user;
    private Skill skill;
    private RecommendationRequest request;
    private RecommendationRequestDto recommendationRequestDto;
    private SkillRequest skillRequest;
    private List<SkillRequest> skillRequestList;

    @BeforeEach
    public void setUp() {
        request = new RecommendationRequest();
        user = new User();
        skill = new Skill();
        skill.setId(1L);
        skillRequest = new SkillRequest();
        skillRequestList = new ArrayList<>();

        skillRequest.setId(1L);
        skillRequest.setSkill(skill);
        skillRequestList.add(skillRequest);
        user.setId(1L);
        request.setId(12L);
        request.setRequester(user);
        request.setSkills(skillRequestList);
        recommendationRequestDto = recommendationRequestDto.builder()
                .id(1L)
                .skillIds(List.of(1L, 2L))
                .requesterId(user.getId())
                .build();
    }

    @Test
    public void testToDto() {
        RecommendationRequestDto dto = recommendationRequestMapper.toDto(request);

        assertEquals(request.getId(), dto.id());
        assertEquals(user.getId(), dto.requesterId());
        List<Long> skillRequestIds = skillRequestList.stream()
                .map(SkillRequest::getId)
                .toList();
        List<Long> skillRequestDtoIds = dto.skillIds();
        assertEquals(skillRequestIds, skillRequestDtoIds);
    }

    @Test
    public void testToEntity() {
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);
        assertEquals(recommendationRequest.getId(), recommendationRequestDto.id());
        assertEquals(user.getId(), recommendationRequestDto.requesterId());
    }
}
