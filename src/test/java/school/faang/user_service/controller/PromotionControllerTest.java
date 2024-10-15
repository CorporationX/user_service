package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.PromotionDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.enums.PromotionType;
import school.faang.user_service.service.impl.PromotionServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PromotionControllerTest {
    private User user;
    private PromotionDto promotionDto;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @Mock
    private PromotionServiceImpl promotionService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private PromotionController promotionController;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        promotionDto = PromotionDto.builder()
                .id(1L)
                .promotedUserId(user.getId())
                .priorityLevel(1)
                .remainingShows(10)
                .promotionTarget("profile")
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(promotionController).build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should return PromotionDto with status CREATED when buyPromotion is successful")
    public void testBuyPromotion_Success() throws Exception {
        when(promotionService.buyPromotion(anyLong(), any(PromotionType.class), anyString())).thenReturn(promotionDto);
        when(userContext.getUserId()).thenReturn(user.getId());

        mockMvc.perform(post("/promotion")
                        .param("type", String.valueOf(PromotionType.GENERAL))
                        .param("target", "profile"))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(promotionDto)));

        verify(promotionService, times(1)).buyPromotion(anyLong(), any(PromotionType.class), anyString());
    }
}
