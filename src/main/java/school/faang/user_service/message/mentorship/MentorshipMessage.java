package school.faang.user_service.message.mentorship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MentorshipMessage {
    INVALID_ID("ID \"{}\" is less than 1"),
    EQUALS_IDS("Mentee ID \"{}\" and mentor ID \"{}\" are equal"),
    GET_MENTEES_START("Get mentees for the user \"{}\" has been started"),
    GET_MENTEES_FINISH("Get mentees for the user \"{}\" has been finished"),
    GET_MENTORS_START("Get mentors for the user \"{}\" has been started"),
    GET_MENTORS_FINISH("Get mentors for the user \"{}\" has been finished"),
    DELETE_MENTEE("User \"{}\" is no longer a mentee of user \"{}\""),
    DELETE_MENTOR("User \"{}\" is no longer a mentor of user \"{}\""),
    NO_MENTEE("User \"{}\" does not have a mentee \"{}\""),
    NO_MENTOR("User \"{}\" does not have a mentor \"{}\"");

    private final String message;
}
