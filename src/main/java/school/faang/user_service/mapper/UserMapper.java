package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    SubscriptionUserDto toDto(User user);

    User toEntity(SubscriptionUserDto subscriptionUserDto);

    List<SubscriptionUserDto> toDto(List<User> list);

    List<User> toEntity(List<SubscriptionUserDto> list);
}
