package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.context.UserHeaderFilter;


@WebMvcTest
@Import({UserHeaderFilter.class, UserContext.class})
public abstract class BaseControllerTest {

    protected static final String USER_HEADER = "x-user-id";
    protected static final String DEFAULT_HEADER_VALUE = "1";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserContext userContext;

    @Autowired
    protected WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new UserHeaderFilter(userContext))
                .build();
    }

}