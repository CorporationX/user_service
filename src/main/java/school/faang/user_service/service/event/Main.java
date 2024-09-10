package school.faang.user_service.service.event;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import school.faang.user_service.UserServiceApplication;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
            ConfigurableApplicationContext context = SpringApplication.run(UserServiceApplication.class, args);
            EventController controller = context.getBean(EventController.class);

           List<EventDto> eventDtos =  controller.getOwnedEvents(3L);

            context.close();
    }
}
