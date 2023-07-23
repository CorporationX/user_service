package school.faang.user_service.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillDto;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {


    SkillDto toDto(Skill entity);

    default List<UserSkillGuaranteeDto> mapGuarantees(List<UserSkillGuarantee> guarantees) {
        return guarantees.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    UserSkillGuaranteeDto toDto(UserSkillGuarantee guarantee);

}