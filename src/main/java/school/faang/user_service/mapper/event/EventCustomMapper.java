package school.faang.user_service.mapper.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.SkillCustomMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventCustomMapper {
    private final SkillCustomMapper skillCustomMapper;

    public EventDto eventToEventDto(Event event) {
        List<SkillDto> skillDtoList = event.getRelatedSkills()
                .stream()
                .map(skillCustomMapper::fromSkillToSkillDto)
                .toList();

        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .location(event.getLocation())
                .maxAttendees(event.getMaxAttendees())
                .ownerId(event.getOwner().getId())
                .relatedSkills(skillDtoList)
                .type(event.getType())
                .status(event.getStatus())
                .build();
    }
}
