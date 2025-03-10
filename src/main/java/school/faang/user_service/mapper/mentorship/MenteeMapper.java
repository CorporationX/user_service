package school.faang.user_service.mapper.mentorship;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MenteeMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    User toEntity(MenteeDto menteeDto);

    @Mapping(target = "mentors", expression = "java(mapMentorsToIds(user.getMentors()))")
    MenteeDto toDto(User user);

    default List<Long> mapMentorsToIds(List<User> mentors) {
        if (mentors == null) {
            return Collections.emptyList();
        }
        return mentors.stream()
                .map(User::getId)
                .toList();
    }
}
