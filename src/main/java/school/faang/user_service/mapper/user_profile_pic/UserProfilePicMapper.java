package school.faang.user_service.mapper.user_profile_pic;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.entity.UserProfilePic;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProfilePicMapper {
    UserProfilePicDto toDto(UserProfilePic userProfilePic);
}
