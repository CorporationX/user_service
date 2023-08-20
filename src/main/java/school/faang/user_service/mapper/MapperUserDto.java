package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.mapper.SkillMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GoalMapper.class, SkillMapper.class, CountryMapper.class},
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MapperUserDto {
    List<UserDto> toDto(List<User> userList);
    List<User> toEntity(List<UserDto> userDtoList);
}
