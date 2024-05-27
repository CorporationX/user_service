package school.faang.user_service.mapper;


import com.json.student.Person;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(Person person);

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);
}
