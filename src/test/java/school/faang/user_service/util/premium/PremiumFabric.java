package school.faang.user_service.util.premium;

import school.faang.user_service.entity.premium.Premium;

import java.util.List;
import java.util.stream.LongStream;

public class PremiumFabric {
    public static List<Premium> buildPremiums(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PremiumFabric::buildPremium)
                .toList();
    }

    public static Premium buildPremium(Long id) {
        return Premium
                .builder()
                .id(id)
                .build();
    }
}
