package school.faang.user_service.filter.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RelatedSkillsFilterTest {

    @InjectMocks
    private RelatedSkillsFilter relatedSkillsFilter;

    private final List<SkillDto> relatedSkillsDto = List.of(

            SkillDto.builder()
                    .id(1L)
                    .title("title")
                    .build());

    private EventFilterDto eventFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у EventFilterDto поле relatedSkills не null, тогда возвращаем true")
        void whenFieldValidThenReturnTrue() {

            eventFilterDto = EventFilterDto.builder()
                    .relatedSkills(relatedSkillsDto)
                    .build();

            assertTrue(relatedSkillsFilter.isApplicable(eventFilterDto));
        }

        @Test
        @DisplayName("Если у EventFilterDto заполнено поле relatedSkills, тогда возвращаем отфильтрованный список")
        void whenFieldFilledThenReturnFilteredList() {
            Stream<Event> eventStream = Stream.of(
                    Event.builder()
                            .relatedSkills(List.of(
                                    Skill.builder()
                                            .id(1L)
                                            .title("title")
                                            .build()))
                            .build(),
                    Event.builder()
                            .relatedSkills(List.of(
                                    Skill.builder()
                                            .id(2L)
                                            .title("title2")
                                            .build()))
                            .build());

            eventFilterDto = EventFilterDto.builder()
                    .relatedSkills(relatedSkillsDto)
                    .build();

            Stream<Event> resultEventStream = Stream.of(
                    Event.builder()
                            .relatedSkills(List.of(
                                    Skill.builder()
                                            .id(1L)
                                            .title("title")
                                            .build()))
                            .build());

            List<Event> events = relatedSkillsFilter.apply(eventStream, eventFilterDto).toList();
            System.out.println(events);
            assertEquals(resultEventStream.toList(), events);
        }

    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Если у EventFilterDto поле relatedSkills null тогда возвращаем false")
        void whenFieldNullThenReturnFalse() {

            eventFilterDto = EventFilterDto.builder()
                    .relatedSkills(null)
                    .build();

            assertFalse(relatedSkillsFilter.isApplicable(eventFilterDto));
        }
    }
}