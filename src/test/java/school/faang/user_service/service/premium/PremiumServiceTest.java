package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PremiumServiceTest {

    private PremiumRepository premiumRepository;
    private PremiumService premiumService;

    @BeforeEach
    void setUp() {
        premiumRepository = mock(PremiumRepository.class);
        premiumService = new PremiumService(premiumRepository, 2); // batch size = 2
    }

    @Test
    void shouldDeleteExpiredPremiumsInBatches() {
        List<Premium> expiredPremiums = generatePremiumList(5);
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(expiredPremiums);

        premiumService.removeExpiredPremiums();

        ArgumentCaptor<List<Premium>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(premiumRepository, times(3)).deleteAll(captor.capture());

        List<List<Premium>> allBatches = captor.getAllValues();

        List<Integer> sizes = allBatches.stream().map(List::size).sorted().toList();
        assertEquals(List.of(1, 2, 2), sizes);
    }

    @Test
    void shouldDoNothingIfNoExpiredPremiums() {
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        premiumService.removeExpiredPremiums();

        verify(premiumRepository, never()).deleteAll(any());
    }

    private List<Premium> generatePremiumList(int count) {
        List<Premium> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Premium premium = Premium.builder()
                    .id(i)
                    .user(User.builder().id((long) i).build())
                    .startDate(LocalDateTime.now().minusDays(10))
                    .endDate(LocalDateTime.now().minusDays(1))
                    .build();
            list.add(premium);
        }
        return list;
    }
}
