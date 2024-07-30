package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @InjectMocks
    private RecommendationService recommendationService;
    Recommendation recommendation;
    Recommendation recommendationFindFirst;
    RecommendationDto recommendationDto;
    User author;
    User receiver;
    Skill skill;
    SkillOffer skillOffer;
    SkillOfferDto skillOfferDto;

    @BeforeEach
    void init() {
        skill = new Skill();
        skill.setId(1L);

        skillOffer = new SkillOffer();
        skillOffer.setId(1L);
        skillOffer.setSkill(skill);

        skillOfferDto = new SkillOfferDto();
        skillOfferDto.setId(1L);

        author = User.builder()
                .id(1L)
                .build();
        receiver = User.builder()
                .id(2L)
                .build();
        recommendation = Recommendation.builder()
                .author(author)
                .receiver(receiver)
                .skillOffers(List.of(skillOffer))
                .build();

        recommendationFindFirst = Recommendation.builder()
                .author(author)
                .receiver(receiver)
                .skillOffers(List.of(skillOffer))
                .build();

        recommendationDto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .skillOffers(List.of(skillOfferDto))
                .build();
    }

    @Test
    @DisplayName("findFirstOrderException")
    void testCheckNotRecommendBeforeSixMonthsOrderException() {
        Mockito.when(recommendationRepository
                        .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                recommendationService.create(recommendationDto));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("BeforeSixMonthsException")
    void testCheckNotRecommendBeforeSixMonthsException() {
        findFirstOrderValid();
        recommendationFindFirst.setUpdatedAt(LocalDateTime.now().minus(5, ChronoUnit.MONTHS));

        assertThrows(IllegalStateException.class, () ->
                recommendationService.create(recommendationDto));
    }

    @Test
    @DisplayName("checkForSkills")
    void testCheckForSkillsException() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setSkillId(1L);

        findFirstOrderValid();
        recommendationFindFirst.setUpdatedAt(LocalDateTime.now().minus(7, ChronoUnit.MONTHS));

        Mockito.when(skillOfferRepository.existsById(Mockito.anyLong()))
                .thenThrow(new NullPointerException("exception"));
        Exception exception = assertThrows(NullPointerException.class, () ->
                recommendationService.create(recommendationDto));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("saveSkillOffersAddAndSaveGuarantee")
    void testAddAndSaveGuarantee() {
        Mockito.when(recommendationMapper.toEntity(any(RecommendationDto.class)))
                .thenReturn(recommendation);
        findFirstOrderValid();
        recommendationFindFirst.setUpdatedAt(LocalDateTime.now().minus(7, ChronoUnit.MONTHS));

        Mockito.when(skillOfferRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong()))
                .thenReturn(List.of(skill));
        Mockito.when(userSkillGuaranteeRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Mockito.when(userSkillGuaranteeRepository.save(any(UserSkillGuarantee.class)))
                .thenReturn(new UserSkillGuarantee());

        recommendationService.create(recommendationDto);

        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(Mockito.anyLong());
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(1))
                .save(any(UserSkillGuarantee.class));
    }

    @Test
    @DisplayName("saveSkillOffers")
    void testSaveSkillOffers(){
        Mockito.when(recommendationMapper.toEntity(any(RecommendationDto.class)))
                .thenReturn(recommendation);
        findFirstOrderValid();
        recommendationFindFirst.setUpdatedAt(LocalDateTime.now().minus(7, ChronoUnit.MONTHS));

        Mockito.when(skillOfferRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong()))
                .thenReturn(List.of(skill));
        Mockito.when(userSkillGuaranteeRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(skillOfferRepository.save(Mockito.any(SkillOffer.class))).thenReturn(new SkillOffer());

        recommendationService.create(recommendationDto);

        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(Mockito.anyLong());
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito.verify(skillOfferRepository, Mockito.times(1))
                .save(Mockito.any(SkillOffer.class));
    }

    @Test
    @DisplayName("fullMethodCreate")
    void testCreate(){
        Mockito.when(recommendationMapper.toEntity(any(RecommendationDto.class)))
                .thenReturn(recommendation);
        findFirstOrderValid();
        recommendationFindFirst.setUpdatedAt(LocalDateTime.now().minus(7, ChronoUnit.MONTHS));

        Mockito.when(skillOfferRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong()))
                .thenReturn(List.of(skill));
        Mockito.when(userSkillGuaranteeRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(skillOfferRepository.save(Mockito.any(SkillOffer.class)))
                .thenReturn(new SkillOffer());
        Mockito.when(recommendationRepository.save(any(Recommendation.class)))
                .thenReturn(new Recommendation());
        Mockito.when(recommendationMapper.toDto(any(Recommendation.class)))
                .thenReturn(recommendationDto);

        recommendationService.create(recommendationDto);

        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(Mockito.anyLong());
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito.verify(skillOfferRepository, Mockito.times(1))
                .save(Mockito.any(SkillOffer.class));
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .save(any(Recommendation.class));
        Mockito.verify(recommendationMapper, Mockito.times(1))
                .toDto(any(Recommendation.class));
    }

    private void findFirstOrderValid() {
        Mockito.when(recommendationRepository
                        .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(recommendationFindFirst));
    }
}