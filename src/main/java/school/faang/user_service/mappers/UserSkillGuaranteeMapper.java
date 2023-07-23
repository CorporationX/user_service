package school.faang.user_service.mappers;

import org.mapstruct.*;

import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillGuaranteeMapper {
    @Mapping(target = "user", source = "userId", qualifiedByName = "toUser")
    @Mapping(target = "skill", source = "skillId", qualifiedByName = "toSkill")
    @Mapping(target = "guarantor", source = "guarantorId", qualifiedByName = "toGuarantor")
    UserSkillGuarantee toEntity(UserSkillGuaranteeDto dto);

    @Named("toUser")
    default User toUser(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("toSkill")
    default Skill toSkill(Long skillId) {
        Skill skill = new Skill();
        skill.setId(skillId);
        return skill;
    }

    @Named("toGuarantor")
    default User toGuarantor(Long guarantorId) {
        User guarantor = new User();
        guarantor.setId(guarantorId);
        return guarantor;
    }
}