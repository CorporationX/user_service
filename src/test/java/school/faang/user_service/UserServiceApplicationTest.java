package school.faang.user_service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.filters.*;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.event.EventServiceHelper;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.service.goal.GoalService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
class UserServiceApplicationTest {
    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    public void testEventControllerBeanExists() {
        EventController bean = context.getBean(EventController.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testEventMapperBeanExists() {
        EventMapper bean = context.getBean(EventMapper.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testSkillMapperBeanExists() {
        SkillMapper bean = context.getBean(SkillMapper.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testEventDescriptionFilterBeanExists() {
        EventDescriptionFilter bean = context.getBean(EventDescriptionFilter.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testEventEndDateAfterFilterBeanExists() {
        EventEndDateAfterFilter bean = context.getBean(EventEndDateAfterFilter.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testEventEndDateBeforeFilterBeanExists() {
        EventEndDateBeforeFilter bean = context.getBean(EventEndDateBeforeFilter.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testEventLocationFilterBeanExists() {
        EventLocationFilter bean = context.getBean(EventLocationFilter.class);
        assertThat(bean).isNotNull();
    }
    @Test
    public void testEventMaxAttendeesLargerFilterBeanExists() {
        EventMaxAttendeesLargerFilter bean = context.getBean(EventMaxAttendeesLargerFilter.class);
        assertThat(bean).isNotNull();
    }
    @Test
    public void testEventMaxAttendeesLowerFilterBeanExists() {
        EventMaxAttendeesLowerFilter bean = context.getBean(EventMaxAttendeesLowerFilter.class);
        assertThat(bean).isNotNull();
    }
    @Test
    public void testEventRelatedAllSkillsFilterBeanExists() {
        EventRelatedAllSkillsFilter bean = context.getBean(EventRelatedAllSkillsFilter.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testEventRelatedAnySkillsFilterBeanExists() {
        EventRelatedAnySkillsFilter bean = context.getBean(EventRelatedAnySkillsFilter.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testEventStartDateAfterFilterBeanExists() {
        EventStartDateAfterFilter bean = context.getBean(EventStartDateAfterFilter.class);
        assertThat(bean).isNotNull();
    }
    @Test
    public void testEventStartDateBeforeFilterBeanExists() {
        EventStartDateBeforeFilter bean = context.getBean(EventStartDateBeforeFilter.class);
        assertThat(bean).isNotNull();
    }
    @Test
    public void testEventStatusFilterBeanExists() {
        EventStatusFilter bean = context.getBean(EventStatusFilter.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testEventTitleFilterBeanExists() {
        EventTitleFilter bean = context.getBean(EventTitleFilter.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testEventTypeFilterExists() {
        EventTypeFilter bean = context.getBean(EventTypeFilter.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testEventServiceHelperExists() {
        EventServiceHelper bean = context.getBean(EventServiceHelper.class);
        assertThat(bean).isNotNull();
    }
    @Test
    public void testEventServiceExists() {
        EventServiceHelper bean = context.getBean(EventServiceHelper.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testGoalServiceExists() {
        GoalService bean = context.getBean(GoalService.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testGoalInvitationServiceExists() {
        GoalInvitationService bean = context.getBean(GoalInvitationService.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testMentorshipRequestServiceExists() {
        MentorshipRequestService bean = context.getBean(MentorshipRequestService.class);
        assertThat(bean).isNotNull();
    }
}
