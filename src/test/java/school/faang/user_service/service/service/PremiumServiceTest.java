package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.PaymentRequest;
import school.faang.user_service.entity.premium.PaymentResponse;
import school.faang.user_service.entity.premium.PaymentStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.PremiumService;
import school.faang.user_service.validator.PremiumValidator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumValidator premiumValidator;

    @Spy
    private PremiumMapper mapper;


    @InjectMocks
    private PremiumService premiumService;

    @BeforeEach
    public void init() {

    }

    @Test
    public void testCreatePremiumSuccess() {
        User user = new User();
        user.setId(1L);
        PremiumDto premiumDto = new PremiumDto(1L, 2L, null, null);
        assertEquals(premiumDto, premiumService.createPremium(user, PremiumPeriod.ONE_MONTH));
    }
}
