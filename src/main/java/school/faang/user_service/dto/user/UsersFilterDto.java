package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class UsersFilterDto {
    private Boolean active;
    private LocalDateTime createdBefore;
    private LocalDateTime createdAfter;
    private Integer page;
    private Integer size;
    private UsersSortOption sort;
}
