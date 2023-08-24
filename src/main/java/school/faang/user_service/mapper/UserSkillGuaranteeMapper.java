package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserSkillGuaranteeDto;
import school.faang.user_service.entity.UserSkillGuarantee;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillGuaranteeMapper {

    UserSkillGuarantee toEntity(UserSkillGuaranteeDto userSkillGuaranteeDto);

    UserSkillGuaranteeDto toDto(UserSkillGuarantee userSkillGuarantee);
}
