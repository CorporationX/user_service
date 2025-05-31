package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Product;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.enums.Plan;
import school.faang.user_service.entity.promotion.enums.ViewWidth;
import school.faang.user_service.entity.promotion.user.ProfilePromotion;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.promotion.ProductRepository;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;
import school.faang.user_service.repository.user.UserRepositoryAdapter;
import school.faang.user_service.service.promotion.interfaces.PromotionActionsService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionPlanRepository promotionPlanRepo;
    @Mock
    private ProductRepository productRepo;
    @Mock
    private UserRepositoryAdapter userRepoAdapter;
    @Mock
    private Map<String, PromotionActionsService> promotionCreators;
    @Mock
    private PromotionActionsService promotionActionsService;
    @InjectMocks
    private PromotionServiceImpl promotionService;
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Captor
    private ArgumentCaptor<PromotionDto> promotionDtoCaptor;
    @Captor
    private ArgumentCaptor<PromotionPlan> promotionPlanCaptor;
    @Captor
    private ArgumentCaptor<Product> productArgumentCaptor;

    private PromotionPlan promotionPlan;
    private User user;
    private PromotionDto promotionDto;

    @BeforeEach
    void setUp() {
        promotionDto = PromotionDto.builder()
                .promotionType(PromotionType.PROFILE)
                .plan(Plan.VIP)
                .clientId(1L)
                .build();

        promotionPlan = PromotionPlan.builder()
                .id(1)
                .price(BigDecimal.valueOf(100L))
                .numPromotedViews(5)
                .viewWidth(ViewWidth.PUBLIC)
                .currency(Currency.getInstance("EUR"))
                .build();

        user = User.builder()
                .id(1L)
                .username("username")
                .goals(new ArrayList<>())
                .receivedGoalInvitations(new ArrayList<>())
                .build();
    }

    @Test
    void addPromotionTest_shouldAddPromotion() {
        ProfilePromotion product = new ProfilePromotion();
        product.setId(1L);
        product.setClient(user);
        product.setProfile(user);
        product.setCurrency(promotionDto.getCurrency());
        product.setNumPromotedViews(promotionPlan.getNumPromotedViews());
        product.setPrice(promotionPlan.getPrice());
        product.setViewWidth(promotionPlan.getViewWidth());

        when(promotionPlanRepo.findByPlan(any())).thenReturn(Optional.of(promotionPlan));
        when(userRepoAdapter.findById(anyLong())).thenReturn(user);
        when(promotionCreators.get(any())).thenReturn(promotionActionsService);
        when(promotionActionsService.create(any(), any(), any())).thenReturn(product);
        when(productRepo.save(any())).thenReturn(product);

        promotionService.addPromotion(promotionDto);
        verify(promotionActionsService).create(promotionDtoCaptor.capture(), userCaptor.capture(), promotionPlanCaptor.capture());
        verify(productRepo).save(productArgumentCaptor.capture());
        PromotionDto promotionDtoValue = promotionDtoCaptor.getValue();
        User userValue = userCaptor.getValue();
        PromotionPlan promotionPlanValue = promotionPlanCaptor.getValue();
        Product productValue = productArgumentCaptor.getValue();

        assertEquals(promotionDtoValue, promotionDto);
        assertEquals(userValue, user);
        assertEquals(promotionPlanValue, promotionPlan);
        assertEquals(productValue, product);
    }

    @Test
    void addPromotionTest_whenNoUserFound_thenThrowIllegalArgumentException() {
        when(promotionPlanRepo.findByPlan(any())).thenReturn(Optional.of(promotionPlan));
        when(promotionCreators.get(any())).thenReturn(promotionActionsService);
        when(userRepoAdapter.findById(anyLong())).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> promotionService.addPromotion(promotionDto));
    }

    @Test
    void addPromotionTest_whenNoPlanDetailsFound_thenThrowNotFoundException() {
        when(promotionCreators.get(any())).thenReturn(promotionActionsService);
        when(promotionPlanRepo.findByPlan(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> promotionService.addPromotion(promotionDto));
    }
}