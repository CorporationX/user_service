package school.faang.user_service.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import school.faang.user_service.CommonIntegrationTest;
import school.faang.user_service.controller.mentorship_request.MentorshipRequestController;
import school.faang.user_service.service.event.EventServiceImpl;
import school.faang.user_service.service.goal.GoalServiceImpl;
import school.faang.user_service.service.mentorship_request.MentorshipRequestService;
import school.faang.user_service.service.recomendation.RecommendationRequestService;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationContextTest extends CommonIntegrationTest {
    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    public void testEventServiceBeanExists() {
        EventServiceImpl bean = context.getBean(EventServiceImpl.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testGoalServiceBeanExists() {
        GoalServiceImpl bean = context.getBean(GoalServiceImpl.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testMentorshipRequestServiceBeanExists() {
        MentorshipRequestService bean = context.getBean(MentorshipRequestService.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testMentorshipRequestControllerBeanExists() {
        MentorshipRequestController bean = context.getBean(MentorshipRequestController.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testRecommendationRequestServiceBeanExists() {
        RecommendationRequestService bean = context.getBean(RecommendationRequestService.class);
        assertThat(bean).isNotNull();
    }
}
