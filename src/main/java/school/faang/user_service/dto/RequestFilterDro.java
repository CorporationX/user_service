package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterDro {
    private String descriptionPattern;
    private String requesterPattern;
    private String receiverPattern;
    private String statusPattern;
    private Integer requesterIdPattern;
    private Integer receiverIdPattern;
}
