package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventReadDto;
import school.faang.user_service.dto.UserReadDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.mapper.UserReadMapper;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class EventReadMapperTest {

    @Mock
    private UserReadMapper userReadMapper;
    @InjectMocks
    private EventReadMapper eventReadMapper;
    private final LocalDateTime DATE_NOW = LocalDateTime.now();

    @Test
    void map() {
        Event event = getEvent();
        UserReadDto userReadDto = getUserReadDto(event.getOwner().getId());
        doReturn(userReadDto).when(userReadMapper).map(getUser(userReadDto.getId()));

        EventReadDto actualResult = eventReadMapper.map(event);

        EventReadDto expectedResult = getEventReadDto();
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

    private EventReadDto getEventReadDto() {
        Event event = getEvent();
        return new EventReadDto(
                event.getId(),
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                getUserReadDto(event.getOwner().getId()),
                event.getDescription(),
                event.getLocation(),
                event.getMaxAttendees()
        );
    }

    private UserReadDto getUserReadDto(Long id) {
        return new UserReadDto(id);
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