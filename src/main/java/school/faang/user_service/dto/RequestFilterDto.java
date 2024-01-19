package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
    private String status;
}
