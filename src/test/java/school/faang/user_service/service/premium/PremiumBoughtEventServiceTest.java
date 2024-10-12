package school.faang.user_service.service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.redis.premium.RedisPremiumBoughtEventPublisher;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.util.premium.PremiumFabric.buildPremiumBoughtEvent;

@ExtendWith(MockitoExtension.class)
class PremiumBoughtEventServiceTest {
    private static final long USER_ID = 1L;
    private static final double COST = 10.0;
    private static final int DAYS = 31;
    private static final int NUMBER_OF_EVENTS = 3;

    @Mock
    private RedisPremiumBoughtEventPublisher redisPremiumBoughtEventPublisher;

    @InjectMocks
    private PremiumBoughtEventService premiumBoughtEventService;

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Publish message premium bought successful")
    void testAddToPublishSuccessful() {
        PremiumBoughtEventDto dto = new PremiumBoughtEventDto(USER_ID, COST, DAYS);
        premiumBoughtEventService.addToPublish(dto);
        List<PremiumBoughtEventDto> profileViewEventDtos = (List<PremiumBoughtEventDto>)
                ReflectionTestUtils.getField(premiumBoughtEventService, "premiumBoughtEventDtos");

        assertThat(profileViewEventDtos).isNotNull();
        PremiumBoughtEventDto resultDto = profileViewEventDtos.get(0);
        assertThat(resultDto).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    @DisplayName("Premium bought event dtos is empty check successful")
    void testPremiumBoughtEventDtosIsEmptySuccessful() {
        assertThat(premiumBoughtEventService.premiumBoughtEventDtosIsEmpty()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given an exception when publish list then catch and return copyList into main list")
    void testPublishAllPremiumBoughtEventsException() {
        List<PremiumBoughtEventDto> eventDtos = new ArrayList<>(buildPremiumBoughtEvent(NUMBER_OF_EVENTS));
        ReflectionTestUtils.setField(premiumBoughtEventService, "premiumBoughtEventDtos", eventDtos);
        doThrow(new RedisConnectionFailureException(""))
                .when(redisPremiumBoughtEventPublisher).publish(anyList());

        premiumBoughtEventService.publishAllPremiumBoughtEvents();
        List<PremiumBoughtEventDto> profileViewEventDtos = (List<PremiumBoughtEventDto>)
                ReflectionTestUtils.getField(premiumBoughtEventService, "premiumBoughtEventDtos");

        assertThat(profileViewEventDtos).isNotEmpty();
        assertThat(profileViewEventDtos.size()).isEqualTo(NUMBER_OF_EVENTS);
        verify(redisPremiumBoughtEventPublisher).publish(anyList());
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Save all premium bought events events successful")
    void testPublishAllPremiumBoughtEventsSuccessful() {
        List<PremiumBoughtEventDto> eventDtos = new ArrayList<>(buildPremiumBoughtEvent(NUMBER_OF_EVENTS));
        ReflectionTestUtils.setField(premiumBoughtEventService, "premiumBoughtEventDtos", eventDtos);

        premiumBoughtEventService.publishAllPremiumBoughtEvents();
        List<PremiumBoughtEventDto> profileViewEventDtos = (List<PremiumBoughtEventDto>)
                ReflectionTestUtils.getField(premiumBoughtEventService, "premiumBoughtEventDtos");

        assertThat(profileViewEventDtos).isEmpty();
        verify(redisPremiumBoughtEventPublisher).publish(anyList());
    }
}