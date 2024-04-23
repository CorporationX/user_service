
package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MenteeDto {
    private Long id;
    private String name;
    private List<Long> mentorsIds;
    private List<Long> goalIds;
}