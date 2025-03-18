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
import school.faang.user_service.dto.promotion.user.UserPromotionRequestDto;
import school.faang.user_service.service.UserPromotionService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotion/users")
public class UserPromotionController {
    private final UserPromotionService userPromotionService;

    @PostMapping("/start")
    public ResponseEntity<String> startUserPromotion(
            @RequestBody UserPromotionRequestDto userPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to start user promotion. userId: {}, userPromotionRequestDto: {}",
                userPromotionRequestDto.userDto().userId(), userPromotionRequestDto.userPromotionDto());

        ResponseEntity<String> response = userPromotionService.processStartUserPromotion(
                userPromotionRequestDto.userDto(), userPromotionRequestDto.userPromotionDto(), currencyDto);

        log.info("User promotion started successfully. userId: {}", userPromotionRequestDto.userDto().userId());
        return response;
    }

    @DeleteMapping("/end")
    public ResponseEntity<String> endUserPromotion(
            @RequestBody UserPromotionRequestDto userPromotionRequestDto) {

        log.info("Received request to end user promotion. userId: {}, userPromotionRequestDto: {}",
                userPromotionRequestDto.userDto().userId(), userPromotionRequestDto.userPromotionDto());

        ResponseEntity<String> response = userPromotionService.processEndUserPromotion(
                userPromotionRequestDto.userDto(), userPromotionRequestDto.userPromotionDto());

        log.info("User promotion ended successfully. userId: {}", userPromotionRequestDto.userDto().userId());
        return response;
    }

    @PutMapping("/update/priority")
    public ResponseEntity<String> updateUserPromotionPriority(
            @RequestBody UserPromotionRequestDto userPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to update user promotion priority. userId: {}, userPromotionRequestDto: {}",
                userPromotionRequestDto.userDto().userId(), userPromotionRequestDto.userPromotionDto());

        ResponseEntity<String> response = userPromotionService.processUpdateUserPromotionPriority(
                userPromotionRequestDto.userDto(), userPromotionRequestDto.userPromotionDto(), currencyDto);

        log.info("Successfully updated user promotion priority for userId: {}. New promotion priority: {}",
                userPromotionRequestDto.userDto().userId(), userPromotionRequestDto.userPromotionDto().promotionPriority());
        return response;
    }

    @PutMapping("/update/type")
    public ResponseEntity<String> updateUserPromotionType(
            @RequestBody UserPromotionRequestDto userPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to update user promotion type. userId: {}, userPromotionRequestDto: {}",
                userPromotionRequestDto.userDto().userId(), userPromotionRequestDto.userPromotionDto());

        ResponseEntity<String> response = userPromotionService.processUpdateUserPromotionType(
                userPromotionRequestDto.userDto(), userPromotionRequestDto.userPromotionDto(), currencyDto);

        log.info("Successfully updated user promotion type for userId: {}. New promotion type: {}",
                userPromotionRequestDto.userDto().userId(), userPromotionRequestDto.userPromotionDto().userPromotionType());
        return response;
    }
}