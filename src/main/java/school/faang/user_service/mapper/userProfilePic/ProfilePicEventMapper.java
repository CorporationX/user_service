package school.faang.user_service.mapper.userProfilePic;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.profilepic.ProfilePicEvent;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfilePicEventMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.userProfilePic.fileId", target = "avatarUrl")
    ProfilePicEvent toProfilePicEvent(User user);
}
