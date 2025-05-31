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
import school.faang.user_service.entity.promotion.Product;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.enums.Plan;
import school.faang.user_service.exception.ConflictPlanException;
import school.faang.user_service.mapper.promotion.PromotionMapperImpl;
import school.faang.user_service.repository.event.EventRepositoryAdapter;
import school.faang.user_service.repository.promotion.EventPromotionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPromotionActionsServiceTest {
    @Mock
    private EventRepositoryAdapter eventRepoAdapter;
    @Spy
    private PromotionMapperImpl promotionMapper;
    @Mock
    private EventPromotionRepository eventPromotionRepo;
    @InjectMocks
    private EventPromotionActionsService eventPromotionCreator;

    private PromotionDto promotionDto;

    @BeforeEach
    void setUp() {
        promotionDto = PromotionDto.builder()
                .promotionType(PromotionType.EVENT)
                .clientId(1L)
                .eventId(1L)
                .plan(Plan.VIP)
                .build();
    }

    @Test
    void createTest_shouldCreatePromotion() {
        User user = UserTestData.defaultUser().build();
        PromotionPlan plan = PromotionPlanTestData.defaultPlan().build();
        Event event = EventTestData.defaultEvent().build();
        event.setOwner(user);
        Product expectedProduct = new Product();

        when(eventPromotionRepo.existsByEventIdAndActiveTrue(anyLong())).thenReturn(false);
        when(eventRepoAdapter.findById(anyLong())).thenReturn(event);

        Product product = eventPromotionCreator.create(promotionDto, user, plan);

        assertEquals(expectedProduct, product);
        verify(eventRepoAdapter, times(1)).findById(anyLong());
    }

    @Test
    void createTest_whenEventIsPromoted_thenThrowConflictPlanException() {
        User user = UserTestData.defaultUser().build();
        PromotionPlan plan = PromotionPlanTestData.defaultPlan().build();
        when(eventPromotionRepo.existsByEventIdAndActiveTrue(anyLong())).thenReturn(true);

        assertThrows(ConflictPlanException.class, () ->
                eventPromotionCreator.create(promotionDto, user, plan));
    }

    @Test
    void getTypeTest_shouldReturnPromotionType() {
        PromotionType type = eventPromotionCreator.getType();

        assertEquals(PromotionType.EVENT, type);
    }
}