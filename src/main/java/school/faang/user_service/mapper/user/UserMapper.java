package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "countryId", target = "country.id")
    User toEntity(UserRegistrationDto userRegistrationDto);

    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "userProfilePic.fileId", target = "userProfilePicId")
    UserDto toDto(User user);

    List<UserDto> toDtos(List<User> users);
}
