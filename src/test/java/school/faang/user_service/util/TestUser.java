package school.faang.user_service.util;

import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;

public class TestUser {
    public final static long FOLLOWER_ID = 1L;
    public final static long FOLLOWEE_ID = 2L;

    static final User USER_1 = User.builder()
            .id(1L)
            .username("John Doe")
            .email("1@m.ru")
            .phone("11111")
            .aboutMe("Me")
            .city("Moscow")
            .contacts(List.of(Contact.builder().contact("Alice").build()))
            .country(Country.builder().title("Russia").build())
            .experience(80)
            .skills(List.of(Skill.builder().title("Move").build()))
            .build();
    static final User USER_2 = User.builder()
            .id(2L)
            .username("John Green")
            .email("2@m.ru")
            .phone("222222")
            .aboutMe("Or")
            .city("Shanghai")
            .contacts(List.of(Contact.builder().contact("John").build()))
            .country(Country.builder().title("China").build())
            .experience(90)
            .skills(List.of(Skill.builder().title("Run").build()))
            .build();
    static final User USER_3 = User.builder()
            .id(3L)
            .username("Axcel Writh")
            .email("3@m.ru")
            .phone("333333")
            .aboutMe("Else")
            .city("Kakapuka")
            .contacts(List.of(Contact.builder().contact("Elvis").build()))
            .country(Country.builder().title("Peru").build())
            .experience(100)
            .skills(List.of(Skill.builder().title("Write Code").build()))
            .build();

    public final static List<User> USER_LIST = List.of(USER_1, USER_2, USER_3);
    public final static List<User> FILTERED_BY_NAME = List.of(USER_1, USER_2);
    public final static List<User> FILTERED_USERS = List.of(USER_1);

    public final static List<User> FILTERED_BY_EXP_MIN = List.of(USER_3);


}
