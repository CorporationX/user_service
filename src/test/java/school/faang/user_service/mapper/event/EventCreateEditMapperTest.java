package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EventCreateEditMapperTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private EventCreateEditMapper mapper;
    private final LocalDateTime DATE_NOW = LocalDateTime.now();

    @Test
    void map() {
        EventCreateEditDto eventCreateEditDto = getEventCreateEditDto();
        User user = getUser(eventCreateEditDto.getOwnerId());
        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(1L);
        Mockito.doReturn(eventCreateEditDto.getRelatedSkillIds().stream()
                        .map(this::getSkill)
                        .toList())
                .when(skillRepository).findAllById(eventCreateEditDto.getRelatedSkillIds());

        Event actualResult = mapper.map(eventCreateEditDto);

        Event expectedResult = getEvent();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private Event getEvent() {
        return Event.builder()
                .title("title")
                .startDate(DATE_NOW)
                .endDate(DATE_NOW.plusDays(1))
                .owner(getUser(1L))
                .description("description")
                .relatedSkills(Stream.of(1L, 2L)
                        .map(this::getSkill)
                        .toList())
                .type(EventType.POLL)
                .status(EventStatus.PLANNED)
                .maxAttendees(1)
                .build();
    }

    private EventCreateEditDto getEventCreateEditDto() {
        Event event = getEvent();
        return new EventCreateEditDto(
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                event.getOwner().getId(),
                event.getDescription(),
                event.getRelatedSkills().stream()
                        .map(Skill::getId)
                        .toList(),
                event.getLocation(),
                event.getMaxAttendees(),
                event.getType(),
                event.getStatus()
        );
    }


    private User getUser(Long id) {
        return User.builder()
                .id(id)
                .build();
    }


    private Skill getSkill(Long id) {
        return Skill.builder()
                .id(id)
                .build();
    }
}