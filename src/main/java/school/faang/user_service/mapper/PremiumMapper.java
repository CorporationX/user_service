package school.faang.user_service.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.user.UserService;

@Mapper(componentModel = "spring",
        uses = {SkillMapper.class, UserService.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PremiumMapper {

    @Mapping(source = "id", target = "premiumId")
    @Mapping(source = "user.id", target = "userId")
    PremiumDto toDto(Premium premium);

    @Mapping(source = "premiumId", target = "id")
    @Mapping(source = "userId", target = "user", qualifiedByName = "findUserById")
    Premium toEntity(PremiumDto premiumDto, @Context UserService userService);

    @Named("findUserById")
    default User findUserById(Long id, @Context UserService userService) {
        return userService.findUserById(id);
    }
}
