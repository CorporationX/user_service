package school.faang.user_service.service.jira;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.jira.JiraAccountDto;
import school.faang.user_service.entity.jira.JiraAccount;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.jira.JiraAccountMapper;
import school.faang.user_service.repository.jira.JiraAccountRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraAccountServiceImpl implements JiraAccountService {

    private final JiraAccountRepository jiraAccountRepository;
    private final JiraAccountMapper jiraAccountMapper;

    @Override
    public JiraAccountDto addJiraAccount(long userId, JiraAccountDto jiraAccountDto) {

        jiraAccountDto.setUserId(userId);
        JiraAccount jiraAccount = jiraAccountMapper.toEntity(jiraAccountDto);
        jiraAccount = jiraAccountRepository.save(jiraAccount);

        return jiraAccountMapper.toDto(jiraAccount);
    }

    @Override
    public JiraAccountDto getJiraAccountInfo(long userId) {

        JiraAccount account = jiraAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Jira account with userId=" + userId + " not found"));

        return jiraAccountMapper.toDto(account);
    }
}
