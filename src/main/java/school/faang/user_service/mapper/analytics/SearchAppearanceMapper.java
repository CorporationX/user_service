package school.faang.user_service.mapper.analytics;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.analytics.SearchAppearanceCreateDto;
import school.faang.user_service.dto.analytics.SearchAppearanceViewDto;
import school.faang.user_service.entity.analytics.SearchAppearance;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * MapStruct-маппер для преобразования сущностей и DTO,
 * связанных с событием {@code SearchAppearance}.
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
public interface SearchAppearanceMapper {
    SearchAppearanceCreateDto toDto(SearchAppearanceEvent event);

    SearchAppearanceViewDto toDto(SearchAppearance entity);

    List<SearchAppearanceViewDto> toDtoList(List<SearchAppearance> entities);

    SearchAppearance toEntity(SearchAppearanceCreateDto dto);
}
