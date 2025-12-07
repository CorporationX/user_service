package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import school.faang.user_service.service.premium.PremiumCacheService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumCacheServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private PremiumCacheService premiumCacheService;

    @BeforeEach
    void setUp() {
        // Моки настраиваются индивидуально в каждом тесте
    }

    @Test
    void isActive_WhenKeyExists_ShouldReturnTrue() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        when(valueOperations.get("premium:active:1")).thenReturn("1");
        
        boolean result = premiumCacheService.isActive(userId);

        
        assertThat(result).isTrue();
        verify(valueOperations).get("premium:active:1");
    }

    @Test
    void isActive_WhenKeyDoesNotExist_ShouldReturnFalse() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        when(valueOperations.get("premium:active:1")).thenReturn(null);

        
        boolean result = premiumCacheService.isActive(userId);

        
        assertThat(result).isFalse();
        verify(valueOperations).get("premium:active:1");
    }

    @Test
    void isActive_WhenKeyIsEmpty_ShouldReturnFalse() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        when(valueOperations.get("premium:active:1")).thenReturn(null);

        
        boolean result = premiumCacheService.isActive(userId);

        
        assertThat(result).isFalse();
    }

    @Test
    void setActiveUntil_WhenEndDateIsNull_ShouldNotSetCache() {
        
        long userId = 1L;
        LocalDateTime nullEndDate = null;

        
        premiumCacheService.setActiveUntil(userId, nullEndDate);

        
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void setActiveUntil_WhenEndDateIsInFuture_ShouldSetCacheWithCorrectTTL() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime futureDate = LocalDateTime.now(ZoneOffset.UTC).plusDays(30);

        
        premiumCacheService.setActiveUntil(userId, futureDate);

        
        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WhenEndDateIsInPast_ShouldEvictCache() {
        
        long userId = 1L;
        LocalDateTime pastDate = LocalDateTime.now(ZoneOffset.UTC).minusDays(1);

        
        premiumCacheService.setActiveUntil(userId, pastDate);

        
        verify(stringRedisTemplate).delete("premium:active:1");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void setActiveUntil_WhenEndDateIsNow_ShouldEvictCache() {
        
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        
        premiumCacheService.setActiveUntil(userId, now);

        
        verify(stringRedisTemplate).delete("premium:active:1");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void setActiveUntil_WhenEndDateIsOneSecondInFuture_ShouldSetCacheWithOneSecondTTL() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime oneSecondFuture = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1);

        
        premiumCacheService.setActiveUntil(userId, oneSecondFuture);

        
        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WhenEndDateIsOneMillisecondInFuture_ShouldSetCacheWithOneMillisecondTTL() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime oneMillisecondFuture = LocalDateTime.now(ZoneOffset.UTC).plusNanos(1_000_000);

        
        premiumCacheService.setActiveUntil(userId, oneMillisecondFuture);

        
        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void evict_ShouldDeleteKey() {
        
        long userId = 1L;

        
        premiumCacheService.evict(userId);

        
        verify(stringRedisTemplate).delete("premium:active:1");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 999L, 123456789L, Long.MAX_VALUE})
    void isActive_WithDifferentUserIds_ShouldUseCorrectKey(long userId) {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("premium:active:" + userId)).thenReturn("1");

        
        boolean result = premiumCacheService.isActive(userId);

        
        assertThat(result).isTrue();
        verify(valueOperations).get("premium:active:" + userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 999L, 123456789L, Long.MAX_VALUE})
    void evict_WithDifferentUserIds_ShouldUseCorrectKey(long userId) {

        premiumCacheService.evict(userId);

        
        verify(stringRedisTemplate).delete("premium:active:" + userId);
    }

    @Test
    void setActiveUntil_WithLeapYearDate_ShouldCalculateCorrectTTL() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime leapYearDate = LocalDateTime.of(2028, 2, 29, 12, 0, 0);
        LocalDateTime futureDate = leapYearDate.plusDays(30);

        
        premiumCacheService.setActiveUntil(userId, futureDate);

        
        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithYearBoundary_ShouldCalculateCorrectTTL() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime yearEnd = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
        LocalDateTime futureDate = yearEnd.plusDays(30);

        
        premiumCacheService.setActiveUntil(userId, futureDate);

        
        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithDSTTransition_ShouldCalculateCorrectTTL() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime dstDate = LocalDateTime.of(2026, 3, 10, 2, 30, 0);
        LocalDateTime futureDate = dstDate.plusDays(30);

        
        premiumCacheService.setActiveUntil(userId, futureDate);

        
        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithVeryLongTTL_ShouldSetCache() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime veryFutureDate = LocalDateTime.now(ZoneOffset.UTC).plusYears(10);

        
        premiumCacheService.setActiveUntil(userId, veryFutureDate);

        
        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithVeryShortTTL_ShouldSetCache() {
        
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime veryNearFuture = LocalDateTime.now(ZoneOffset.UTC).plusNanos(1_000_000);

        
        premiumCacheService.setActiveUntil(userId, veryNearFuture);

        
        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithExactCurrentTime_ShouldEvictCache() {
        
        long userId = 1L;
        LocalDateTime exactNow = LocalDateTime.now(ZoneOffset.UTC);

        
        premiumCacheService.setActiveUntil(userId, exactNow);

        
        verify(stringRedisTemplate).delete("premium:active:1");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithNegativeTTL_ShouldEvictCache() {
        
        long userId = 1L;
        LocalDateTime pastDate = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(1);

        
        premiumCacheService.setActiveUntil(userId, pastDate);

        
        verify(stringRedisTemplate).delete("premium:active:1");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }
}
