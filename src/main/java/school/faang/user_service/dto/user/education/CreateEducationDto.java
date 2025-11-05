package school.faang.user_service.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CreateEducationDto {
    private final Integer yearFrom;
    private final Integer yearTo;
    private final String institution;
    private final String educationLevel;
    private final String specialization;
}
