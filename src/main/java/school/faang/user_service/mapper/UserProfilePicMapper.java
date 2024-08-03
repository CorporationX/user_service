package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.entity.UserProfilePic;

@Mapper(componentModel = "spring")
public interface UserProfilePicMapper {
    UserProfilePicDto toDto(UserProfilePic userProfilePic);
    UserProfilePic toEntity(final UserProfilePicDto dto);
}
