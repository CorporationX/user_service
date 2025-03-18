package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.promotion.event.EventPromotionRequestDto;
import school.faang.user_service.service.EventPromotionService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotion/events")
public class EventPromotionController {
    private final EventPromotionService eventPromotionService;

    @PostMapping("/start")
    public ResponseEntity<String> startEventPromotion(@RequestBody EventPromotionRequestDto eventPromotionRequestDto,
                                                      @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to start user promotion. userId: {}, eventPromotionRequestDto: {}",
                eventPromotionRequestDto.eventDto().eventId(), eventPromotionRequestDto.eventPromotionDto());
        ResponseEntity<String> response = eventPromotionService.processStartEventPromotion(
                eventPromotionRequestDto.eventDto(), eventPromotionRequestDto.eventPromotionDto(), currencyDto);
        log.info("Event promotion started successfully. userID: {}", eventPromotionRequestDto.eventDto().eventId());
        return response;
    }

    @DeleteMapping("/end")
    public ResponseEntity<String> endEventPromotion(
            @RequestBody EventPromotionRequestDto eventPromotionRequestDto) {

        log.info("Received request to end event promotion. userId: {}, eventPromotionRequestDto: {}",
                eventPromotionRequestDto.eventDto().eventId(), eventPromotionRequestDto);
        ResponseEntity<String> response = eventPromotionService.processEndEventPromotion(
                eventPromotionRequestDto.eventDto(), eventPromotionRequestDto.eventPromotionDto());
        log.info("Event promotion ended successfully. eventID: {}", eventPromotionRequestDto.eventDto().eventId());
        return response;
    }

    @PutMapping("/update/priority")
    public ResponseEntity<String> updateEventPromotionPriority(
            @RequestBody EventPromotionRequestDto eventPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to update event promotion priority. eventId: {}, eventPromotionRequestDto: {}",
                eventPromotionRequestDto.eventDto().eventId(), eventPromotionRequestDto);
        ResponseEntity<String> response = eventPromotionService.processUpdateEventPromotionPriority(
                eventPromotionRequestDto.eventDto(), eventPromotionRequestDto.eventPromotionDto(), currencyDto);
        log.info("Successfully updated event promotion priority for userId: {}. New promotion priority: {}",
                eventPromotionRequestDto.eventDto().eventId(),
                eventPromotionRequestDto.eventPromotionDto().promotionPriority());
        return response;
    }

    @PutMapping("/update/type")
    public ResponseEntity<String> updateEventPromotionType(
            @RequestBody EventPromotionRequestDto eventPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to update event promotion type. eventId: {}, eventPromotionRequestDto: {}",
                eventPromotionRequestDto.eventDto().eventId(), eventPromotionRequestDto);
        ResponseEntity<String> response = eventPromotionService.processUpdateEventPromotionType(
                eventPromotionRequestDto.eventDto(), eventPromotionRequestDto.eventPromotionDto(), currencyDto);
        log.info("Successfully updated event promotion type for eventId {}. New promotion type: {} ",
                eventPromotionRequestDto.eventDto().eventId(),
                eventPromotionRequestDto.eventPromotionDto().eventPromotionType());
        return response;
    }
}
