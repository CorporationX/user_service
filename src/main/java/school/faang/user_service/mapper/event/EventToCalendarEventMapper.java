package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.controller.event.CalendarEventDto;
import school.faang.user_service.dto.event.EventDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventToCalendarEventMapper {
    EventToCalendarEventMapper INSTANCE = Mappers.getMapper(EventToCalendarEventMapper.class);

    @Mapping(source = "title", target = "summary")
    @Mapping(source = "startDate", target = "startTime")
    @Mapping(source = "endDate", target = "endTime")
    CalendarEventDto toCalendarEventDto(EventDto eventDto);
}
