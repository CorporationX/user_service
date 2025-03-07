package school.faang.user_service.mapper.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.PremiumActivated;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.PremiumMapper;

import java.time.format.DateTimeFormatter;

@Component
public class PremiumMapperImpl implements PremiumMapper {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PremiumActivated premiumToPremiumActivated(Premium premium) {
        return new PremiumActivated(
                formatter.format(premium.getStartDate()),
                formatter.format(premium.getEndDate())
        );
    }
}
