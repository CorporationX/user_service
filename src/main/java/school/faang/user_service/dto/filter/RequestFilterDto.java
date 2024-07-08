package school.faang.user_service.dto.filter;

import lombok.Data;

@Data
public class RequestFilterDto {
    private String descriptionPattern;
    private Long requesterPattern;
    private Long receiverPattern;
    private String requestStatusPattern;
}
