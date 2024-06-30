package school.faang.user_service.controller.jira;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.jira.JiraAccountDto;
import school.faang.user_service.service.jira.JiraAccountService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class JiraAccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JiraAccountService jiraAccountService;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private JiraAccountController jiraAccountController;

    private final long userId = 1L;
    private JiraAccountDto jiraAccountDto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        jiraAccountDto = JiraAccountDto.builder()
                .username("username")
                .password("password")
                .projectUrl("url")
                .build();

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(jiraAccountController).build();
    }

    @Test
    void addJiraAccount() throws Exception {
        String json = objectMapper.writeValueAsString(jiraAccountDto);

        when(userContext.getUserId()).thenReturn(userId);
        when(jiraAccountService.addJiraAccount(eq(userId), any(JiraAccountDto.class))).thenReturn(jiraAccountDto);

        mockMvc.perform(post("/users/account/jira")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));

        InOrder inOrder = inOrder(userContext, jiraAccountService);
        inOrder.verify(userContext).getUserId();
        inOrder.verify(jiraAccountService).addJiraAccount(eq(userId), any(JiraAccountDto.class));
    }

    @Test
    void getJiraAccount() throws Exception {
        String json = objectMapper.writeValueAsString(jiraAccountDto);

        when(jiraAccountService.getJiraAccountInfo(userId)).thenReturn(jiraAccountDto);
        when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(get("/users/account/jira"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        InOrder inOrder = inOrder(userContext, jiraAccountService);
        inOrder.verify(userContext).getUserId();
        inOrder.verify(jiraAccountService).getJiraAccountInfo(userId);
    }
}