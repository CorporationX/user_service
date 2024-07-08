package school.faang.user_service.mapper.premium;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

/**
 * @author Evgenii Malkov
 */
@Mapper(componentModel = "spring")
public interface PremiumMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.id", target = "userId")
    PremiumDto toPremiumDto(Premium premium);
}
