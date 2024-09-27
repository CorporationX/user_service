package school.faang.user_service.mapper.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.promotion.PromotionFabric.getUser;
import static school.faang.user_service.util.promotion.PromotionFabric.getUserPromotion;

class ResponseUserMapperTest {
    private static final long USER_ID = 1;
    private static final String USERNAME = "username";
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;

    private final UserMapper responseUserMapper = Mappers.getMapper(UserMapper.class);

//    @Test
//    @DisplayName("Test conver event to response event")
//    void testToDto() {
//        UserPromotion userPromotion = getUserPromotion(TARIFF, TARIFF.getNumberOfViews());
//        User user = getUser(USER_ID, USERNAME, userPromotion);
//        var responseDto = new UserResponseDto(USER_ID, USERNAME, TARIFF.toString(), TARIFF.getNumberOfViews());
//
//        assertThat(responseUserMapper.toUserResponseDto(user)).isEqualTo(responseDto);
//    }
}