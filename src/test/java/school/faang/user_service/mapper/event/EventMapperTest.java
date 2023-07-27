//package school.faang.user_service.mapper.event;
//
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import school.faang.user_service.dto.event.EventDto;
//import school.faang.user_service.entity.Skill;
//import school.faang.user_service.entity.User;
//import school.faang.user_service.entity.event.Event;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@ExtendWith(MockitoExtension.class)
//class EventMapperTest {
//    @Spy
//    private EventMapperImpl eventMapper;
//    private Event event;
//    private EventDto eventDto;
//
//    @BeforeEach
//    void setUp() {
//        Skill skill1 = Skill.builder().id(1L).build();
//        Skill skill2 = Skill.builder().id(2L).build();
//        Skill skill3 = Skill.builder().id(3L).build();
//
//        User owner = new User();
//        owner.setId(1L);
//
//        event = Event.builder()
//                .id(1L)
//                .title("Test Event")
//                .startDate(LocalDateTime.now())
//                .endDate(LocalDateTime.now().plusHours(2))
//                .owner(owner)
//                .description("This is a test event")
//                .relatedSkills(Arrays.asList(skill1, skill2, skill3))
//                .location("Test Location")
//                .maxAttendees(50)
//                .build();
//
//        eventDto = EventDto.builder()
//                .id(1L)
//                .title("Test Event")
//                .startDate(LocalDateTime.now())
//                .endDate(LocalDateTime.now().plusHours(2))
//                .ownerId(1L)
//                .description("This is a test event")
//                .skillIds(Arrays.asList(1L, 2L, 3L))
//                .location("Test Location")
//                .maxAttendees(50)
//                .build();
//    }
//
//    @Test
//    void toDto_AllFieldsMustMatch() {
//        EventDto eventDto1 = eventMapper.toDto(event);
//        assertAll(() -> {
//            assertEquals(eventDto.getId(), eventDto1.getId());
//            assertEquals(event.getTitle(), eventDto1.getTitle());
//            assertEquals(event.getStartDate(), eventDto1.getStartDate());
//            assertEquals(event.getEndDate(), eventDto1.getEndDate());
//            assertEquals(event.getOwner().getId(), eventDto1.getOwnerId());
//            assertEquals(event.getDescription(), eventDto1.getDescription());
//            assertEquals(event.getLocation(), eventDto1.getLocation());
//            assertEquals(event.getMaxAttendees(), eventDto1.getMaxAttendees());
//            List<Long> expectedSkillIds = event.getRelatedSkills().stream()
//                    .map(Skill::getId)
//                    .toList();
//            assertEquals(expectedSkillIds, eventDto1.getSkillIds());
//
//        });
//    }
//
//    @Test
//    void toEntity_AllFieldsMustMatch() {
//        Event event1 = eventMapper.toEvent(eventDto);
//        assertAll(() -> {
//            assertEquals(event.getId(), event1.getId());
//            assertEquals(eventDto.getTitle(), event1.getTitle());
//            assertEquals(eventDto.getStartDate(), event1.getStartDate());
//            assertEquals(eventDto.getEndDate(), event1.getEndDate());
//            assertEquals(eventDto.getOwnerId(), event1.getOwner().getId());
//            assertEquals(eventDto.getDescription(), event1.getDescription());
//            assertEquals(eventDto.getLocation(), event1.getLocation());
//            assertEquals(eventDto.getMaxAttendees(), event1.getMaxAttendees());
//            List<Skill> expectedSkills = eventDto.getSkillIds().stream()
//                    .map(id -> Skill.builder().id(id).build())
//                    .toList();
//            assertEquals(expectedSkills, event1.getRelatedSkills());
//
//        });
//    }
//}