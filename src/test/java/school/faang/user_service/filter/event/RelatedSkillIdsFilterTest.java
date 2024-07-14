package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.filter.impl.RelatedSkillsPatternFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RelatedSkillIdsFilterTest {
    private EventFilterDto eventFilterDto;
    private List<Event> events;
    private EventFilter filter;

    @BeforeEach
    void setUp() {
        eventFilterDto = new EventFilterDto();
        events = prepareEvents();
        filter = new RelatedSkillsPatternFilter();
    }

    @Test
    public void testApplyAnySkills() {
        // Arrange
        // ищем события для которых в умениях есть "third" или "sixth" (могут быть и другие умения)
        eventFilterDto.setOptionFilterSkills(false);
        eventFilterDto.setRelatedSkillIsPattern(prepareSkillsPatternForAny());

        int sizeFilteredListExp = 2; // должен найти 2 события
        Event eventExpectationFirst = events.get(1);
        Event eventExpectationSecond = events.get(2);

        // Act
        List<Event> filteredList = filter.apply(events.stream(), eventFilterDto).toList();
        int sizeFilteredListActual = filteredList.size();

        // Assert
        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventExpectationFirst, filteredList.get(0));
        assertEquals(eventExpectationSecond, filteredList.get(1));
    }

    @Test
    public void testTestAnySkills() {
        // Arrange
        // ищем события для которых в умениях есть "third" или "sixth" (могут быть и другие умения)
        eventFilterDto.setOptionFilterSkills(false);
        eventFilterDto.setRelatedSkillIsPattern(prepareSkillsPatternForAny());

        int sizeFilteredListExp = 2; // должен найти 2 события
        Event eventExpectationFirst = events.get(1);
        Event eventExpectationSecond = events.get(2);

        // Act
        List<Event> filteredList = events.stream()
                .filter(e -> filter.test(e, eventFilterDto))
                .toList();
        int sizeFilteredListActual = filteredList.size();

        // Assert
        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventExpectationFirst, filteredList.get(0));
        assertEquals(eventExpectationSecond, filteredList.get(1));
    }

    private List<Skill> prepareSkillsPatternForAny() {
        Skill skillFilterFirst = new Skill();
        skillFilterFirst.setTitle("third");
        Skill skillFilterSecond = new Skill();
        skillFilterSecond.setTitle("sixth");
        return List.of(skillFilterFirst, skillFilterSecond);
    }

    @Test
    public void testApplyAllSkills() {
        // Arrange

        // ищем события, в которых не должно быть умений, кроме тех, что мы ищём.
        eventFilterDto.setOptionFilterSkills(true);
        eventFilterDto.setRelatedSkillIsPattern(prepareSkillsPatternForAll());
        int sizeFilteredListExp = 1;
        EventFilter filter = new RelatedSkillsPatternFilter();
        Event eventExpectation = events.get(0);

        // Act
        List<Event> filteredList = events.stream()
                .filter(e -> filter.test(e, eventFilterDto))
                .toList();
        int sizeFilteredListActual = filteredList.size();

        // Assert
        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventExpectation, filteredList.get(0));
    }

    private List<Skill> prepareSkillsPatternForAll() {
        Skill skillFilterFirst = new Skill();
        skillFilterFirst.setTitle("first");
        Skill skillFilterSecond = new Skill();
        skillFilterSecond.setTitle("second");
        Skill skillFilterThird = new Skill();
        skillFilterThird.setTitle("third");
        return List.of(skillFilterFirst, skillFilterSecond, skillFilterThird);
    }

    private List<Event> prepareEvents() {
        Skill skillFirst = new Skill();
        skillFirst.setTitle("skillFirst");
        Skill skillSecond = new Skill();
        skillSecond.setTitle("skillSecond");
        Skill skillThird = new Skill();
        skillThird.setTitle("skillThird");
        Skill skillFourth = new Skill();
        skillFourth.setTitle("skillFourth");
        Skill skillFifth = new Skill();
        skillFifth.setTitle("skillFifth");
        Skill skillSixth = new Skill();
        skillSixth.setTitle("skillSixth");

        Event eventFirst = new Event();
        Event eventSecond = new Event();
        Event eventThird = new Event();
        eventFirst.setRelatedSkills(List.of(skillFirst, skillSecond));
        eventSecond.setRelatedSkills(List.of(skillThird, skillFourth));
        eventThird.setRelatedSkills(List.of(skillFifth, skillSixth));
        return List.of(eventFirst, eventSecond, eventThird);
    }
}
