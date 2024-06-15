package school.faang.user_service.dto.mentorship;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    private String descriptionPattern;
    private Long requesterIdPattern;
    private Long receiverIdPattern;
    private RequestStatus statusPattern;
}
