package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.SkillOfferMapperImpl;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    String str = "2023-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

    private RecommendationService recommendationService;
    @Mock
    private SkillChecker checker;
    private Skill systemSkill;
    private Skill failedSkill;
    List<SkillOffer> doubleSkillOffers = new ArrayList<>();
    List<SkillOffer> noSystemSkillOffers = new ArrayList<>();
    List<SkillOffer> receiverSkillOfferToCreate = new ArrayList<>();
    private SkillOffer skillOffer1;
    private SkillOffer skillOffer2;
    private SkillOffer skillOffer3;
    private SkillOffer skillOffer4;


    @BeforeEach
    void setUp() {
        checker = new SkillChecker(skillRepository);
        recommendationService = new RecommendationService(recommendationRepository,
                skillRepository,
                skillOfferRepository,
                userRepository,
                recommendationMapper,
                userSkillGuaranteeRepository,
                checker);

        systemSkill = Skill.builder().id(1L).build();
        failedSkill = Skill.builder().id(148L).build();

        skillOffer1 = SkillOffer
                .builder()
                .id(1L)
                .skill(systemSkill)
                .build();
        skillOffer2 = SkillOffer
                .builder()
                .id(2L)
                .skill(systemSkill)
                .build();
        skillOffer3 = SkillOffer
                .builder()
                .id(3L)
                .skill(systemSkill)
                .build();
        skillOffer4 = SkillOffer
                .builder()
                .id(4L)
                .skill(failedSkill)
                .build();

        doubleSkillOffers.add(skillOffer1);
        doubleSkillOffers.add(skillOffer2);
        noSystemSkillOffers.add(skillOffer1);
        noSystemSkillOffers.add(skillOffer4);
    }

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

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(badRecommendationDto));
        assertEquals("list of skills contains not unique skills, please, check this", ex.getMessage());
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
        assertEquals("list of skills contains not valid skills, please, check this", ex.getMessage());
    }

    @Test
    void testCreateRecommendation_SkillExists_Positive() {
        receiverSkillOfferToCreate.add(skillOffer4);

        RecommendationDto validRecommendationDto = new RecommendationDto();
        validRecommendationDto.setId(1L);
        validRecommendationDto.setSkillOffers(recommendationMapper.toListOfDto(receiverSkillOfferToCreate));
        validRecommendationDto.setAuthorId(1L);
        validRecommendationDto.setReceiverId(2L);
        validRecommendationDto.setContent("anyText");

        User actualGuarantor = User.builder()
                .id(1L)
                .build();

        User receiver = User.builder()
                .id(2L)
                .skills(List.of(failedSkill)).build();
        User guarantorToFailedSkill = User.builder().id(7L).build();

        when(recommendationRepository.create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent()))
                .thenReturn(1L);

        Recommendation entity = recommendationMapper.toEntity(validRecommendationDto);
        entity.setAuthor(actualGuarantor);
        entity.setReceiver(receiver);

        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(Optional.of(entity));
        when(skillRepository.countExisting(anyList())).thenReturn(1);

        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        guarantees.add(UserSkillGuarantee.builder()
                .guarantor(guarantorToFailedSkill).build());
        failedSkill.setGuarantees(guarantees);

        when(skillRepository.findAllById(anyList())).thenReturn(List.of(failedSkill));
        when(skillOfferRepository.findAllById(anyList())).thenReturn(receiverSkillOfferToCreate);

        recommendationService.create(validRecommendationDto);
        verify(recommendationRepository).create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent());
        verify(recommendationRepository).findById(anyLong());
        verify(userSkillGuaranteeRepository).saveAll(anyList());
        verify(skillOfferRepository).create(148L, 1L);
        verify(recommendationRepository).save(entity);
    }

    @Test
    void testCreateRecommendation_SkillOfferExists_Positive() {
        receiverSkillOfferToCreate.add(skillOffer1);

        RecommendationDto validRecommendationDto = new RecommendationDto();
        validRecommendationDto.setId(1L);
        validRecommendationDto.setSkillOffers(recommendationMapper.toListOfDto(receiverSkillOfferToCreate));
        validRecommendationDto.setAuthorId(1L);
        validRecommendationDto.setReceiverId(2L);
        validRecommendationDto.setContent("anyText");

        User actualGuarantor = User.builder()
                .id(1L)
                .build();

        User receiver = User.builder()
                .id(2L)
                .build();
        User firstGuarantor = User.builder().id(5L).build();
        User secondGuarantor = User.builder().id(6L).build();

        List<User> authors = new ArrayList<>();
        authors.add(firstGuarantor);
        authors.add(secondGuarantor);

        when(recommendationRepository.create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent()))
                .thenReturn(1L);

        Recommendation entity = recommendationMapper.toEntity(validRecommendationDto);
        entity.setAuthor(actualGuarantor);
        receiver.setSkills(new ArrayList<>());
        entity.setReceiver(receiver);
        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(Optional.of(entity));

        when(skillRepository.countExisting(anyList())).thenReturn(1);

        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        authors.forEach(o -> guarantees.add(UserSkillGuarantee.builder()
                .skill(Skill.builder().id(1L).build())
                .guarantor(o)
                .user(receiver)
                .build()));

        guarantees.add(UserSkillGuarantee.builder()
                .skill(Skill.builder().id(1L).build())
                .guarantor(actualGuarantor)
                .user(receiver)
                .build());

        when(skillOfferRepository.findAllAuthorsBySkillIdAndReceiverId(1L, receiver.getId()))
                .thenReturn(authors);
        when(skillRepository.findAllById(anyList())).thenReturn(List.of(systemSkill));
        when(skillOfferRepository.findAllById(anyList())).thenReturn(receiverSkillOfferToCreate);

        recommendationService.create(validRecommendationDto);
        verify(recommendationRepository).create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent());

        verify(recommendationRepository).findById(anyLong());
        verify(skillRepository).assignSkillToUser(1L, receiver.getId());
        verify(userSkillGuaranteeRepository).saveAll(guarantees);
        verify(skillOfferRepository).create(1L, 1L);
        verify(recommendationRepository).save(entity);

    }

    @Test
    void testUpdateRecommendation_positive() {
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

        when(recommendationRepository.save(any())).thenReturn(expectedRecommendation);

        RecommendationDto result = recommendationService.update(recommendationUpdateDto);
        verify(recommendationRepository).save(oldRecommendation);
        assertEquals(recommendationMapper.toDto(expectedRecommendation), result);
    }
}