package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.util.TestDataFactory;

import static org.assertj.core.api.Assertions.*;

public class RecommendationRequestMapperTest {

    private final RecommendationRequestMapper mapper = Mappers.getMapper(RecommendationRequestMapper.class);

    @Test
    void testToEntity(){
        // given - precondition
        var recommendationRequestDto = TestDataFactory.createRecommendationRequestDto();

        // when - action
        var recommendationRequest = mapper.toEntity(recommendationRequestDto);

        // then - verify the output
        assertThat(recommendationRequest.getId()).isEqualTo(recommendationRequestDto.getId());
        assertThat(recommendationRequest.getMessage()).isEqualTo(recommendationRequestDto.getMessage());
        assertThat(recommendationRequest.getStatus().name()).isEqualTo(recommendationRequestDto.getStatus().toUpperCase());

        assertThat(recommendationRequest.getSkills()).hasSize(recommendationRequestDto.getSkillIds().size());
        assertThat(recommendationRequest.getSkills()).extracting(SkillRequest::getId)
                .containsExactlyInAnyOrderElementsOf(recommendationRequestDto.getSkillIds());
    }

    @Test
    void testToDto(){
        // given - precondition
        var recommendationRequest = TestDataFactory.createRecommendationRequest();

        // when - action
        var recommendationRequestDto = mapper.toDto(recommendationRequest);

        // then - verify the output
        assertThat(recommendationRequestDto.getId()).isEqualTo(recommendationRequest.getId());
        assertThat(recommendationRequestDto.getMessage()).isEqualTo(recommendationRequest.getMessage());
        assertThat(recommendationRequestDto.getStatus()).isEqualTo(recommendationRequest.getStatus().name());
        assertThat(recommendationRequestDto.getSkillIds())
                .hasSize(recommendationRequest.getSkills().size());
        assertThat(recommendationRequestDto.getRequesterId()).isEqualTo(recommendationRequest.getRequester().getId());
        assertThat(recommendationRequestDto.getReceiverId()).isEqualTo(recommendationRequest.getReceiver().getId());
        assertThat(recommendationRequestDto.getCreatedAt()).isEqualTo(recommendationRequest.getCreatedAt());
        assertThat(recommendationRequestDto.getUpdatedAt()).isEqualTo(recommendationRequest.getUpdatedAt());
    }
}
