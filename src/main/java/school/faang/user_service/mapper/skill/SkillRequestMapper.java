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
    @Mapping(target = "recommendationRequestId", ignore = true)
    SkillRequestDto toDto(SkillRequest skillRequest);

    @Mapping(target = "request.id", ignore = true)
    SkillRequest toEntity(SkillRequestDto skillRequestDto);
}