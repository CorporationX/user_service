package school.faang.user_service.UserMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "user.country", target = "country"),
            @Mapping(source = "user.ownedEvents", target = "ownedEvents"),
            @Mapping(source = "user.mentors", target = "mentors", ignore = true),
            @Mapping(source = "user.mentees", target = "mentees", ignore = true),
            @Mapping(source = "user.receivedMentorshipRequests", target = "receivedMentorshipRequests"),
            @Mapping(source = "user.sentMentorshipRequests", target = "sentMentorshipRequests")
    })
    UserDto toUserDto(User user);
}
