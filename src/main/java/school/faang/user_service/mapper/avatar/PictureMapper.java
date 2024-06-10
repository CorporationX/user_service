package school.faang.user_service.mapper.avatar;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.avatar.UserProfilePicDto;
import school.faang.user_service.entity.UserProfilePic;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PictureMapper {

    UserProfilePicDto toDto(UserProfilePic userProfilePic);

    UserProfilePic toEntity(UserProfilePicDto userProfilePicDto);
}
