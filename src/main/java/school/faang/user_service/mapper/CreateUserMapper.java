package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)

public interface CreateUserMapper {
    @Mapping(target = "countryId", source = "country.id")
    UserCreateDto toDto(User user);

    @Mapping(target = "country.id", source = "countryId")
    User toEntity(UserCreateDto userDto);

    List<UserCreateDto> toDto(List<User> users);
}
