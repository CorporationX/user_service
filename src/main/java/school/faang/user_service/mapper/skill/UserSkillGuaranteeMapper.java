package school.faang.user_service.mapper.skill;

import org.mapstruct.*;

import school.faang.user_service.dto.skill.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillGuaranteeMapper {
    @Mapping(source = "userId", target = "user", qualifiedByName = "toUser")
    @Mapping(source = "skillId", target = "skill", qualifiedByName = "toSkill")
    @Mapping(source = "guarantorId", target = "guarantor", qualifiedByName = "toGuarantor")
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