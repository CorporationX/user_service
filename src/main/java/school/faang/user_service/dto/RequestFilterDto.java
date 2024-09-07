package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.requestformentoring.helper.filters.FilterRequest;

import java.util.List;

@Data
public class RequestFilterDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
