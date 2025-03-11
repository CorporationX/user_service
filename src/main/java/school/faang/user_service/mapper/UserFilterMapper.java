package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.filter.UserFilterByNamePhoneExperience;

@Mapper
public interface UserFilterMapper {
    UserFilterByNamePhoneExperience toEntity(UserFilterDto dto);

    UserFilterDto toDto(UserFilterByNamePhoneExperience entity);
}