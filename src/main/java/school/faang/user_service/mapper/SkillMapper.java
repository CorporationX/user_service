package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserSkillGuaranteeMapper.class)
public interface SkillMapper {
    @Mapping(source = "guarantees", target = "guaranteeDtoList", qualifiedByName = "listSkillGuaranteeDto")
    public SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skillDto);
}
