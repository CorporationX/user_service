package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    List<UserDto> toDtoList(List<User> users);

    UserDto toDto(User user);

    User toEntity(UserRegistrationDto userRegistrationDto);

    default UserProfilePic mapUserProfilePic(String pictureKey, String smallPictureKey) {
        return UserProfilePic.builder()
                .fileId(pictureKey)
                .smallFileId(smallPictureKey)
                .build();
    }
}
