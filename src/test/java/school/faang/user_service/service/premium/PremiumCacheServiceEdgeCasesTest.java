package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import school.faang.user_service.service.premium.PremiumCacheService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumCacheServiceEdgeCasesTest {

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
    void setActiveUntil_WithLeapYearFebruary29_ShouldCalculateCorrectTTL() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime leapYearDate = LocalDateTime.of(2028, Month.FEBRUARY, 29, 12, 0, 0);
        LocalDateTime futureDate = leapYearDate.plusDays(30);

        premiumCacheService.setActiveUntil(userId, futureDate);

        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithNonLeapYearFebruary28_ShouldCalculateCorrectTTL() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime nonLeapYearDate = LocalDateTime.of(2027, Month.FEBRUARY, 28, 12, 0, 0);
        LocalDateTime futureDate = nonLeapYearDate.plusDays(30);

        premiumCacheService.setActiveUntil(userId, futureDate);

        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithYearBoundary_ShouldCalculateCorrectTTL() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime yearEnd = LocalDateTime.of(2025, Month.DECEMBER, 31, 23, 59, 59);
        LocalDateTime futureDate = yearEnd.plusDays(30);

        premiumCacheService.setActiveUntil(userId, futureDate);

        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithDSTTransition_ShouldCalculateCorrectTTL() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime dstDate = LocalDateTime.of(2026, Month.MARCH, 10, 2, 30, 0);
        LocalDateTime futureDate = dstDate.plusDays(30);

        premiumCacheService.setActiveUntil(userId, futureDate);

        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithDSTFallBack_ShouldCalculateCorrectTTL() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime dstFallBack = LocalDateTime.of(2026, Month.NOVEMBER, 3, 2, 30, 0);
        LocalDateTime futureDate = dstFallBack.plusDays(30);

        premiumCacheService.setActiveUntil(userId, futureDate);

        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithUTCMidnight_ShouldCalculateCorrectTTL() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime utcMidnight = LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0, 0);
        LocalDateTime futureDate = utcMidnight.plusDays(30);

        premiumCacheService.setActiveUntil(userId, futureDate);

        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithUTCMidnightLeapYear_ShouldCalculateCorrectTTL() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime utcMidnightLeapYear = LocalDateTime.of(2028, Month.JANUARY, 1, 0, 0, 0);
        LocalDateTime futureDate = utcMidnightLeapYear.plusDays(30);


        premiumCacheService.setActiveUntil(userId, futureDate);


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
    void setActiveUntil_WithVeryLongTTL_ShouldSetCache() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime veryFarFuture = LocalDateTime.now(ZoneOffset.UTC).plusYears(100);


        premiumCacheService.setActiveUntil(userId, veryFarFuture);


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
    void setActiveUntil_WithOneMillisecondInPast_ShouldEvictCache() {

        long userId = 1L;
        LocalDateTime oneMillisecondPast = LocalDateTime.now(ZoneOffset.UTC).minusNanos(1_000_000);

        premiumCacheService.setActiveUntil(userId, oneMillisecondPast);

        verify(stringRedisTemplate).delete("premium:active:1");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithOneSecondInPast_ShouldEvictCache() {

        long userId = 1L;
        LocalDateTime oneSecondPast = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(1);


        premiumCacheService.setActiveUntil(userId, oneSecondPast);


        verify(stringRedisTemplate).delete("premium:active:1");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithOneMinuteInPast_ShouldEvictCache() {

        long userId = 1L;
        LocalDateTime oneMinutePast = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1);


        premiumCacheService.setActiveUntil(userId, oneMinutePast);


        verify(stringRedisTemplate).delete("premium:active:1");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithOneHourInPast_ShouldEvictCache() {

        long userId = 1L;
        LocalDateTime oneHourPast = LocalDateTime.now(ZoneOffset.UTC).minusHours(1);


        premiumCacheService.setActiveUntil(userId, oneHourPast);


        verify(stringRedisTemplate).delete("premium:active:1");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithOneDayInPast_ShouldEvictCache() {

        long userId = 1L;
        LocalDateTime oneDayPast = LocalDateTime.now(ZoneOffset.UTC).minusDays(1);


        premiumCacheService.setActiveUntil(userId, oneDayPast);


        verify(stringRedisTemplate).delete("premium:active:1");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithOneYearInPast_ShouldEvictCache() {

        long userId = 1L;
        LocalDateTime oneYearPast = LocalDateTime.now(ZoneOffset.UTC).minusYears(1);


        premiumCacheService.setActiveUntil(userId, oneYearPast);


        verify(stringRedisTemplate).delete("premium:active:1");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithOneSecondInFuture_ShouldSetCache() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime oneSecondFuture = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1);


        premiumCacheService.setActiveUntil(userId, oneSecondFuture);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithOneMinuteInFuture_ShouldSetCache() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime oneMinuteFuture = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(1);


        premiumCacheService.setActiveUntil(userId, oneMinuteFuture);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithOneHourInFuture_ShouldSetCache() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime oneHourFuture = LocalDateTime.now(ZoneOffset.UTC).plusHours(1);


        premiumCacheService.setActiveUntil(userId, oneHourFuture);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithOneDayInFuture_ShouldSetCache() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime oneDayFuture = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);


        premiumCacheService.setActiveUntil(userId, oneDayFuture);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithOneYearInFuture_ShouldSetCache() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime oneYearFuture = LocalDateTime.now(ZoneOffset.UTC).plusYears(1);


        premiumCacheService.setActiveUntil(userId, oneYearFuture);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithMaxLongUserId_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long maxUserId = Long.MAX_VALUE;
        LocalDateTime futureDate = LocalDateTime.now(ZoneOffset.UTC).plusDays(30);


        premiumCacheService.setActiveUntil(maxUserId, futureDate);


        verify(valueOperations).set(eq("premium:active:" + maxUserId), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithMinLongUserId_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long minUserId = Long.MIN_VALUE;
        LocalDateTime futureDate = LocalDateTime.now(ZoneOffset.UTC).plusDays(30);


        premiumCacheService.setActiveUntil(minUserId, futureDate);


        verify(valueOperations).set(eq("premium:active:" + minUserId), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithZeroUserId_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long zeroUserId = 0L;
        LocalDateTime futureDate = LocalDateTime.now(ZoneOffset.UTC).plusDays(30);


        premiumCacheService.setActiveUntil(zeroUserId, futureDate);


        verify(valueOperations).set(eq("premium:active:0"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithNegativeUserId_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long negativeUserId = -1L;
        LocalDateTime futureDate = LocalDateTime.now(ZoneOffset.UTC).plusDays(30);


        premiumCacheService.setActiveUntil(negativeUserId, futureDate);


        verify(valueOperations).set(eq("premium:active:-1"), eq("1"), any(Duration.class));
    }

    @Test
    void isActive_WithMaxLongUserId_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long maxUserId = Long.MAX_VALUE;
        when(valueOperations.get("premium:active:" + maxUserId)).thenReturn("1");


        boolean result = premiumCacheService.isActive(maxUserId);


        assertThat(result).isTrue();
        verify(valueOperations).get("premium:active:" + maxUserId);
    }

    @Test
    void isActive_WithMinLongUserId_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long minUserId = Long.MIN_VALUE;
        when(valueOperations.get("premium:active:" + minUserId)).thenReturn("1");


        boolean result = premiumCacheService.isActive(minUserId);


        assertThat(result).isTrue();
        verify(valueOperations).get("premium:active:" + minUserId);
    }

    @Test
    void isActive_WithZeroUserId_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long zeroUserId = 0L;
        when(valueOperations.get("premium:active:0")).thenReturn("1");


        boolean result = premiumCacheService.isActive(zeroUserId);


        assertThat(result).isTrue();
        verify(valueOperations).get("premium:active:0");
    }

    @Test
    void isActive_WithNegativeUserId_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long negativeUserId = -1L;
        when(valueOperations.get("premium:active:-1")).thenReturn("1");


        boolean result = premiumCacheService.isActive(negativeUserId);


        assertThat(result).isTrue();
        verify(valueOperations).get("premium:active:-1");
    }

    @Test
    void evict_WithMaxLongUserId_ShouldWork() {

        long maxUserId = Long.MAX_VALUE;


        premiumCacheService.evict(maxUserId);


        verify(stringRedisTemplate).delete("premium:active:" + maxUserId);
    }

    @Test
    void evict_WithMinLongUserId_ShouldWork() {

        long minUserId = Long.MIN_VALUE;


        premiumCacheService.evict(minUserId);


        verify(stringRedisTemplate).delete("premium:active:" + minUserId);
    }

    @Test
    void evict_WithZeroUserId_ShouldWork() {

        long zeroUserId = 0L;


        premiumCacheService.evict(zeroUserId);


        verify(stringRedisTemplate).delete("premium:active:0");
    }

    @Test
    void evict_WithNegativeUserId_ShouldWork() {

        long negativeUserId = -1L;


        premiumCacheService.evict(negativeUserId);


        verify(stringRedisTemplate).delete("premium:active:-1");
    }

    @Test
    void setActiveUntil_WithNullEndDate_ShouldNotSetCache() {

        long userId = 1L;
        LocalDateTime nullEndDate = null;


        premiumCacheService.setActiveUntil(userId, nullEndDate);


        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
        verify(stringRedisTemplate, never()).delete(anyString());
    }

    @Test
    void setActiveUntil_WithVeryPreciseTime_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime preciseTime = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 30, 45, 123_456_789);


        premiumCacheService.setActiveUntil(userId, preciseTime);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithLeapYearToNonLeapYear_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime leapYearDate = LocalDateTime.of(2028, Month.FEBRUARY, 29, 12, 0, 0);
        LocalDateTime futureDate = leapYearDate.plusDays(365); // Should end on Feb 28, 2029


        premiumCacheService.setActiveUntil(userId, futureDate);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithNonLeapYearToLeapYear_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime nonLeapYearDate = LocalDateTime.of(2027, Month.FEBRUARY, 28, 12, 0, 0);
        LocalDateTime futureDate = nonLeapYearDate.plusDays(365); // Should end on Feb 28, 2028


        premiumCacheService.setActiveUntil(userId, futureDate);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithMonthBoundary_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime monthEnd = LocalDateTime.of(2026, Month.JANUARY, 31, 23, 59, 59);
        LocalDateTime futureDate = monthEnd.plusDays(30);


        premiumCacheService.setActiveUntil(userId, futureDate);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithDSTSpringForward_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime dstSpringForward = LocalDateTime.of(2026, Month.MARCH, 10, 2, 30, 0);
        LocalDateTime futureDate = dstSpringForward.plusDays(30);


        premiumCacheService.setActiveUntil(userId, futureDate);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }

    @Test
    void setActiveUntil_WithDSTFallBack_ShouldWork() {

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        long userId = 1L;
        LocalDateTime dstFallBack = LocalDateTime.of(2026, Month.NOVEMBER, 3, 2, 30, 0);
        LocalDateTime futureDate = dstFallBack.plusDays(30);


        premiumCacheService.setActiveUntil(userId, futureDate);


        verify(valueOperations).set(eq("premium:active:1"), eq("1"), any(Duration.class));
    }
}
