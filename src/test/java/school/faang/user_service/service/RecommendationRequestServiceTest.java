package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private List<RequestFilter> requestFilter;
    @Captor
    private ArgumentCaptor<RecommendationRequest> captor;

    @Test
    public void testCreateWithFindByIdRequesterAndReceiver() {
        RecommendationRequestDto requestDto = processExceptionWithFindingIdRequesterAndReceiver(true);

        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testCreateBelieveTheRequestToSendNotMoreThanSixMonths() {
        RecommendationRequestDto requestDto = processExceptionWithFindingIdRequesterAndReceiver(false);
        checkLastRequest(requestDto, LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testCreateCheckTheExistenceOfSkillsInTheDatabase() {
        RecommendationRequestDto requestDto = processExceptionWithFindingIdRequesterAndReceiver(false);
        checkLastRequest(requestDto, LocalDateTime.now().minus(7, ChronoUnit.MONTHS));
        checkSkills(3);
        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testCreateSave() {
        RecommendationRequestDto requestDto = processExceptionWithFindingIdRequesterAndReceiver(false);
        checkLastRequest(requestDto, LocalDateTime.now().minus(7, ChronoUnit.MONTHS));
        checkSkills(2);
        recommendationRequestService.create(requestDto);

        verify(recommendationRequestRepository, times(1)).save(captor.capture());
    }

    private RecommendationRequestDto processExceptionWithFindingIdRequesterAndReceiver(boolean existsById) {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1);
        requestDto.setRecieverId(1);

        when(userRepository.findById(requestDto.getRequesterId()).isEmpty()).thenReturn(existsById);
        when(userRepository.findById(requestDto.getRecieverId()).isEmpty()).thenReturn(existsById);

        return requestDto;
    }

    private void checkLastRequest(RecommendationRequestDto requestDto, LocalDateTime localDateTime) {
        RecommendationRequest request = new RecommendationRequest();
        request.setUpdatedAt(localDateTime);

        when(recommendationRequestRepository
                .findLatestPendingRequest(requestDto.getRequesterId(), requestDto.getRecieverId()))
                .thenReturn(Optional.of(request));
    }

    private void checkSkills(int id) {
        List<Long> skillsId = new ArrayList<>(id);
        when(skillRepository.findAllById(skillsId)).thenReturn(List.of(new Skill(), new Skill()));
    }
}