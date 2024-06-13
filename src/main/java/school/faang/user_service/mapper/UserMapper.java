package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "preferredContact", source = "contactPreference.preference")
    UserDto toDto(User user);

    @Mapping(target = "contactPreference.preference", source = "preferredContact")
    User toEntity(UserDto userDto);
}
