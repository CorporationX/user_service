package school.faang.user_service.mapper.premium;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PremiumMapper {

    @Mapping(source = "user.id", target = "userId")
    PremiumDto toDto(Premium entity);

    @Mapping(source = "userId", target = "user", qualifiedByName = "mapIdToUser")
    Premium toEntity(PremiumDto dto);

    @Named("mapIdToUser")
    default User mapIdToUser(Long id) {
        User user = new User();
        user.setId(id);

        return user;
    }
}
