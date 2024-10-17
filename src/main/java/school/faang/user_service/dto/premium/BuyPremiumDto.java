package school.faang.user_service.dto.premium;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyPremiumDto {

    @NonNull
    private Integer days;
}
