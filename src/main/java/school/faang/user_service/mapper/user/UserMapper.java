package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "country.title", target = "country")
    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDto toDto(User user);

    @Mapping(target = "country", ignore = true)
    @Mapping(target = "contactPreference.preference", source = "preference")
    User toEntity(UserDto userDto);
}
