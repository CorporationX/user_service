package school.faang.user_service.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validation.SubscriptionValidator;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @Mock
    SubscriptionService service;
    @Mock
    UserContext ctx;
    @Mock
    SubscriptionValidator validator;
    @InjectMocks
    private SubscriptionController controller;


}