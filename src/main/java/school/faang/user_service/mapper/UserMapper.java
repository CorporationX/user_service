package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "goals", target = "goalIds", qualifiedByName = "listToGoalDto")
    UserDto userToDto(User user);

    @Mapping(source = "goalIds", target = "goals", qualifiedByName = "goalDtoToList")
    User dtoToUser(UserDto userDto);

    @Named("listToGoalDto")
    default List<GoalDto> listToGoalDto(List<Goal> goals) {
        List<GoalDto> goalDtos = new ArrayList<>();
        goals.stream().forEach(goal ->
                goalDtos.add(GoalMapper.INSTANCE.toDto(goal)));
        return goalDtos;
    }

    @Named("goalDtoToList")
    default List<Goal> goalDtoToList(List<GoalDto> list) {
        List<Goal> goalDtos = new ArrayList<>();
        list.stream().forEach(lists ->
                goalDtos.add(GoalMapper.INSTANCE.toEntity(lists)));
        return goalDtos;
    }

    List<UserDto> toUserDtoList(List<User> users);

    List<User> toUserList(List<UserDto> userDtoList);
}
