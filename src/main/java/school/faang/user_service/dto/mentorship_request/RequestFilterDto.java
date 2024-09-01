package school.faang.user_service.dto.mentorship_request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.filter.FilterDto;
import school.faang.user_service.entity.RequestStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto extends FilterDto {
    private String descriptionPattern;

    private Long requesterId;

    private Long receiverId;

    private RequestStatus status;
}
