package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.ConfidentialUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "userProfilePic.fileId", target = "avatar")
    @Mapping(source = "userProfilePic.smallFileId", target = "avatarSmall")
    @Mapping(target = "preference", source = "contactPreference.preference")
    UserDto toDto(User user);

    @Mapping(target = "contactPreference.preference", source = "preference")
    User toEntity(UserDto userDto);

    @Mapping(source = "countryId", target = "country.id")
    User toEntity(ConfidentialUserDto confidentialUserDto);

    List<UserDto> toDto(List<User> users);

    List<User> toEntity(List<UserDto> users);
}
