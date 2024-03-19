package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "premium", target = "isPremium", qualifiedByName = "isUserPremium")
    @Mapping(source = "country.id", target = "countryId")
    UserDto toDto(User user);

    @Mapping(target = "premium", ignore = true)
    @Mapping(source = "countryId", target = "country.id")
    User toEntity(UserDto userDto);

    List<UserDto> toDto(List<User> users);

    @Named("isUserPremium")
    default boolean isUserPremium(Premium premium) {
        if (premium != null) {
            return premium.getEndDate().isAfter(LocalDateTime.now());
        }
        return false;
    }
}
