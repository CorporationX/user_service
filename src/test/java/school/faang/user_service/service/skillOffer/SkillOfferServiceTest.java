//package school.faang.user_service.service.skillOffer;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import school.faang.user_service.dto.recommendation.RecommendationDto;
//import school.faang.user_service.dto.recommendation.SkillOfferDto;
//import school.faang.user_service.repository.SkillRepository;
//import school.faang.user_service.repository.UserSkillGuaranteeRepository;
//import school.faang.user_service.repository.recommendation.SkillOfferRepository;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class SkillOfferServiceTest {
//
//    @Mock
//    private SkillOfferRepository skillOfferRepository;
//    @Mock
//    private SkillRepository skillRepository;
//    @Mock
//    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
//    @InjectMocks
//    private SkillOfferService skillOfferService;
//    private RecommendationDto recommendationDto;
//    private SkillOfferDto skillOfferDto;
//
//    @BeforeEach
//    void init(){
//        SkillOfferDto skillOfferDto = new SkillOfferDto();
//        skillOfferDto.setId(1L);
//
//        recommendationDto = RecommendationDto.builder()
//                .authorId(1L)
//                .receiverId(2L)
//                .skillOffers(List.of(skillOfferDto))
//                .build();
//    }
//
//    @Test
//    void testCheckForSkills(){
//        when(skillOfferRepository.findAllById(anyList()).spliterator())
//                .thenThrow(new NullPointerException("exception"));
//        Exception exception = assertThrows(NullPointerException.class, () ->
//                skillOfferService.checkForSkills(List.of(skillOfferDto)));
//
//        assertEquals("exception", exception.getMessage());
//    }
//}