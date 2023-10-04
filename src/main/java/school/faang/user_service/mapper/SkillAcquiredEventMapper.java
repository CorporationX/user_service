package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillAcquiredEvent;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserSkillGuaranteeMapper.class)
public interface SkillAcquiredEventMapper {
    @Mapping(source = "id", target = "skillId")
    @Mapping(source = "user.id", target = "receiverId")
    @Mapping(source = "guarantees", target = "guaranteeDtoList", qualifiedByName = "listSkillGuaranteeDto")
    SkillAcquiredEvent toEventDto(Skill skill);
}
