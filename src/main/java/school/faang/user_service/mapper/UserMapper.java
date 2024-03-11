package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.base.UserMapperBase;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends UserMapperBase {
    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    List<UserDto> listToDto(List<User> users);

    @Mapping(target = "userProfilePic", expression = "java(getUserProfilePic(userDto))")
    @Mapping(target = "country", expression = "java(getCountry(userDto.getCountry()))")
    User toEntity(UserRegistrationDto userDto);

    @Mapping(target = "country", source = "country.title")
    @Mapping(target = "profilePicFileId", source = "userProfilePic.fileId")
    @Mapping(target = "profilePicSmallFileId", source = "userProfilePic.smallFileId")
    UserRegistrationDto toRegDto(User user);

    default UserProfilePic getUserProfilePic(UserRegistrationDto userDto) {
        String fileId = userDto.getProfilePicFileId();
        String smallFileId = userDto.getProfilePicSmallFileId();

        if (fileId != null && smallFileId != null) {
            return UserProfilePic.builder()
                    .fileId(fileId)
                    .smallFileId(smallFileId)
                    .build();
        }
        return null;
    }
}