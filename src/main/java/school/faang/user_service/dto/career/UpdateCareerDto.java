package school.faang.user_service.dto.career;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class UpdateCareerDto {
    private final LocalDate from;
    private final LocalDate to;
    private final String company;
    private final String position;

}
