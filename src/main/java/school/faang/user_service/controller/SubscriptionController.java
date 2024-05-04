package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/{followeeId}/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getFollowers(
            @PathVariable long followeeId,
            @RequestParam(value = "namePattern") String namePattern,
            @RequestParam(value = "aboutPattern") String aboutPattern,
            @RequestParam(value = "emailPattern") String emailPattern,
            @RequestParam(value = "contactPattern") String contactPattern,
            @RequestParam(value = "countryPattern") String countryPattern,
            @RequestParam(value = "cityPattern") String cityPattern,
            @RequestParam(value = "phonePattern") String phonePattern,
            @RequestParam(value = "skillPattern") String skillPattern,
            @RequestParam(value = "experienceMin", defaultValue = "0") int experienceMin,
            @RequestParam(value = "experienceMax", defaultValue = "100") int experienceMax,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize) {

        UserFilterDto filter = UserFilterDto.builder()
                .namePattern(namePattern)
                .aboutPattern(aboutPattern)
                .emailPattern(emailPattern)
                .contactPattern(contactPattern)
                .countryPattern(countryPattern)
                .cityPattern(cityPattern)
                .phonePattern(phonePattern)
                .skillPattern(skillPattern)
                .experienceMin(experienceMin)
                .experienceMax(experienceMax)
                .page(page)
                .pageSize(pageSize)
                .build();
        return subscriptionService.getFollowers(followeeId, filter);
    }
}
