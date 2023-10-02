package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.LightUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GoalMapper.class, SkillMapper.class},
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    @Mapping(target = "followers", expression = "java(entity.getFollowers().stream().map(fol -> fol.getId()).toList())")
    @Mapping(target = "followees", expression = "java(entity.getFollowees().stream().map(fol -> fol.getId()).toList())")
    @Mapping(target = "mentors", expression = "java(entity.getMentors().stream().map(men -> men.getId()).toList())")
    @Mapping(target = "mentees", expression = "java(entity.getMentees().stream().map(men -> men.getId()).toList())")
    UserDto toDto(User entity);

    List<UserDto> toDtos(List<User> entities);

}
