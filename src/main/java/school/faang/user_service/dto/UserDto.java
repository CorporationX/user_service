package school.faang.user_service.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String country;
    private String city;
    private Integer experience;
    private List<Long> followerIds;
    private List<Long> followeeIds;
    private List<Long> menteeIds;
    private List<Long> mentorIds;
    private List<Long> goalIds;
    private List<Long> skillIds;
    private List<Long> recommendationGivenIds;
    private List<Long> recommendationReceivedIds;
}
