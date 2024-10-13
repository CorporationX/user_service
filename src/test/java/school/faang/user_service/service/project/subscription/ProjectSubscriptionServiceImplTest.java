package school.faang.user_service.service.project.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.ProjectDto;
import school.faang.user_service.model.event.ProjectFollowerEvent;
import school.faang.user_service.publisher.ProjectSubscriptionPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.service.impl.project.subscription.ProjectSubscriptionServiceImpl;
import school.faang.user_service.validator.project.subscription.ProjectSubscriptionValidator;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyLong;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProjectSubscriptionServiceImplTest {

    @Mock
    private ProjectSubscriptionRepository subscriptionRepository;

    @Mock
    private ProjectSubscriptionValidator validator;

    @Mock
    private ProjectSubscriptionPublisher publisher;

    @InjectMocks
    private ProjectSubscriptionServiceImpl service;

    @Captor
    private ArgumentCaptor<ProjectFollowerEvent> eventCaptor;

    @Test
    void testFollowProjectOk(){
        when(validator.isAlreadySubscribed(anyLong(), anyLong())).thenReturn(false);
        ProjectDto projectDto = ProjectDto.builder()
                .projectId(4L)
                .ownerId(5L)
                .build();

        service.followProject(2L, projectDto);

        verify(publisher).publish(eventCaptor.capture());

        assertEquals(5L, eventCaptor.getValue().ownerId());
        assertEquals(4L, eventCaptor.getValue().projectId());
        assertEquals(2L, eventCaptor.getValue().followerId());
    }
}
