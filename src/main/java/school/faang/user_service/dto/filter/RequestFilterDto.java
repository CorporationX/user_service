package school.faang.user_service.dto.filter;

import lombok.Data;

@Data
public class RequestFilterDto {
    private String descriptionPattern;
    private Long requesterId;
    private Long receiverId;
    private String requestStatusPattern;
}
