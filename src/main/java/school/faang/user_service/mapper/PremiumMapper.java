package school.faang.user_service.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.user.UserService;

@Mapper(componentModel = "spring",
        uses = {SkillMapper.class, UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PremiumMapper {

    @Mapping(source = "id", target = "premiumId")
    @Mapping(source = "user.id", target = "userId")
    PremiumDto toDto(Premium premium);

    @Mapping(source = "premiumId", target = "id")
    @Mapping(source = "userId", target = "user", qualifiedByName = "userById")
    Premium toEntity(PremiumDto premiumDto, @Context UserService userService);

    default User userById(Long id) {
        return User.builder().id(id).build();
    }
}
