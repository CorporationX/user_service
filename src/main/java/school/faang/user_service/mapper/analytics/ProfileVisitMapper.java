package school.faang.user_service.mapper.analytics;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.analytics.ProfileVisitCreateDto;
import school.faang.user_service.dto.analytics.ProfileVisitViewDto;
import school.faang.user_service.entity.analytics.ProfileVisit;
import school.faang.user_service.messaging.dto.ProfileVisitEvent;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Маппер для преобразования между сущностями {@link ProfileVisit},
 * DTO и событиями {@link SearchAppearanceEvent}.
 *
 * <p>Используется MapStruct для автоматической генерации кода преобразования.</p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProfileVisitMapper {
    ProfileVisitCreateDto toDto(ProfileVisitEvent event);

    ProfileVisitViewDto toDto(ProfileVisit entity);

    List<ProfileVisitViewDto> toDtoList(List<ProfileVisit> entities);

    ProfileVisit toEntity(ProfileVisitCreateDto dto);
}
