package school.faang.user_service.validator.recommendation;

import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.RecommendationRequestDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.RecommendationRequest;
import school.faang.user_service.model.entity.SkillRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.RecommendationRequestRepository;
import school.faang.user_service.repository.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class RecommendationRequestValidatorTest {

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    RecommendationRequestRepository recommendationRequestRepository;

    @InjectMocks
    RecommendationRequestValidator recommendationRequestValidator;
    RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    void setUp() {
        recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setRequesterId(5L);
        recommendationRequestDto.setReceiverId(1L);
        recommendationRequestDto.setSkillsIds(List.of(1L, 2L));
    }

    @Test
    public void isRequestAllowedTrue() {
        when(recommendationRequestRepository.findAll()).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> recommendationRequestValidator.isRequestAllowed(recommendationRequestDto));
    }

    @Test
    public void isRequestAllowedFalse() {
        User requesterUser = new User();
        requesterUser.setId(recommendationRequestDto.getRequesterId());
        User receiverUser = new User();
        receiverUser.setId(recommendationRequestDto.getReceiverId());
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setRequester(requesterUser);
        recommendationRequest.setReceiver(receiverUser);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        DuplicateRequestException exception = assertThrows(DuplicateRequestException.class,
                () -> recommendationRequestValidator.isRequestAllowed(recommendationRequestDto));
        assertEquals("There is already a request created less than 6 months ago", exception.getMessage());
    }

    @Test
    public void testIsUsersInDbWrongId() {
        when(userRepository.existsById(recommendationRequestDto.getRequesterId())).thenReturn(false);
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            recommendationRequestValidator.isUsersInDb(recommendationRequestDto);
        });
        assertEquals("Requester id or receiver id is wrong", exception.getMessage());
    }

    @Test
    public void testIsUsersInDbCorrectUserId() {
        when(userRepository.existsById(recommendationRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(recommendationRequestDto.getReceiverId())).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestValidator.isUsersInDb(recommendationRequestDto));
    }

    @Test
    public void testisSkillsInDbCorrectSkillId() {
        List<SkillRequest> skillRequests = createSkillRequests();
        int skillRequestsCount = 2;

        runTest(skillRequestsCount, recommendationRequestDto, skillRequests);

        assertDoesNotThrow(() -> recommendationRequestValidator.isSkillsInDb(recommendationRequestDto));
    }

    @Test
    public void testisSkillsInDbWrongSkillId() {
        int skillRequestsCount = 1;
        List<SkillRequest> skillRequests = createSkillRequests();

        runTest(skillRequestsCount, recommendationRequestDto, skillRequests);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            recommendationRequestValidator.isSkillsInDb(recommendationRequestDto);
        });
        assertEquals("No such skills in database", exception.getMessage());
    }

    public void runTest(int count, RecommendationRequestDto recommendationRequestDto, List<SkillRequest> skillRequests) {
        when(skillRequestRepository.findAllById(recommendationRequestDto.getSkillsIds())).thenReturn(skillRequests);
        when(skillRepository.countExisting(List.of(1L, 2L))).thenReturn(count);
    }

    public static List<SkillRequest> createSkillRequests() {
        Skill firstSkill = new Skill();
        firstSkill.setId(1L);
        Skill secondSkill = new Skill();
        secondSkill.setId(2L);
        SkillRequest firstSkillRequest = new SkillRequest();
        SkillRequest secondSkillRequest = new SkillRequest();
        firstSkillRequest.setSkill(firstSkill);
        secondSkillRequest.setSkill(secondSkill);
        return List.of(firstSkillRequest, secondSkillRequest);
    }
}