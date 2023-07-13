package school.faang.user_service.controller;

import org.mapstruct.Mapper;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MenteeMapper {
    MenteeDto toUser (User user);
}
