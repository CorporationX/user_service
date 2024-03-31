package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface SubscriptionUserMapper {
    List<SubscriptionUserDto> toDto (List<User> users);

    User toEntity(SubscriptionUserDto userDto);

    SubscriptionUserDto toDto(User user);

    List<User> toEntity(List<SubscriptionUserDto> users);

}
