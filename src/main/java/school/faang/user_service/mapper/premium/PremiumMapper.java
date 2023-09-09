package school.faang.user_service.mapper.premium;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.integration.PaymentResponse;

@Mapper(componentModel = "spring")
public interface PremiumMapper {

    @Mapping(target = "userId", source = "user.id")
    PremiumDto toDto(Premium premium);
    PremiumRequestDto toDto(PaymentResponse paymentResponse);
}