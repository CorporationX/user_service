package school.faang.user_service.mappers;

import org.mapstruct.*;

import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.UserSkillGuarantee;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillGuaranteeMapper {


    UserSkillGuarantee toEntity(UserSkillGuaranteeDto dto);


}