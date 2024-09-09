package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class PremiumRemoverServiceTest {
    private static long USER_ID = 1L;
    private static long PREMIUM_ID = 1L;
    @Mock
    private PremiumRepository premiumRepository;
    @InjectMocks
    private PremiumService premiumService;


    List<Long> expiredPremiumIds = new ArrayList<>();
    List<Premium> expiredPremiums = new ArrayList<>();

    @BeforeEach
    void init() {
        for (long i = 1; i < 40L; i++) {
            expiredPremiumIds.add(i);
            expiredPremiums.add(Premium.builder()
                    .id(i)
                    .user(User.builder().id(i).build())
                    .build());
        }
    }

    @Test
    public void deleteAsyncPremiumByIds() {
        premiumService.deleteAsyncPremiumByIds(expiredPremiumIds);
        verify(premiumRepository, times(1)).deleteAllById(expiredPremiumIds);
    }

    @Test
    public void findExpiredPremiumIds() throws NoSuchFieldException, IllegalAccessException {
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(expiredPremiums);

        Field batchSize = premiumService.getClass().getDeclaredField("batchSize");
        batchSize.setAccessible(true);
        batchSize.set(premiumService, 5);

        List<List<Long>> result = premiumService.findExpiredPremiumIds();

        assertEquals(result.size(), 8);
        assertEquals(4, result.get(result.size()-1).size());
    }

}
