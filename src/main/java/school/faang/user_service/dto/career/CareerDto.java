package school.faang.user_service.dto.career;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class CareerDto {

    private Long id;

    private LocalDate from;

    private LocalDate to;

    private String company;

    private String position;
}
