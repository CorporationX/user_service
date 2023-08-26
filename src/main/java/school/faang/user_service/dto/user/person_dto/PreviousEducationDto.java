package school.faang.user_service.dto.user.person_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PreviousEducationDto {
    private String degree;
    private String institution;
    private Integer completionYear;
}
