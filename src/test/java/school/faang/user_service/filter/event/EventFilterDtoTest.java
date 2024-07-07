package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilterDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventFilterDtoTest {
    private EventFilterDto eventFilterDto;

    @BeforeEach
    void setUp() {
        eventFilterDto = new EventFilterDto();
    }

    @Test
    public void testGetFilters(){
        int sizeEmptyListExp = 0;
        int sizeNotEmptyListExp = 11;
        List<Predicate<Event>> emptyFilterList = eventFilterDto.getFilters();
        int sizeEmptyListActual = emptyFilterList.size();

        eventFilterDto.setTitle("title");
        eventFilterDto.setDescription("description");
        eventFilterDto.setStartDateBefore(LocalDateTime.of(2020, 1, 1, 10,10,10));
        eventFilterDto.setStartDateAfter(LocalDateTime.of(2020, 1, 1, 10,10,10));
        eventFilterDto.setEndDateBefore(LocalDateTime.of(2020, 1, 1, 10,10,10));
        eventFilterDto.setEndDateAfter(LocalDateTime.of(2020, 1, 1, 10,10,10));
        eventFilterDto.setLocation("location");
        eventFilterDto.setMaxAttendeesFrom(1);
        eventFilterDto.setMaxAttendeesTo(3);
        eventFilterDto.setOwner(new User());
        eventFilterDto.setRelatedSkillIds(List.of(1L, 2L));
        eventFilterDto.setOptionFilterSkills(true);

        List<Predicate<Event>> notEmptyFilterList = eventFilterDto.getFilters();
        int sizeNotEmptyFilterListActual = notEmptyFilterList.size();

        assertEquals(sizeEmptyListExp, sizeEmptyListActual);
        assertEquals(sizeNotEmptyListExp, sizeNotEmptyFilterListActual);
    }

    @Test
    public void testTitleFilter(){
        String titleFirstEvent = "titleFirstEvent";
        Event eventFirst = new Event();
        eventFirst.setTitle(titleFirstEvent);
        String titleSecondEvent = "titleSecondEvent";
        Event eventSecond = new Event();
        List<Event> events = List.of(eventFirst, eventSecond);
        eventSecond.setTitle(titleSecondEvent);
        String titleFilter = "first";
        eventFilterDto.setTitle(titleFilter);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventFirst, filteredList.get(0));
    }

    @Test
    public void testDescriptionFilter(){
        String descriptionFirstEvent = "some description1";
        Event eventFirst = new Event();
        eventFirst.setTitle(descriptionFirstEvent);
        String descriptionSecondEvent = "some description2";
        Event eventSecond = new Event();
        eventSecond.setTitle(descriptionSecondEvent);
        String descriptionFilter = "description2";
        eventFilterDto.setTitle(descriptionFilter);
        List<Event> events = List.of(eventFirst, eventSecond);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventSecond, filteredList.get(0));
    }

    @Test
    public void testStartDateAfterFilter(){
        LocalDateTime startDateFirst = LocalDateTime.of(2024, 7, 5, 12, 0);
        LocalDateTime startDateSecond = LocalDateTime.of(2024, 7, 7, 12, 0);
        LocalDateTime startDateAfterFilter = LocalDateTime.of(2024, 7, 5, 13, 0);
        Event eventFirst = new Event();
        eventFirst.setStartDate(startDateFirst);
        Event eventSecond = new Event();
        eventSecond.setStartDate(startDateSecond);
        eventFilterDto.setStartDateAfter(startDateAfterFilter);
        List<Event> events = List.of(eventFirst, eventSecond);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventSecond, filteredList.get(0));
    }

    @Test
    public void testEndDateBeforeFilter(){
        LocalDateTime endDateFirst = LocalDateTime.of(2024, 7, 5, 12, 0);
        LocalDateTime endDateSecond = LocalDateTime.of(2024, 7, 7, 12, 0);
        LocalDateTime endDateBeforeFilter = LocalDateTime.of(2024, 7, 6, 13, 0);
        Event eventFirst = new Event();
        eventFirst.setEndDate(endDateFirst);
        Event eventSecond = new Event();
        eventSecond.setEndDate(endDateSecond);
        eventFilterDto.setEndDateBefore(endDateBeforeFilter);
        List<Event> events = List.of(eventFirst, eventSecond);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventFirst, filteredList.get(0));
    }

    @Test
    public void testEndDateAfterFilter(){
        LocalDateTime endDateFirst = LocalDateTime.of(2024, 7, 5, 12, 0);
        LocalDateTime endDateSecond = LocalDateTime.of(2024, 7, 7, 12, 0);
        LocalDateTime endDateAfterFilter = LocalDateTime.of(2024, 7, 6, 13, 0);
        Event eventFirst = new Event();
        eventFirst.setEndDate(endDateFirst);
        Event eventSecond = new Event();
        eventSecond.setEndDate(endDateSecond);
        eventFilterDto.setEndDateAfter(endDateAfterFilter);
        List<Event> events = List.of(eventFirst, eventSecond);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventSecond, filteredList.get(0));
    }

    @Test
    public void testStartDateBeforeFilter(){
        LocalDateTime startDateFirst = LocalDateTime.of(2024, 7, 5, 12, 0);
        LocalDateTime startDateSecond = LocalDateTime.of(2024, 7, 7, 12, 0);
        LocalDateTime startDateBeforeFilter = LocalDateTime.of(2024, 7, 7, 11, 0);
        Event eventFirst = new Event();
        eventFirst.setStartDate(startDateFirst);
        Event eventSecond = new Event();
        eventSecond.setStartDate(startDateSecond);
        eventFilterDto.setStartDateBefore(startDateBeforeFilter);
        List<Event> events = List.of(eventFirst, eventSecond);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventFirst, filteredList.get(0));

//        eventFilterDto.setStartDateBefore(LocalDateTime.of(2020, 1, 1, 10,10,10));
//        eventFilterDto.setStartDateAfter(LocalDateTime.of(2020, 1, 1, 10,10,10));
//        eventFilterDto.setEndDateBefore(LocalDateTime.of(2020, 1, 1, 10,10,10));
//        eventFilterDto.setEndDateAfter(LocalDateTime.of(2020, 1, 1, 10,10,10));
//        eventFilterDto.setLocation("location");
//        eventFilterDto.setMaxAttendeesFrom(1);
//        eventFilterDto.setMaxAttendeesTo(3);
//        eventFilterDto.setOwner(new User());
//        eventFilterDto.setRelatedSkillIds(List.of(1L, 2L));
//        eventFilterDto.setOptionFilterSkills(true);

    }

    @Test
    public void testLocationFilter(){
        String locationFirstEvent = "Russian Federation";
        Event eventFirst = new Event();
        eventFirst.setLocation(locationFirstEvent);
        String locationSecondEvent = "France";
        Event eventSecond = new Event();
        List<Event> events = List.of(eventFirst, eventSecond);
        eventSecond.setLocation(locationSecondEvent);
        String locationFilter = "Russian";
        eventFilterDto.setLocation(locationFilter);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventFirst, filteredList.get(0));
    }

    @Test
    public void testMaxAttendeesFromFilter(){
        int maxAttendeesFirstEvent = 2;
        int maxAttendeesSecondEvent = 4;
        int maxAttendeesFilterFrom = 3;
        Event eventFirst = new Event();
        eventFirst.setMaxAttendees(maxAttendeesFirstEvent);
        Event eventSecond = new Event();
        eventSecond.setMaxAttendees(maxAttendeesSecondEvent);
        List<Event> events = List.of(eventFirst, eventSecond);
        eventFilterDto.setMaxAttendeesFrom(maxAttendeesFilterFrom);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventSecond, filteredList.get(0));
    }

    @Test
    public void testMaxAttendeesToFilter(){
        int maxAttendeesFirstEvent = 2;
        int maxAttendeesSecondEvent = 4;
        int maxAttendeesFilterTo = 3;
        Event eventFirst = new Event();
        eventFirst.setMaxAttendees(maxAttendeesFirstEvent);
        Event eventSecond = new Event();
        eventSecond.setMaxAttendees(maxAttendeesSecondEvent);
        List<Event> events = List.of(eventFirst, eventSecond);
        eventFilterDto.setMaxAttendeesTo(maxAttendeesFilterTo);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventFirst, filteredList.get(0));
    }

    @Test
    public void testOwnerFilter(){
        User userFirstEvent = new User();
        userFirstEvent.setId(1L);
        User userSecondEvent = new User();
        userSecondEvent.setId(2L);
        User userFilter = new User();
        userFilter.setId(1L);
        Event eventFirst = new Event();
        eventFirst.setOwner(userFirstEvent);
        Event eventSecond = new Event();
        eventSecond.setOwner(userSecondEvent);
        List<Event> events = List.of(eventFirst, eventSecond);
        eventFilterDto.setOwner(userFilter);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventFirst, filteredList.get(0));
    }

    @Test
    public void testRelatedSkillsAnyFilter(){
        Skill skillFirst = new Skill();
        Skill skillSecond = new Skill();
        Skill skillThird = new Skill();
        Skill skillFourth = new Skill();
        skillFirst.setId(1L);
        skillSecond.setId(2L);
        skillThird.setId(3L);
        skillFourth.setId(4L);
        Event eventFirst = new Event();
        Event eventSecond = new Event();
        eventFirst.setRelatedSkills(List.of(skillFirst, skillSecond));
        eventSecond.setRelatedSkills(List.of(skillThird, skillFourth));
        List<Event> events = List.of(eventFirst, eventSecond);
        eventFilterDto.setRelatedSkillIds(List.of(3L));
        eventFilterDto.setOptionFilterSkills(false);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventSecond, filteredList.get(0));
    }

    @Test
    public void testRelatedSkillsAllFilter(){
        Skill skillFirst = new Skill();
        Skill skillSecond = new Skill();
        Skill skillThird = new Skill();
        Skill skillFourth = new Skill();
        skillFirst.setId(1L);
        skillSecond.setId(2L);
        skillThird.setId(3L);
        skillFourth.setId(4L);
        Event eventFirst = new Event();
        Event eventSecond = new Event();
        eventFirst.setRelatedSkills(List.of(skillFirst, skillSecond));
        eventSecond.setRelatedSkills(List.of(skillThird, skillFourth));
        List<Event> events = List.of(eventFirst, eventSecond);
        eventFilterDto.setRelatedSkillIds(List.of(1L, 2L, 3L));
        eventFilterDto.setOptionFilterSkills(true);
        int sizeFilteredListExp = 1;

        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventFirst, filteredList.get(0));
    }

    @Test
    public void testSeveralFilter(){
        // Arrange
        String firstTitle = "firstTitle filter";
        String secondTitle = "secondTitle filter";
        String thirdTitle = "thirdTitle";
        String firstDescription = "firstDescription";
        String secondDescription = "secondDescription filter";
        String thirdDescription = "thirdDescription filter";
        int firstMaxAttendees = 1;
        int secondMaxAttendees = 2;
        int thirdMaxAttendees = 3;

        Event eventFirst = new Event();
        eventFirst.setTitle(firstTitle);
        eventFirst.setDescription(firstDescription);
        eventFirst.setMaxAttendees(firstMaxAttendees);

        Event eventSecond = new Event();
        eventSecond.setTitle(secondTitle);
        eventSecond.setDescription(secondDescription);
        eventSecond.setMaxAttendees(secondMaxAttendees);

        Event eventThird = new Event();
        eventThird.setTitle(thirdTitle);
        eventThird.setDescription(thirdDescription);
        eventThird.setMaxAttendees(thirdMaxAttendees);

        List<Event> events = List.of(eventFirst, eventSecond, eventThird);
        eventFilterDto.setTitle("filter");
        eventFilterDto.setDescription("filter");
        eventFilterDto.setMaxAttendeesFrom(secondMaxAttendees);
        int sizeFilteredListExp = 1;

        // Action
        List<Event> filteredList = filterEvents(events, eventFilterDto.getFilters());
        int sizeFilteredListActual = filteredList.size();

        // Assert
        assertEquals(sizeFilteredListExp, sizeFilteredListActual);
        assertEquals(eventSecond, filteredList.get(0));
    }

    private List<Event> filterEvents(List<Event> events, List<Predicate<Event>> filters) {
        return events.stream()
                .filter(e -> filters.stream()
                        .allMatch(f -> f.test(e)))
                .toList();
    }
}
