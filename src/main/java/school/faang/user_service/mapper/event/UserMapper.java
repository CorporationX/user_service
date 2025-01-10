package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", uses = {SkillMapper.class})
public interface UserMapper {
    @Mapping(source = "skills", target = "skills")
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}