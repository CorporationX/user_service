package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorMapper {
    @Mapping(source = "mentees", target = "menteesIds", qualifiedByName = "convertUserToId")
    MentorDto toDTO(User mentor);

    User toEntity(MentorDto mentorDTO);

    @Named("convertUserToId")
    default List<Long> convertUserToId(List<User> users) {
        return users.stream().map(User::getId).collect(Collectors.toList());
    }
}