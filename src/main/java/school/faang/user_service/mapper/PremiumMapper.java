package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PremiumMapper {

    @Mapping(source = "id", target = "user.id")
    Premium toEntity(Long id, LocalDateTime startDate, LocalDateTime endDate);
}
