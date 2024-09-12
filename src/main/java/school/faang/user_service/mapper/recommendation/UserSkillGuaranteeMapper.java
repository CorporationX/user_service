package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.UserSkillGuarantee;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillGuaranteeMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "skillId", source = "skill.id")
    @Mapping(target = "guarantorId", source = "guarantor.id")
    UserSkillGuaranteeDto toDto(UserSkillGuarantee userSkillGuarantee);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "skill.id", source = "skillId")
    @Mapping(target = "guarantor.id", source = "guarantorId")
    UserSkillGuarantee toEntity(UserSkillGuaranteeDto userSkillGuaranteeDto);
}
