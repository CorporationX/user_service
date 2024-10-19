package school.faang.user_service.model.filter_dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserFilterDto {
    private String namePattern;
    private String aboutPattern;
    private String emailPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String phonePattern;
    private String skillPattern;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private Integer experience;
    private int experienceMin;
    private int experienceMax;
    private int page;
    private int pageSize;
}