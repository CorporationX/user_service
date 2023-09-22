package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.entity.UserProfilePic;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProfilePicMapper {
    UserProfilePicDto toDto(UserProfilePic userProfilePic);
    UserProfilePic toEntity(UserProfilePicDto userProfilePicDto);
}
