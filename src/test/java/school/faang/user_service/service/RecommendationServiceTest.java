package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.*;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @InjectMocks
    private RecommendationService recommendationService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private SkillOfferRepository skillOffersRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserSkillGuaranteeMapper userSkillGuaranteeDtoMapper;
    @Spy
    private SkillOfferMapper skillOfferMapper = new SkillOfferMapperImpl();
    @Spy
    private RecommendationMapper recommendationMapper = new RecommendationMapperImpl(skillOfferMapper);
    @Spy
    private UserSkillGuaranteeMapper userSkillGuaranteeMapper = new UserSkillGuaranteeMapperImpl();
    @Spy
    private SkillMapper skillMapper = new SkillMapperImpl(userSkillGuaranteeMapper);


    SkillOfferDto skillOfferDto;
    RecommendationDto recommendationDto;
    Recommendation recommendation;
    SkillOffer skillOffer;
    Skill skill;
    List<UserSkillGuarantee> guaranteesList;
    UserSkillGuarantee guarantees;
    User athorId;

    @BeforeEach
    void setUp() {
        this.skillOfferDto = new SkillOfferDto(1L, 1L, 1L);
        this.recommendationDto = RecommendationDto
                .builder()
                .authorId(1L)
                .receiverId(1L)
                .content("content")
                .skillOffers(List.of(skillOfferDto))
                .build();
        this.athorId = User
                .builder()
                .id(1)
                .build();
        this.guarantees = UserSkillGuarantee
                .builder()
                .user(athorId)
                .build();
        this.guaranteesList = new ArrayList<>(List.of(guarantees));
        this.skill = Skill
                .builder()
                .id(1L)
                .guarantees(guaranteesList)
                .build();
        this.skillOffer = SkillOffer
                .builder()
                .id(1L)
                .skill(skill)
                .recommendation(recommendation)
                .build();
        this.recommendation = Recommendation
                .builder()
                .id(1L)
                .skillOffers(new ArrayList<>(List.of(skillOffer)))
                .receiver(User.builder().id(1L).build())
                .author(User.builder().id(1L).build())
                .build();
    }

    @Test
    public void testCreateThrowException() {
        Mockito.when(recommendationRepository.create(1L, 1L, "content"))
                .thenReturn(1L);
        assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.create(recommendationDto);
        });
    }

    @Test
    public void testCreateCallValidateData() {
        Mockito.when(skillOffersRepository.findById(1L))
                .thenReturn(Optional.of(new SkillOffer()));
        Mockito.when(skillOffersRepository.create(1L, 1L))
                .thenReturn(1L);
        Mockito.when(recommendationRepository.create(1L, 1L, "content"))
                .thenReturn(1L);
        Mockito.when(recommendationRepository.findById(1L))
                .thenReturn(Optional.of(recommendation));

        recommendationService.create(recommendationDto);

        Mockito.verify(recommendationValidator).validateData(recommendationDto);
    }

    @Test
    public void testCreateCallValidateSkill() {
        Mockito.when(skillOffersRepository.findById(1L))
                .thenReturn(Optional.of(new SkillOffer()));
        Mockito.when(skillOffersRepository.create(1L, 1L))
                .thenReturn(1L);
        Mockito.when(recommendationRepository.create(1L, 1L, "content"))
                .thenReturn(1L);
        Mockito.when(recommendationRepository.findById(1L))
                .thenReturn(Optional.of(recommendation));

        recommendationService.create(recommendationDto);

        Mockito.verify(recommendationValidator).validateSkill(recommendationDto);
    }

    @Test
    public void testCreateCallSkillSave() {
        Mockito.when(skillOffersRepository.findById(1L))
                .thenReturn(Optional.of(new SkillOffer()));
        Mockito.when(skillOffersRepository.create(1L, 1L))
                .thenReturn(1L);
        Mockito.when(recommendationRepository.create(1L, 1L, "content"))
                .thenReturn(1L);
        Mockito.when(recommendationRepository.findById(1L))
                .thenReturn(Optional.of(recommendation));

        recommendationService.create(recommendationDto);

        assertEquals(2, recommendation.getSkillOffers().size());
    }

    @Test
    public void testSkillSaveThrowException() {
        Mockito.when(recommendationRepository.create(1L, 1L, "content"))
                .thenReturn(1L);
        Mockito.when(recommendationRepository.findById(1L))
                .thenReturn(Optional.of(recommendation));
        Mockito.when(skillOffersRepository.create(1L, 1L))
                .thenReturn(1L);

        assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.create(recommendationDto);
        });
    }

    @Test
    public void testCreate() {
        Mockito.when(skillOffersRepository.findById(1L))
                .thenReturn(Optional.of(new SkillOffer()));
        Mockito.when(skillOffersRepository.create(1L, 1L))
                .thenReturn(1L);
        Mockito.when(recommendationRepository.create(1L, 1L, "content"))
                .thenReturn(1L);
        Mockito.when(recommendationRepository.findById(1L))
                .thenReturn(Optional.of(recommendation));

        RecommendationDto result = recommendationService.create(recommendationDto);

        assertEquals(2, result.getSkillOffers().size());
    }

    @Test
    public void testCallGuaranteesHaveSkill() {
        User athorId = User
                .builder()
                .id(2)
                .build();
        User userId = User
                .builder()
                .id(3)
                .build();
        UserSkillGuarantee guarantees = UserSkillGuarantee
                .builder()
                .user(athorId)
                .guarantor(userId)
                .build();
        List<UserSkillGuarantee> guaranteesList = new ArrayList<>();
        guaranteesList.add(guarantees);
        Skill skill = Skill
                .builder()
                .id(1L)
                .guarantees(guaranteesList)
                .build();
        List<Skill> skills = new ArrayList<>();
        skills.add(skill);

        Mockito.when(skillRepository.findAllByUserId(recommendation.getReceiver().getId()))
                .thenReturn(skills);

        recommendationService.guaranteesHaveSkill(recommendation);

        assertEquals(2, recommendation.getSkillOffers().get(0).getSkill().getGuarantees().size());
    }
}
