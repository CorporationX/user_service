package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.model.UserProfilePic;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProfilePicMapper {

    UserProfilePicDto userProfilePicToDto(UserProfilePic userProfilePic);
}
