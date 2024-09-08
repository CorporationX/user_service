package school.faang.user_service.util;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;

public final class TestDataFactory {

    public static User createUser() {
        return User.builder()
                .aboutMe("I love football")
                .city("Moscow")
                .contacts(List.of(Contact.builder()
                        .contact("example")
                        .build(), Contact.builder()
                        .contact("Email")
                        .build()))
                .country(Country.builder()
                        .title("Russia")
                        .build())
                .email("@gmail.com")
                .experience(10)
                .username("Petr")
                .phone("911")
                .skills(List.of(Skill.builder()
                        .title("Java")
                        .build(), Skill.builder()
                                .title("Driver")
                        .build()))
                .build();
    }

    public static int createPage () {
        return 2;
    }

    public static int createPageSize () {
        return 3;
    }

    public static UserFilterDto createFilterDto() {
        return new UserFilterDto();
    }
}
