package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.LightUserDto;
import school.faang.user_service.dto.user.UserDto;import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GoalMapper.class, SkillMapper.class},
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MapperUserDto {

    @Mapping(target = "followers", expression = "java(entity.getFollowers().stream().map(fol -> fol.getId()).toList())")
    @Mapping(target = "followees", expression = "java(entity.getFollowees().stream().map(fol -> fol.getId()).toList())")
    @Mapping(target = "mentors", expression = "java(entity.getMentors().stream().map(men -> men.getId()).toList())")
    @Mapping(target = "mentees", expression = "java(entity.getMentees().stream().map(men -> men.getId()).toList())")
    UserDto toDto(User entity);

    @Mapping(target = "followers", expression = "java(userDto.followers().stream().map(folId -> User.builder().id(folId).build()).toList())")
    @Mapping(target = "followees", expression = "java(userDto.followees().stream().map(folId -> User.builder().id(folId).build()).toList())")
    @Mapping(target = "mentors", expression = "java(userDto.mentors().stream().map(menId -> User.builder().id(menId).build()).toList())")
    @Mapping(target = "mentees", expression = "java(userDto.mentees().stream().map(menId -> User.builder().id(menId).build()).toList())")
    User toEntity(UserDto userDto);

    List<UserDto> toDto(List<User> userList);
    List<User> toEntity(List<UserDto> userDtoList);


    @Mapping(target = "followerIds", expression = "java(entity.getFollowers().stream().map(fol -> fol.getId()).toList())")
    LightUserDto toLight(User entity);
}
