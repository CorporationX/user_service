package school.faang.user_service.mapper.skill;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = SkillMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SkillRequestMapper {
    @Mapping(source = "request.id", target = "recommendationRequestId")
    SkillRequestDto toDto(SkillRequest skillRequest);

    @Mapping(source = "recommendationRequestId", target = "request.id")
    SkillRequest toEntity(SkillRequestDto skillRequestDto);
}