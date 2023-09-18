package school.faang.user_service.dto.user;

import lombok.Builder;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

@Builder
public record UserDto(Long id,
                      String username,
                      String email,
                      String phone,
                      String aboutMe,
                      boolean active,
                      String city,
                      Integer experience,
                      List<Long> followers,
                      List<Long> followees,
                      List<Long> mentors,
                      List<Long> mentees,
                      CountryDto country,
                      List<GoalDto> goals,
                      List<SkillDto> skills,
                      PreferredContact preference) {
    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }
}
