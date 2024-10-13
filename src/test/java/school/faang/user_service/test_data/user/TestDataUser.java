package school.faang.user_service.test_data.user;

import lombok.Getter;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestDataUser {
    private Country getCountry() {
        return Country.builder()
                .id(1L)
                .title("Country")
                .build();
    }

    public Skill getSkill1() {
        return Skill.builder()
                .id(1L)
                .title("skill1")
                .build();
    }

    public Skill getSkill2() {
        return Skill.builder()
                .id(2L)
                .title("skill2")
                .build();
    }

    public User getUser() {
        return User.builder()
                .id(1L)
                .username("User1")
                .email("user1@mail.com")
                .password("123456789")
                .active(true)
                .country(getCountry())
                .skills(new ArrayList<>(List.of(getSkill1(), getSkill2())))
                .banned(false)
                .build();
    }
}
