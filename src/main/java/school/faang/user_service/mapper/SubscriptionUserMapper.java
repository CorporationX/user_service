package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriptionUserMapper {

    SubscriptionUserDto toDto(User user);

    User toEntity(SubscriptionUserDto userDto);

    List<SubscriptionUserDto> toDto(List<User> users);

    List<User> toEntity(List<SubscriptionUserDto> usersDto);
}
