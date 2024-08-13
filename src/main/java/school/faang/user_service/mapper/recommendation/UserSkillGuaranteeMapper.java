package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillGuaranteeMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "guarantor.id", target = "guarantorId")
    UserSkillGuaranteeDto toDto(UserSkillGuarantee userSkillGuarantee);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "skillId", target = "skill.id")
    @Mapping(source = "guarantorId", target = "guarantor.id")
    UserSkillGuarantee toEntity(UserSkillGuaranteeDto userSkillGuaranteeDto);

    List<UserSkillGuarantee> toEntityList(List<UserSkillGuaranteeDto> userSkillGuaranteeDtoList);

    List<UserSkillGuaranteeDto> toDtoList(List<UserSkillGuarantee> userSkillGuaranteeDtoList);
}
