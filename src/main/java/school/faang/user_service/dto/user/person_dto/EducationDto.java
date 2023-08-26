package school.faang.user_service.dto.user.person_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EducationDto {
    private String faculty;
    private Integer yearOfStudy;
    private String major;
    private Double gpa;
}
