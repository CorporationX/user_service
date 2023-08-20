package school.faang.user_service.mapper.premium;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.integration.PaymentResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PremiumResponseMapper {

    PremiumResponseDto toDto(PaymentResponse paymentResponse);
}
