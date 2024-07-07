package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "relatedSkills", target = "relatedSkillIds", qualifiedByName = "mapSkills")
    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event entity);

    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "mapOwner")
    Event toEntity(EventDto eventDto);

    @Named("mapSkills")
    default List<Long> mapSkillsToSkillIds(List<Skill> skills) {
        // без этого ломается eventService.getEvent() если relatedSkills = null
        if (skills == null) {
            skills = new ArrayList<>();
        }
        return skills.stream().map(Skill::getId).toList();
    }

    @Named("mapOwner")
    default User mapOwnerIdToOwner(Long ownerId) {
        User user = new User();
        user.setId(ownerId);
        return user;
    }
}
