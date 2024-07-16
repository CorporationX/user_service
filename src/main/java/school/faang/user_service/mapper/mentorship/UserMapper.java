package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "mentees", target = "menteeIds", qualifiedByName = "MapMentees")
    @Mapping(source = "mentors", target = "mentorIds", qualifiedByName = "MapMentors")
    UserDto toDto(User user);

    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    User toEntity(UserDto userDto);

    @Named("MapMentees")
     default List<Long> menteesToMenteesIds(List<User> mentees){
         return mentees.stream().map(User::getId).toList();
     }

    @Named("MapMentors")
    default List<Long> mentorsToMentorsIds(List<User> mentors){
        return mentors.stream().map(User::getId).toList();
    }
}
