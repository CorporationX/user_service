package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.builders.EventTestData;
import school.faang.user_service.builders.PromotionPlanTestData;
import school.faang.user_service.builders.UserTestData;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.enums.Plan;
import school.faang.user_service.exception.ConflictPlanException;
import school.faang.user_service.mapper.promotion.PromotionMapperImpl;
import school.faang.user_service.repository.promotion.ProfilePromotionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilePromotionActionsServiceTest {
    @Spy
    private PromotionMapperImpl promotionMapperClass;
    @Mock
    private ProfilePromotionRepository userPromotionRepo;
    @InjectMocks
    private ProfilePromotionActionsService profilePromotionCreator;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    @Captor
    private ArgumentCaptor<PromotionPlan> promotionPlanCaptor;

    private PromotionDto promotionDto;

    @BeforeEach
    void setUp() {
        promotionDto = PromotionDto.builder()
                .promotionType(PromotionType.PROFILE)
                .clientId(1L)
                .plan(Plan.VIP)
                .build();
    }

    @Test
    void createTest_shouldCreatePromotion() {
        User user = UserTestData.defaultUser().build();
        PromotionPlan plan = PromotionPlanTestData.defaultPlan().build();

        when(userPromotionRepo.existsByProfileIdAndActiveTrue(anyLong())).thenReturn(false);

        profilePromotionCreator.create(promotionDto, user, plan);

        verify(userPromotionRepo, times(1)).existsByProfileIdAndActiveTrue(anyLong());
        verify(promotionMapperClass, times(1)).toProfilePromotion(
                promotionPlanCaptor.capture(), userArgumentCaptor.capture());

        PromotionPlan promotionPlanCaptorValue = promotionPlanCaptor.getValue();
        User userCaptorValue = userArgumentCaptor.getValue();
        assertEquals(user, userCaptorValue);
        assertEquals(plan, promotionPlanCaptorValue);
    }

    @Test
    void createTest_whenEventIsPromoted_thenThrowConflictPlanException() {
        User user = UserTestData.defaultUser().build();
        PromotionPlan plan = PromotionPlanTestData.defaultPlan().build();
        when(userPromotionRepo.existsByProfileIdAndActiveTrue(anyLong())).thenReturn(true);

        assertThrows(ConflictPlanException.class, () ->
                profilePromotionCreator.create(promotionDto, user, plan));
    }

    @Test
    void getTypeTest_shouldReturnPromotionType() {
        PromotionType type = profilePromotionCreator.getType();

        assertEquals(PromotionType.PROFILE, type);
    }
}