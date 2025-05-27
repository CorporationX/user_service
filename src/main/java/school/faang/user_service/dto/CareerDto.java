package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Data
public class CareerDto {
    private Long id;
    private LocalDate from;
    private LocalDate to;
    private String company;
    private String position;
}
