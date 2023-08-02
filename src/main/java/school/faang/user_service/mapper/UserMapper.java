package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "goals",target = "goalIds",qualifiedByName = "listIdGoalUser" )
    UserDto userToDto(User user);
    @Mapping(target = "goals",ignore = true)
    User dtoToUser(UserDto userDto);
    List<UserDto> toUserDtoList(List<User> users);
    List<User> toUserList(List<UserDto> userDtoList);

    @Named("listIdGoalUser")
    default List<Long> listIdGoalUser(List<Goal> goalUser){
        return goalUser.stream().map(Goal::getId).toList();
    }
}
