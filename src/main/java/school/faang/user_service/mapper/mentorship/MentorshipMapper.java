package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MentorshipMapper {

    default List<Long> toDtoList(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(User::getId)
                .toList();
    }
}
