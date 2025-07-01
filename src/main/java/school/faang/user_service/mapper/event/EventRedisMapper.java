package school.faang.user_service.mapper.event;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.model.event.EventRedisModel;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface EventRedisMapper {

    EventRedisModel toEventRedisModel(Event event);

    Event toEventEntity(EventRedisModel eventRedisModel);
}
