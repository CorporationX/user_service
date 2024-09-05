package school.faang.user_service.service.recomendation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import school.faang.user_service.UserServiceApplication;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = UserServiceApplication.class)
@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    RecommendationRequest recommendationRequest;
    @Mock
    private RecommendationRequestMapper mapper;
    @Mock
    UserRepository userRepository;
    @Mock
    RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    SkillRequestRepository skillRequestRepository;
    @Mock
    SkillRepository skillRepository;
    @InjectMocks
    RecommendationRequestService recommendationRequestService;
    static RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    public void setUp() {
        recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setId(1L);
        recommendationRequestDto.setReceiverId(2L);
        recommendationRequestDto.setRequesterId(5L);
        recommendationRequestDto.setCreatedAt(LocalDateTime.now());
        recommendationRequestDto.setMessage("testMessage");
        recommendationRequestDto.setStatus(RequestStatus.PENDING);
        recommendationRequestDto.setSkillsId(List.of(1L, 2L));


    }

    //Positive
    @Test
    @DisplayName("Successfully find users in DB")
    public void testCheckUsersTrue() {
        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
        Mockito.when(userRepository.existsById(5L)).thenReturn(true);
        userRepository.existsById(2L);
        userRepository.existsById(5L);
        assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(recommendationRequestDto);
        });
    }

    @Test
    public void testExistById () {
        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
        assertTrue(userRepository.existsById(2L));
    }

    @Test
    @DisplayName("Test create")
    public void testCreate() {
        Mockito.when(userRepository.existsById(recommendationRequestDto.getRequesterId())).thenReturn(true);
        Mockito.when(userRepository.existsById(recommendationRequestDto.getReceiverId())).thenReturn(true);
        Mockito.when(recommendationRequestRepository.save(mapper.mapToEntity(recommendationRequestDto))).thenReturn(recommendationRequest);
        Mockito.when(mapper.mapToDto(recommendationRequest)).thenReturn(recommendationRequestDto);
        Mockito.when(recommendationRequest.setSkills(List.of(new SkillRequest(), new SkillRequest())));
        RecommendationRequestDto result = recommendationRequestService.create(recommendationRequestDto);
    }
//    @Test
//    @DisplayName("Successfully find skills in DB")
//    public void testIsSkillsInDbTrue() {
//        when(recommendationRequestService.isSkillsInDb(recommendationRequestDto)).thenReturn(true);
//        boolean res = recommendationRequestService.isSkillsInDb(recommendationRequestDto);
//        assertTrue(res);
//    }

//    //Negative
//    @Test
//    @DisplayName("Can't find users in DB")
//    public void testCheckUsersFalse() {
//        assertFalse(recommendationRequestService.checkUsers(recommendationRequestDto));
//    }
//
//    @Test
//    @DisplayName("Can't find skills in DB")
//    public void testIsSkillsInDbFalse() {
//        assertFalse(recommendationRequestService.isSkillsInDb(recommendationRequestDto));
//    }
}