package school.faang.user_service.dto.filter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    private String descriptionPattern;
    @NotNull
    private RequestStatus requestStatusPattern;
    @NotBlank
    private String requesterNamePattern;
    @NotBlank
    private String receiverNamePattern;
}
