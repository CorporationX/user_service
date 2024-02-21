package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.entity.UserRating;

import school.faang.user_service.service.UserSearchService;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class UserSearchController {
    private final UserSearchService searchService;

    @GetMapping("/search")
    public Page<UserRating> searchUsersByName(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return searchService.searchUsersByName(username, page, size);
    }
}
