package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionDto;
import school.faang.user_service.entity.User;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    List<UserDto> toDtoList(List<User> users);
    UserSubscriptionDto toDto(User user);
    User toEntity(UserSubscriptionDto userDto);

    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDto toUserDto(User user);

     @Mapping(source = "preference", target = "contactPreference.preference")
    User toEntity(UserDto userDto);
}