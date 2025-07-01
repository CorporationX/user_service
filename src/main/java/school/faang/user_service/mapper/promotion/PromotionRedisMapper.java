package school.faang.user_service.mapper.promotion;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.model.promotion.PromotionRedisModel;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface PromotionRedisMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "tariff.id", target = "tariffId")
    PromotionRedisModel toEventPromotionRedis(Promotion promotion);
}
