package school.faang.user_service.mapper.userProfilePic;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.userProfile.UserProfileDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserProfilePicMapper {

    UserProfileDto toDto(User user);
}
