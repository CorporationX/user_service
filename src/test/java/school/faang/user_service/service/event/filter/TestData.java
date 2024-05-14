package school.faang.user_service.service.event.filter;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestData {
    public final static List<Event> ALL_EVENTS = new ArrayList<>();

    static {
        var skillA = new Skill();
        skillA.setTitle("SQL");
        var skillB = new Skill();
        skillB.setTitle("Java");
        var skillC = new Skill();
        skillC.setTitle("Machine learning");

        var careerForum = new Event();
        careerForum.setTitle("Career forum");
        LocalDateTime startDateA = LocalDateTime.of(2024, 6, 12, 12, 0);
        LocalDateTime endDateA = LocalDateTime.of(2024, 6, 12, 18, 0);
        careerForum.setStartDate(startDateA);
        careerForum.setEndDate(endDateA);
        careerForum.setDescription("Here you will find opportunities for your career future!");
        careerForum.setRelatedSkills(List.of(skillA, skillB));
        careerForum.setLocation("Moscow, Career Expo");
        careerForum.setType(EventType.MEETING);
        careerForum.setStatus(EventStatus.PLANNED);

        var projectPresentation = new Event();
        projectPresentation.setTitle("Project presentation");
        LocalDateTime startDateB = LocalDateTime.of(2024, 5, 10, 12, 0);
        LocalDateTime endDateB = LocalDateTime.of(2024, 5, 10, 14, 0);
        projectPresentation.setStartDate(startDateB);
        projectPresentation.setEndDate(endDateB);
        projectPresentation.setDescription("Presentation of a new project based on AI technology.");
        projectPresentation.setRelatedSkills(List.of(skillC, skillA));
        projectPresentation.setLocation("Moscow, AI studio");
        projectPresentation.setType(EventType.PRESENTATION);
        projectPresentation.setStatus(EventStatus.IN_PROGRESS);

        ALL_EVENTS.addAll(List.of(careerForum, projectPresentation));
    }
}
