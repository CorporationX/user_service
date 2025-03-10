package school.faang.user_service.mapper.mentorship;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MentorMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    User toEntity(MentorDto mentorDto);

    @Mapping(target = "mentees", expression = "java(mapMenteesToIds(user.getMentees()))")
    MentorDto toDto(User user);

    default List<Long> mapMenteesToIds(List<User> mentees) {
        if (mentees == null) {
            return Collections.emptyList();
        }
        return mentees.stream()
                .map(User::getId)
                .toList();
    }
}
