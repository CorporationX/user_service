package school.faang.user_service.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,componentModel = "spring")
public interface MapperUserDto {
    List<UserDto> toDto(List<User> userList);
    List<User> toEntity(List<UserDto> userDtoList);
}
