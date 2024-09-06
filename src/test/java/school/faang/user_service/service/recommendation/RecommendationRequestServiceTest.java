package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    private RecommendationRequest recommendationRequestEntity;
    private RecommendationRequestDto recommendationRequestDto;


    @Mock
    private RecommendationRequestMapper mapper;
    @Mock
    UserRepository userRepository;
    @Mock
    RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    SkillRequestRepository skillRequestRepository;
    @Mock
    SkillRepository skillRepository;
    @InjectMocks
    RecommendationRequestService recommendationRequestService;

    @BeforeEach
    public void setUp() {
        LocalDateTime now = LocalDateTime.now();
        List<SkillRequest> skillRequests = List.of(new SkillRequest(), new SkillRequest());
        recommendationRequestEntity = new RecommendationRequest();
        recommendationRequestEntity.setId(1L);
        recommendationRequestEntity.setRequester(new User());
        recommendationRequestEntity.getRequester().setId(5L);
        recommendationRequestEntity.setRequester(new User());
        recommendationRequestEntity.getRequester().setId(2L);
        recommendationRequestEntity.setCreatedAt(now);
        recommendationRequestEntity.setMessage("testMessage");
        recommendationRequestEntity.setStatus(RequestStatus.PENDING);
        recommendationRequestEntity.setSkills(skillRequests);


        recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setId(1L);
        recommendationRequestDto.setReceiverId(2L);
        recommendationRequestDto.setRequesterId(5L);
        recommendationRequestDto.setCreatedAt(now);
        recommendationRequestDto.setMessage("testMessage");
        recommendationRequestDto.setStatus(RequestStatus.PENDING);
        recommendationRequestDto.setSkills(skillRequests);
    }

    //Positive
//    @Test
//    @DisplayName("Successfully find users in DB")
//    public void testCheckUsersTrue() {
//        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
//        Mockito.when(userRepository.existsById(5L)).thenReturn(true);
//        userRepository.existsById(2L);
//        userRepository.existsById(5L);
//        assertThrows(IllegalArgumentException.class, () -> {
//            recommendationRequestService.create(recommendationRequestDto);
//        });
//    }

    @Test
    @DisplayName("Test successfully existById")
    public void testExistById() {
        when(userRepository.existsById(2L)).thenReturn(true);
        assertTrue(userRepository.existsById(2L));
    }

    @Test
    @DisplayName("Test successfully existById")
    public void testNotExistById() {
        when(userRepository.existsById(2L)).thenReturn(false);
        assertFalse(userRepository.existsById(2L));
    }

    @Test
    @DisplayName("Test create")
    public void testCreate() {
        when(userRepository.existsById(recommendationRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(recommendationRequestDto.getReceiverId())).thenReturn(true);
        when(recommendationRequestRepository.save(mapper.mapToEntity(recommendationRequestDto))).thenReturn(recommendationRequestEntity);
        when(mapper.mapToDto(recommendationRequestEntity)).thenReturn(recommendationRequestDto);
        RecommendationRequestDto result = recommendationRequestService.create(recommendationRequestDto);
        System.out.println(result);
        System.out.println(recommendationRequestDto);
        assertAll(
                () -> assertEquals(recommendationRequestDto.getRequesterId(), result.getRequesterId()),
                () -> assertEquals(recommendationRequestDto.getMessage(), result.getMessage())
        );
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