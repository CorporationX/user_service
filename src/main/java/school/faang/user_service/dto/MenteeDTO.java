
package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenteeDTO {
    private Long id;
    private String name;
    private List<Long> mentorsIds;
}