package school.faang.user_service.message.mentorship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Getter
@Component
public enum MentorshipMessage {
    INVALID_ID("ID \"{}\" is less than 1"),
    GET_MENTEES_START("Get mentees for the user \"{}\" has been started"),
    GET_MENTEES_FINISH("Get mentees for the user \"{}\" has been finished"),
    EMPTY_MENTEES("The user \"{}\" has not mentees"),
    GET_MENTORS_START("Get mentors for the user \"{}\" has been started"),
    GET_MENTORS_FINISH("Get mentors for the user \"{}\" has been finished"),
    EMPTY_MENTORS("The user \"{}\" has not mentors");

    private final String message;
}
