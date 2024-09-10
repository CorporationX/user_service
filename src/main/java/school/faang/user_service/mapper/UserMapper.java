package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "premium.id", target = "premiumId")
    @Mapping(source = "password", target = "password", ignore = true)
    @Mapping(source = "country.id", target = "countryId")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    User toEntity(UserDto userDto);
}