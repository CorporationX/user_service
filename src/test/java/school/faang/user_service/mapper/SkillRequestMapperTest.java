package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.SkillRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.mapper.skill.SkillRequestMapperImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class SkillRequestMapperTest {
    private SkillRequestMapperImpl mapper = new SkillRequestMapperImpl(new SkillMapperImpl());
    private static final long RECOMMENDATION_REQUEST_ID = 1L;
    private static final long SKILL_REQUEST_ID_ONE = 1L;
    private static final long SKILL_ID_ONE = 1L;
    private SkillRequest skillRequest;
    private SkillRequestDto skillRequestDto;

    @BeforeEach
    void setUp() {
        skillRequest = SkillRequest.builder()
                .id(SKILL_ID_ONE)
                .request(RecommendationRequest.builder()
                        .id(RECOMMENDATION_REQUEST_ID)
                        .build())
                .skill(Skill.builder()
                        .id(SKILL_ID_ONE)
                        .build())
                .build();


        skillRequestDto = SkillRequestDto.builder()
                .id(SKILL_REQUEST_ID_ONE)
                .recommendationRequestId(RECOMMENDATION_REQUEST_ID)
                .skill(SkillDto.builder()
                        .id(SKILL_ID_ONE)
                        .build())
                .build();
    }

    @Nested
    class ToEntity {
        @Test
        @DisplayName("If SkillRequest is null then pass it further")
        public void validateNullFromDtoIsPassedByMapperTest() {
            assertNull(mapper.toEntity(null));
        }

        @Test
        @DisplayName("On enter SkillRequestDto, on exit SkillRequest")
        public void validateWhenDtoNotNullThenReturnEntityTest() {
            SkillRequest skillRequestResult = mapper.toEntity(skillRequestDto);

            assertEquals(skillRequest.getId(), skillRequestResult.getId());
            assertEquals(skillRequest.getRequest().getId(), skillRequestResult.getRequest().getId());
            assertEquals(skillRequest.getSkill(), skillRequestResult.getSkill());
        }
    }

    @Nested
    class ToDto {
        @Test
        @DisplayName("If SkillRequestDto is null then pass it further")
        public void validateNullFromEntityPassedByMapperTest() {
            assertNull(mapper.toDto(null));
        }

        @Test
        @DisplayName("On enter SkillRequest, on exit SkillRequestDto")
        public void validateWhenEntityNotNullThenReturnDtoTest() {
            SkillRequestDto skillRequestDtoResult = mapper.toDto(skillRequest);

            assertEquals(skillRequestDto.getId(), skillRequestDtoResult.getId());
            assertEquals(skillRequestDto.getRecommendationRequestId(), skillRequestDtoResult.getRecommendationRequestId());
            assertEquals(skillRequestDto.getSkill(), skillRequestDtoResult.getSkill());
        }
    }
}
