package school.faang.user_service.mapper;

import com.json.student.PersonSchemaForUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserDto userDto);

    UserDto toDto(User user);
    UserDto toDto(PersonSchemaForUser person);

    List<UserDto> toDto(List<User> users);
    List<UserDto> toDtoPersons(List<PersonSchemaForUser> persons);
}