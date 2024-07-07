package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

//    @Mapping(target = "id", qualifiedByName = "mapUser")
//    UserDto toDto(User user);
//
//    User toEntity(UserDto userDto);

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    UserDto toDto(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    List<UserDto> toDtoList(List<User> users);

//    @Named("mapUser")
//    default List<Long> mapUser(List<User> users) {
//        return users != null ? users.stream().map(User::getId).toList() : Collections.emptyList();
//    }
}