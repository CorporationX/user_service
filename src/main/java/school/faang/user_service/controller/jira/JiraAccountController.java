package school.faang.user_service.controller.jira;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.jira.JiraAccountDto;
import school.faang.user_service.service.jira.JiraAccountService;


@Slf4j
@Validated
@RestController
@RequestMapping("users/account/jira")
@RequiredArgsConstructor
@Tag(name = "Jira Account")
public class JiraAccountController {

    private final JiraAccountService jiraAccountService;
    private final UserContext userContext;

    @Operation(summary = "Add Jira account to user")
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public JiraAccountDto addJiraAccount(@Valid @RequestBody JiraAccountDto jiraAccountDto) {
        long usrId = userContext.getUserId();
        return jiraAccountService.addJiraAccount(usrId, jiraAccountDto);
    }

    @Operation(summary = "Get user's Jira account")
    @GetMapping
    public JiraAccountDto getJiraAccount() {
        long usrId = userContext.getUserId();
        return jiraAccountService.getJiraAccountInfo(usrId);
    }
}
