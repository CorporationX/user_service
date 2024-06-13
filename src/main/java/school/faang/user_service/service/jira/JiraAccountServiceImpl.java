package school.faang.user_service.service.jira;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.jira.JiraAccountDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.jira.JiraAccount;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.jira.JiraAccountMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.jira.JiraAccountRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraAccountServiceImpl implements JiraAccountService {

    private final JiraAccountRepository jiraAccountRepository;
    private final UserRepository userRepository;
    private final JiraAccountMapper jiraAccountMapper;

    @Override
    public JiraAccountDto addJiraAccount(long userId, JiraAccountDto jiraAccountDto) {

        jiraAccountDto.setUserId(userId);
        JiraAccount jiraAccount = jiraAccountRepository.save(jiraAccountMapper.toEntity(jiraAccountDto));

        User user = getUser(userId);
        user.setJiraAccount(jiraAccount);

        return jiraAccountMapper.toDto(jiraAccount);
    }

    @Override
    public JiraAccountDto getJiraAccountInfo(long userId) {

        User user = getUser(userId);
        return jiraAccountMapper.toDto(user.getJiraAccount());
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with userId= " + userId + " not found"));
    }
}
