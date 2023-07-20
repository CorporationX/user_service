package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillGuaranteeMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "guarantor.id", target = "guarantorId")
    UserSkillGuaranteeDto toDto(UserSkillGuarantee userSkillGuarantee);

    UserSkillGuarantee toEntity(UserSkillGuaranteeDto userSkillGuaranteeDto);

    @Named("listSkillGuaranteeDto")
    default List<UserSkillGuaranteeDto> listGuaranteeDto(List<UserSkillGuarantee> userSkillGuarantees) {
        List<UserSkillGuaranteeDto> userSkillGuaranteeDtos = new ArrayList<>();
        userSkillGuarantees.stream()
                .map(this::toDto)
                .forEach(userSkillGuaranteeDtos::add);
        return userSkillGuaranteeDtos;
    }
}
