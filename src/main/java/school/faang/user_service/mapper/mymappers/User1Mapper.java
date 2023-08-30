package school.faang.user_service.mapper.mymappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {Goal1Mapper.class, Skill1Mapper.class, Country1Mapper.class},
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface User1Mapper {

    @Mapping(target = "preference", source = "contactPreference.preference")
    @Mapping(target = "followers", expression = "java(entity.getFollowers().stream().map(fol -> fol.getId()).toList())")
    @Mapping(target = "followees", expression = "java(entity.getFollowees().stream().map(fol -> fol.getId()).toList())")
    @Mapping(target = "mentors", expression = "java(entity.getMentors().stream().map(men -> men.getId()).toList())")
    @Mapping(target = "mentees", expression = "java(entity.getMentees().stream().map(men -> men.getId()).toList())")
    UserDto toDto(User entity);

    List<UserDto> toDtos(List<User> entities);
}
