package school.faang.user_service.service;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.*;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillChecker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Spy
    private RecommendationMapperImpl recommendationMapper;
    @Spy
    private SkillOfferMapperImpl skillOfferMapper;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
//    @Mock
//    private SkillChecker skillChecker;

    String str = "2023-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

    private RecommendationService recommendationService;
    private SkillChecker checker;
    private Skill systemSkill;
    private Skill failedSkill;
    List<SkillOffer> doubleSkillOffers;
    List<SkillOffer> noSystemSkillOffers;
    private SkillOffer skillOffer2;
    private SkillOffer skillOffer3;


    @BeforeClass
    void setUp() {
        recommendationService = new RecommendationService(recommendationRepository,
                skillRepository,
                skillOfferRepository,
                userRepository,
                recommendationMapper,
                userSkillGuaranteeRepository,
                checker);

//        Recommendation recommendation = new Recommendation();

        Skill systemSkill = Skill.builder().id(1L).build();
        Skill failedIdSkill = Skill.builder().id(148L).build();
//        List<SkillOffer> doubleSkillOffers = new ArrayList<>();
//        List<SkillOffer> noSystemSkillOffers = new ArrayList<>();

        SkillOffer skillOffer1 = SkillOffer
                .builder()
                .id(1L)
                .skill(systemSkill)
                .build();
        SkillOffer skillOffer2 = SkillOffer
                .builder()
                .id(2L)
                .skill(systemSkill)
                .build();
        SkillOffer skillOffer3 = SkillOffer
                .builder()
                .id(3L)
                .skill(systemSkill)
                .build();
        SkillOffer skillOffer4 = SkillOffer
                .builder()
                .id(4L)
                .skill(failedIdSkill)
                .build();

        doubleSkillOffers.add(skillOffer1);
        doubleSkillOffers.add(skillOffer2);
        noSystemSkillOffers.add(skillOffer1);
        noSystemSkillOffers.add(skillOffer4);
    }

//        подставить как результат метода, который вернет оффера
//        либо разбить по рекомендациям

//        User receiver = User.builder()
//                .id(1L)
//                .recommendationsReceived(recommendationMapper.toListOfDto(receiverSkillOffers));
//                .build();
//        when(recommendationRepository.create(anyLong(), anyLong(), anyString())).thenReturn(1L);
//        when(recommendationRepository.findById(anyLong())).thenReturn(Optional.of(recommendation));
//        (recommendationMapper.toEntity(recommendationDto))

    @Test
    @DisplayName("SkillChecker - double of skill in offered skills")
    void testCreateRecommendation_NotUniqueSkills() {
        RecommendationDto badRecommendationDto = new RecommendationDto();

        badRecommendationDto.setId(1L);
        badRecommendationDto.setSkillOffers(recommendationMapper.toListOfDto(doubleSkillOffers));
        badRecommendationDto.setAuthorId(1L);
        badRecommendationDto.setReceiverId(2L);
        badRecommendationDto.setContent("anyText");

        when(recommendationRepository.create(
                badRecommendationDto.getAuthorId(),
                badRecommendationDto.getReceiverId(),
                badRecommendationDto.getContent()))
                .thenReturn(1L);
        Optional<Recommendation> entity = Optional.of(recommendationMapper.toEntity(badRecommendationDto));
        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(entity);
//                .thenReturn(Optional.of(recommendationMapper.toEntity(badRecommendationDto)));


//        doThrow(DataValidationException.class).when(skillChecker).validate(badRecommendationDto.getSkillOffers());
        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(badRecommendationDto));
        assertEquals("list of skills contains not unique skills, please, check this", ex.getMessage());

//        recommendationService.create(badRecommendationDto);

    }

    @Test
    @DisplayName("SkillChecker - double of skill in offered skills")
    void testCreateRecommendation_SkillsNotValid() {
        RecommendationDto validRecommendationDto = new RecommendationDto();
        validRecommendationDto.setId(1L);
        validRecommendationDto.setSkillOffers(recommendationMapper.toListOfDto(noSystemSkillOffers));
        validRecommendationDto.setAuthorId(1L);
        validRecommendationDto.setReceiverId(2L);
        validRecommendationDto.setContent("anyText");

        when(recommendationRepository.create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent()))
                .thenReturn(1L);
        Optional<Recommendation> entity = Optional.of(recommendationMapper.toEntity(validRecommendationDto));
        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(entity);

        when(skillRepository.countExisting(anyList())).thenReturn(1);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(validRecommendationDto));
        assertEquals("list of skills contains not unique skills, please, check this", ex.getMessage());


    }

    @Test
    void testCreateRecommendation_Positive() {

        RecommendationDto validRecommendationDto = new RecommendationDto();
        validRecommendationDto.setId(1L);
        validRecommendationDto.setSkillOffers(recommendationMapper.toListOfDto(noSystemSkillOffers));
        validRecommendationDto.setAuthorId(1L);
        validRecommendationDto.setReceiverId(2L);
        validRecommendationDto.setContent("anyText");

        User receiver = User.builder()
                .id(2L)
                .skills(List.of(failedSkill)).build();

        User guarantor = User.builder().id(5L).build();
        User guarantor2 = User.builder().id(6L).build();
//+как-то засетить гаранти
        Recommendation firstRecommendationOfUser = new Recommendation();
        firstRecommendationOfUser.setId(2L);
        firstRecommendationOfUser.setSkillOffers(List.of(skillOffer2));
        firstRecommendationOfUser.setAuthor(guarantor);
        firstRecommendationOfUser.setReceiver(receiver);

        Recommendation secondRecommendationOfUser = new Recommendation();
        secondRecommendationOfUser.setId(3L);
        secondRecommendationOfUser.setSkillOffers(List.of(skillOffer3));
        secondRecommendationOfUser.setAuthor(guarantor2);
        secondRecommendationOfUser.setReceiver(receiver);
    }
//        UserSkillGuarantee existingGuarantee = UserSkillGuarantee.builder()
//                .id(4L)
//                .guarantor(guarantor)
//                .skill(failedSkill)
//                .user(receiver)
//                .build();


//        failedSkill.setGuarantees();

//        RecommendationDto validRecommendationDto = new RecommendationDto();
//        List<SkillOfferDto> validSkillOffersOfActualRecommendation = new ArrayList<>();

//        Skill skill = Skill.builder()
//                .guarantees(new ArrayList<>(List.of(new UserSkillGuarantee()))).build();
//        List<Skill> userSkills = new ArrayList<>();
//        user.setSkills(userSkills);
//        userSkills.add(skill);
//        List<SkillOfferDto> skills = new ArrayList<>();
//        RecommendationDto recommendationDto = new RecommendationDto();
//        recommendationDto.setAuthorId(1L);
//        recommendationDto.setReceiverId(2L);
//        recommendationDto.setSkillOffers(skills);
//        SkillOffer skillOffer1 = SkillOffer.builder()
//                .id(1L)
//                .skill(skill)
//                .build();
//        SkillOffer skillOffer2 = SkillOffer.builder()
//                .id(2L)
//                .skill(skill)
//                .build();
//        Recommendation recommendation = new Recommendation();
//        recommendation.setAuthor(user);
//        recommendation.setReceiver(user);
//        recommendation.setSkillOffers(List.of(new SkillOffer()));
//
//        SkillOfferDto skillOfferDto = SkillOfferDto.builder().id(1L).skillId(1L).build();
//
//        skills.add(skillOfferDto);
//
//        when(skillRepository.countExisting(anyList())).thenReturn(1);
//        when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.of(user))
//                .thenReturn(Optional.of(user));
//        when(skillRepository.findAllById(anyList())).thenReturn(userSkills);
//        when(userSkillGuaranteeRepository.saveAll(anyList())).thenReturn(null);
//        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
//        RecommendationDto result = recommendationService.create(recommendationDto);
//
//        assertNotNull(result);
//        assertEquals(1, result.getAuthorId());
//        assertEquals(1, result.getReceiverId());
//        assertEquals(1, result.getSkillOffers().size());

    @Test
    void testUpdateRecommendation_positive() {

        RecommendationService recommendationService = new RecommendationService();
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto[]{}));
        recommendationDto.setContent("someText");


        Recommendation oldRecommendation = recommendationMapper.toEntity(recommendationDto);
        when(recommendationRepository.findById(recommendationDto.getId())).thenReturn(Optional.ofNullable(oldRecommendation));

        RecommendationUpdateDto recommendationUpdateDto = RecommendationUpdateDto.builder()
                .content("anotherText")
                .id(1L)
                .updatedAt(dateTime)
                .build();

        oldRecommendation.setContent(recommendationUpdateDto.getContent());
        oldRecommendation.setUpdatedAt(recommendationUpdateDto.getUpdatedAt());

        User author = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        Recommendation expectedRecommendation = Recommendation.builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .updatedAt(dateTime)
                .skillOffers(List.of(new SkillOffer[]{}))
                .content("anotherText")
                .build();


        when(recommendationRepository.save(any())).thenReturn(oldRecommendation);

        RecommendationDto result = recommendationService.update(recommendationUpdateDto);
        verify(recommendationRepository.save(oldRecommendation));
        assertEquals(expectedRecommendation, result);

    }
}