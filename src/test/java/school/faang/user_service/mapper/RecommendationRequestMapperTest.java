package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

public class RecommendationRequestMapperTest {

    RecommendationRequestMapper mapper = Mappers.getMapper(RecommendationRequestMapper.class);

    @Test
    void testDtoToEntity(){
        SkillDto skill1 = new SkillDto(1L,"Java");
        SkillDto skill2 = new SkillDto(2L,"Spring Boot");

        SkillRequestDto skillRequest1 = new SkillRequestDto(1L, new RecommendationRequest(), skill1);
        SkillRequestDto skillRequest2 = new SkillRequestDto(2L, new RecommendationRequest(), skill2);

        var skills = Arrays.asList(skillRequest1, skillRequest2);
        var recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .message("Please provide a recommendation.")
                .status(RequestStatus.PENDING)
                .skills(skills)
                .requesterId(1001L)
                .receiverId(1002L)
                .createdAt(LocalDateTime.of(2020, Month.JANUARY, 18, 0, 0))
                .updatedAt(LocalDateTime.of(2021, Month.JANUARY, 18, 0, 0))
                .build();

        var recommendationRequest = mapper.dtoToEntity(recommendationRequestDto);

        assertThat(recommendationRequest.getId()).isEqualTo(recommendationRequestDto.getId());
        assertThat(recommendationRequest.getMessage()).isEqualTo(recommendationRequestDto.getMessage());
        assertThat(recommendationRequest.getStatus()).isEqualTo(recommendationRequestDto.getStatus());

        assertThat(recommendationRequest.getSkills())
                .hasSize(recommendationRequestDto.getSkills().size())
                .allSatisfy(skillRequest -> {
                    SkillRequestDto skillRequestDto = recommendationRequestDto.getSkills().stream()
                            .filter(dto -> dto.getId() == skillRequest.getId())
                            .findFirst()
                            .orElse(null);
                    assertThat(skillRequestDto).isNotNull();
                    assertThat(skillRequest.getSkill().getId()).isEqualTo(skillRequestDto.getSkillDto().getId());
                    assertThat(skillRequest.getSkill().getTitle()).isEqualTo(skillRequestDto.getSkillDto().getTitle());
                });

        assertThat(recommendationRequest.getRequester().getId()).isEqualTo(recommendationRequestDto.getRequesterId());
        assertThat(recommendationRequest.getReceiver().getId()).isEqualTo(recommendationRequestDto.getReceiverId());

        assertThat(recommendationRequest.getCreatedAt()).isEqualTo(recommendationRequestDto.getCreatedAt());
        assertThat(recommendationRequest.getUpdatedAt()).isEqualTo(recommendationRequestDto.getUpdatedAt());
    }

    @Test
    void testEntityToDto(){
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Spring Boot");

        SkillRequest skillRequest1 = new SkillRequest(1L, new RecommendationRequest(), skill1);
        SkillRequest skillRequest2 = new SkillRequest(2L, new RecommendationRequest(), skill2);

        var skills = Arrays.asList(skillRequest1, skillRequest2);
        var requester = User.builder()
                .id(1001L)
                .build();
        var receiver = User.builder()
                .id(1002L)
                .build();

        var recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .message("Please provide a recommendation.")
                .status(RequestStatus.PENDING)
                .skills(skills)
                .requester(requester)
                .receiver(receiver)
                .createdAt(LocalDateTime.of(2020, Month.JANUARY, 18, 0, 0))
                .updatedAt(LocalDateTime.of(2021, Month.JANUARY, 18, 0, 0))
                .build();

        var recommendationRequestDto = mapper.entityToDto(recommendationRequest);

        assertThat(recommendationRequestDto.getId()).isEqualTo(recommendationRequest.getId());
        assertThat(recommendationRequestDto.getMessage()).isEqualTo(recommendationRequest.getMessage());
        assertThat(recommendationRequestDto.getStatus()).isEqualTo(recommendationRequest.getStatus());

        assertThat(recommendationRequestDto.getSkills())
                .hasSize(recommendationRequest.getSkills().size())
                .allSatisfy(skillRequestDto -> {
                    SkillRequest skillRequest = recommendationRequest.getSkills().stream()
                            .filter(req -> req.getId() == skillRequestDto.getId())
                            .findFirst()
                            .orElse(null);
                    assertThat(skillRequest).isNotNull();
                    assertThat(skillRequestDto.getSkillDto().getId()).isEqualTo(skillRequest.getSkill().getId());
                    assertThat(skillRequestDto.getSkillDto().getTitle()).isEqualTo(skillRequest.getSkill().getTitle());
                });

        assertThat(recommendationRequestDto.getRequesterId()).isEqualTo(recommendationRequest.getRequester().getId());
        assertThat(recommendationRequestDto.getReceiverId()).isEqualTo(recommendationRequest.getReceiver().getId());

        assertThat(recommendationRequestDto.getCreatedAt()).isEqualTo(recommendationRequest.getCreatedAt());
        assertThat(recommendationRequestDto.getUpdatedAt()).isEqualTo(recommendationRequest.getUpdatedAt());
    }
}
