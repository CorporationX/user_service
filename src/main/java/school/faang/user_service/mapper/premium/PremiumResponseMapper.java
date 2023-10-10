package school.faang.user_service.mapper.premium;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.integration.PaymentResponse;

@Mapper(componentModel = "spring")
public interface PremiumResponseMapper {

    PremiumDto toDto(Premium premium);
    PremiumResponseDto toDto(PaymentResponse paymentResponse);

}
