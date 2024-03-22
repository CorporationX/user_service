package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MenteeDTO;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenteeMapper {
    @Mapping(source = "mentors", target = "mentorsIds", qualifiedByName = "convertUserToId")
    MenteeDTO toDTO(User mentee);

    User toEntity(MenteeDTO menteeDTO);

    @Named("convertUserToId")
    default List<Long> convertUserToId(List<User> users) {
        return users.stream().map(User::getId).collect(Collectors.toList());
    }
}
