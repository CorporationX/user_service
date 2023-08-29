package school.faang.user_service.mapper.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EventStartEventMapper {

    @Mapping(target = "userIds", source = "attendees", qualifiedByName = "toUserIds")
    EventStartEventDto toDto(Event event);

    @Named("toUserIds")
    default List<Long> toUserIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }
}