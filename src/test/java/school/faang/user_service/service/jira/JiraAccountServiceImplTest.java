package school.faang.user_service.service.jira;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.jira.JiraAccountDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.jira.JiraAccount;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.jira.JiraAccountMapper;
import school.faang.user_service.repository.jira.JiraAccountRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraAccountServiceImplTest {

    @Mock
    private JiraAccountRepository jiraAccountRepository;
    @Mock
    private JiraAccountMapper jiraAccountMapper;

    @InjectMocks
    private JiraAccountServiceImpl jiraAccountService;

    private final long userId = 1L;
    private JiraAccount jiraAccount;
    private JiraAccountDto jiraAccountDto;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(userId).build();

        jiraAccount = JiraAccount.builder()
                .id(2L)
                .user(user)
                .username("username")
                .password("password")
                .projectUrl("url")
                .build();

        jiraAccountDto = JiraAccountDto.builder()
                .id(2L)
                .username("username")
                .password("password")
                .projectUrl("url")
                .build();
    }

    @Test
    void addJiraAccount() {
        when(jiraAccountMapper.toEntity(jiraAccountDto)).thenReturn(jiraAccount);
        when(jiraAccountRepository.save(jiraAccount)).thenReturn(jiraAccount);
        when(jiraAccountMapper.toDto(jiraAccount)).thenReturn(jiraAccountDto);

        JiraAccountDto actual = jiraAccountService.addJiraAccount(userId, jiraAccountDto);
        assertEquals(userId, actual.getUserId());
        assertEquals(jiraAccountDto, actual);

        InOrder inOrder = inOrder(jiraAccountRepository, jiraAccountMapper);
        inOrder.verify(jiraAccountRepository).save(jiraAccount);
        inOrder.verify(jiraAccountMapper).toDto(jiraAccount);
    }

    @Test
    void getJiraAccountInfo() {
        when(jiraAccountRepository.findByUserId(userId)).thenReturn(Optional.of(jiraAccount));
        when(jiraAccountMapper.toDto(jiraAccount)).thenReturn(jiraAccountDto);

        JiraAccountDto actual = jiraAccountService.getJiraAccountInfo(userId);
        assertEquals(jiraAccountDto, actual);

        InOrder inOrder = inOrder(jiraAccountRepository, jiraAccountMapper);
        inOrder.verify(jiraAccountRepository).findByUserId(userId);
        inOrder.verify(jiraAccountMapper).toDto(jiraAccount);
    }

    @Test
    void getJiraAccountInfoNotFoundException() {
        when(jiraAccountRepository.findByUserId(userId)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> jiraAccountService.getJiraAccountInfo(userId));
        assertEquals("Jira account with userId=" + userId + " not found", actual.getMessage());
    }
}