package school.faang.user_service.service.jira;

import school.faang.user_service.dto.jira.JiraAccountDto;

public interface JiraAccountService {
    JiraAccountDto addJiraAccount(long userId, JiraAccountDto jiraAccountDto);

    JiraAccountDto getJiraAccountInfo(long userId);
}
