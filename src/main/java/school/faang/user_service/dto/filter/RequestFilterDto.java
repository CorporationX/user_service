package school.faang.user_service.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterDto {
    private String descriptionPattern;
    private RequestStatus requestStatusPattern;
    private String requesterNamePattern;
    private String receiverNamePattern;
}
