package school.faang.user_service.dto.entity;

import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDto extends EntityDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private Boolean active;
    private String aboutMe;
    private Long countryId;
    private String city;
    private Integer experience;
    private List<Long> followersIds;
    private List<Long> followeesIds;
    private List<Long> ownedEventsIds;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
    private List<Long> skillsIds;

}
