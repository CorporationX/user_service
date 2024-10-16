package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.ProjectServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.ProjectDto;
import school.faang.user_service.model.dto.ProjectSubscriptionDto;
import school.faang.user_service.model.entity.ProjectSubscription;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.ProjectSubscriptionMapper;
import school.faang.user_service.publisher.ProjectFollowerEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.service.impl.ProjectSubscriptionServiceImpl;
import school.faang.user_service.validator.ProjectSubscriptionValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectSubscriptionServiceTest {

    @Mock
    private ProjectSubscriptionRepository projectSubscriptionRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private ProjectSubscriptionMapper projectSubscriptionMapper;
    @Mock
    private ProjectSubscriptionValidator projectSubscriptionValidator;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private ProjectFollowerEventPublisher projectFollowerEventPublisher;

    @InjectMocks
    private ProjectSubscriptionServiceImpl projectSubscriptionService;

    @Test
    void testSubscribeUserToProject_UserAlreadySubscribed_ThrowsException() {
        long userId = 1L;
        long projectId = 1L;
        long contextUserId = 1L;

        when(userContext.getUserId()).thenReturn(contextUserId);
        when(projectSubscriptionRepository.existsByFollowerIdAndProjectId(userId, projectId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> 
            projectSubscriptionService.subscribeUserToProject(userId, projectId));
        
        verify(projectSubscriptionRepository, never()).followProject(userId, projectId);
    }

    @Test
    void testSubscribeUserToProject_ValidScenario_ReturnsSubscriptionDto() {
        long userId = 1L;
        long projectId = 1L;
        long contextUserId = 1L;
        ProjectDto projectDto = new ProjectDto();
        projectDto.setOwnerId(2L);
        ProjectSubscription projectSubscription = new ProjectSubscription();
        ProjectSubscriptionDto projectSubscriptionDto = new ProjectSubscriptionDto();

        when(userContext.getUserId()).thenReturn(contextUserId);
        when(projectSubscriptionRepository.existsByFollowerIdAndProjectId(userId, projectId)).thenReturn(false);
        when(projectServiceClient.getProject(projectId)).thenReturn(projectDto);
        when(projectSubscriptionRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.of(projectSubscription));
        when(projectSubscriptionMapper.toDto(projectSubscription)).thenReturn(projectSubscriptionDto);

        ProjectSubscriptionDto result = projectSubscriptionService.subscribeUserToProject(userId, projectId);

        assertNotNull(result);
        verify(projectSubscriptionValidator).validateUser(contextUserId, userId);
        verify(projectSubscriptionRepository).followProject(userId, projectId);
        verify(projectFollowerEventPublisher).publish(any());
    }
}
