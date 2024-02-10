package school.faang.user_service.mapper.user;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "country.id", target = "countryId")
    UserDto toUserDto(User user);

    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "userProfilePic.fileId", target = "userProfilePic")
    UserCreateDto toUserCreateDto(User user);


    @Mapping(source = "countryId", target = "country.id")
    @Mapping(source = "userProfilePic", target = "userProfilePic.fileId")
    User toEntity(UserCreateDto userCreateDto);

    List<UserDto> toUserDtoList(List<User> users);
}