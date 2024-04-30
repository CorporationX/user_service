package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.SkillService;

@Component
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

}
