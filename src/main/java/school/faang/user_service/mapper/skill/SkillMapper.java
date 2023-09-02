package school.faang.user_service.mapper.skill;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.UserSkillGuaranteeDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring" , injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skillDTO);

    List<SkillDto> toListSkillsDto(List<Skill> skills);

    List<Skill> toListSkillsEntity(List<SkillDto> skillsDto);

    void updateSkill(SkillDto skillDto,
                   @MappingTarget Skill skill);

    default List<UserSkillGuaranteeDto> mapGuarantees(List<UserSkillGuarantee> guarantees) {
        return guarantees.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    UserSkillGuaranteeDto toDto(UserSkillGuarantee guarantee);

}