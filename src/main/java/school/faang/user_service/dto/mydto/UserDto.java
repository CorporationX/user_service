package school.faang.user_service.dto.mydto;

import lombok.Builder;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

@Builder
public record UserDto(Long id,
                      String username,
                      String email,
                      String phone,
                      PreferredContact preference,
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
                      List<SkillDto> skills) {
}
