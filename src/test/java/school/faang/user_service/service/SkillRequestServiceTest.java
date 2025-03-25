package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillRequestServiceTest {

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @InjectMocks
    private SkillRequestService skillRequestService;

    @Test
    public void testCreateSkillRequestPassDataToRepositorySuccess() {
        long requestId = 1L;
        long skillId = 2L;

        skillRequestService.createSkillRequest(requestId, skillId);

        verify(skillRequestRepository, times(1)).create(requestId, skillId);
    }

    @Test
    public void testGetSkillRequestsByRequestIdRequestDataFromRepositoryReturnsSkillRequests() {
        var requestId = 10L;
        var skillRequest1 = new SkillRequest();
        skillRequest1.setId(1);
        var skillRequest2 = new SkillRequest();
        skillRequest2.setId(2);
        var dataFromRepository = List.of(skillRequest1, skillRequest2);
        when(skillRequestRepository.findAllByRequestId(requestId)).thenReturn(dataFromRepository);

        var result = skillRequestService.getSkillRequestsByRequestId(requestId);

        verify(skillRequestRepository, times(1)).findAllByRequestId(requestId);
        assertEquals(dataFromRepository, result);
    }
}