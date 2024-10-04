package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationDto {
    private String faculty;
    private int yearOfStudy;
    private String major;
}