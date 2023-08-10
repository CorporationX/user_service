package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.exception.RecommendationPeriodIsNotCorrect;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.SkillOfferMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @InjectMocks
    private RecommendationService recommendationService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private static final int RECOMMENDATION_PERIOD_IN_MONTH = 6;

    String str = "2023-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

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
        recommendationService = new RecommendationService(recommendationRepository,
                skillRepository,
                userRepository,
                recommendationMapper,
                userSkillGuaranteeRepository,
                skillOfferRepository);

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

        doubleSkillOffers.addAll(List.of(skillOffer1, skillOffer2, skillOffer1, skillOffer4));
        noSystemSkillOffers.addAll(List.of(skillOffer1, skillOffer4));
    }

    @Test
    void testCreateRecommendation_notValidRecommendationPeriod() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .content("anyText")
                .authorId(1L)
                .receiverId(2L)
                .build();

        Recommendation recommendation = new Recommendation();
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(RECOMMENDATION_PERIOD_IN_MONTH - 1));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId())
        )
                .thenReturn(java.util.Optional.of(recommendation));

        RecommendationPeriodIsNotCorrect ex = assertThrows(RecommendationPeriodIsNotCorrect.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("Date of new recommendation should be after "
                + RECOMMENDATION_PERIOD_IN_MONTH
                + " months of the last recommendation", ex.getMessage());
    }

    @Test
    @DisplayName("SkillChecker - the same skill in offered skills")
    void testCreateRecommendation_NotUniqueSkillsInOfferedSkills() {
        RecommendationDto badRecommendationDto = RecommendationDto
                .builder()
                .id(1L)
                .skillOffers(skillOfferMapper.toDto(doubleSkillOffers))
                .authorId(1L)
                .receiverId(2L)
                .content("anyText")
                .build();

        when(recommendationRepository.create(
                badRecommendationDto.getAuthorId(),
                badRecommendationDto.getReceiverId(),
                badRecommendationDto.getContent()))
                .thenReturn(1L);
        Optional<Recommendation> entity = Optional.of(recommendationMapper.toEntity(badRecommendationDto));
        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(entity);

        assertThrows(DataValidException.class,
                () -> recommendationService.create(badRecommendationDto));
    }

    @Test
    @DisplayName("SkillChecker - unknown skill in offered skills")
    void testCreateRecommendation_NoSystemSkillsInOfferedSkills() {
        RecommendationDto validRecommendationDto = RecommendationDto
                .builder()
                .id(1L)
                .skillOffers(skillOfferMapper.toDto(noSystemSkillOffers))
                .authorId(1L)
                .receiverId(2L)
                .content("anyText")
                .build();

        when(recommendationRepository.create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent()))
                .thenReturn(1L);

        Recommendation entity = Recommendation
                .builder()
                .receiver(User.builder()
                        .id(2L)
                        .build())
                .author(User.builder()
                        .id(1L)
                        .build())
                .skillOffers(noSystemSkillOffers)
                .build();
        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(Optional.of(entity));

        when(skillRepository.countExisting(anyList())).thenReturn(1);

        assertThrows(DataValidException.class, () -> recommendationService.create(validRecommendationDto));
    }

    @Test
    void testCreateRecommendation_SaveValidRecommendationAndAddNewGuarantee() {
        receiverSkillOfferToCreate.add(skillOffer4);

        RecommendationDto validRecommendationDto = RecommendationDto
                .builder()
                .id(1L)
                .skillOffers(skillOfferMapper.toDto(receiverSkillOfferToCreate))
                .authorId(1L)
                .receiverId(2L)
                .content("anyText")
                .build();

        User actualGuarantor = User
                .builder()
                .id(1L)
                .build();

        User receiver = User
                .builder()
                .id(2L)
                .skills(List.of(failedSkill))
                .build();

        User guarantorToFailedSkill = User
                .builder()
                .id(7L)
                .build();

        when(recommendationRepository.create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent()))
                .thenReturn(1L);

        Recommendation entity = Recommendation
                .builder()
                .author(actualGuarantor)
                .receiver(receiver)
                .build();

        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(Optional.of(entity));
        when(skillRepository.countExisting(anyList())).thenReturn(1);

        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        guarantees.add(UserSkillGuarantee
                .builder()
                .guarantor(guarantorToFailedSkill)
                .build());
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
    void testCreateRecommendation_SaveValidRecommendationAndSaveNewSkillToUser() {
        receiverSkillOfferToCreate.add(skillOffer1);

        RecommendationDto validRecommendationDto = RecommendationDto
                .builder()
                .id(1L)
                .skillOffers(skillOfferMapper.toDto(receiverSkillOfferToCreate))
                .authorId(1L)
                .receiverId(2L)
                .content("anyText")
                .build();

        User actualGuarantor = User
                .builder()
                .id(1L)
                .build();

        User receiver = User
                .builder()
                .id(2L)
                .skills(new ArrayList<>())
                .build();
        User firstGuarantor = User
                .builder()
                .id(5L)
                .build();
        User secondGuarantor = User
                .builder()
                .id(6L)
                .build();

        List<User> authors = List.of(firstGuarantor, secondGuarantor);

        when(recommendationRepository.create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent()))
                .thenReturn(1L);

        Recommendation entity = Recommendation
                .builder()
                .author(actualGuarantor)
                .receiver(receiver)
                .build();

        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(Optional.of(entity));

        when(skillRepository.countExisting(anyList())).thenReturn(1);

        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        authors.forEach(o -> guarantees.add(UserSkillGuarantee
                .builder()
                .skill(Skill
                        .builder()
                        .id(1L)
                        .build())
                .guarantor(o)
                .user(receiver)
                .build()));

        guarantees.add(UserSkillGuarantee
                .builder()
                .skill(Skill
                        .builder()
                        .id(1L)
                        .build())
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
    public void testGetAllReceiverRecommendations() {
        Long receiverId = 2L;
        LocalDateTime createdAt = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 5);

        User receiver = new User();
        receiver.setId(receiverId);

        Recommendation recommendation1 = Recommendation
                .builder()
                .id(1L)
                .content("recommendation 1")
                .author(User
                        .builder()
                        .id(1L)
                        .build())
                .receiver(receiver)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        Recommendation recommendation2 = Recommendation
                .builder()
                .id(2L)
                .content("recommendation 2")
                .author(User
                        .builder()
                        .id(4L)
                        .build())
                .receiver(receiver)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.addAll(List.of(recommendation1, recommendation2));

        List<RecommendationDto> recommendationDtoList = new ArrayList<>();
        RecommendationDto recommendationDto1 = new RecommendationDto(1L, 1L, receiverId, "recommendation 1", null, createdAt);
        RecommendationDto recommendationDto2 = new RecommendationDto(2L, 4L, receiverId, "recommendation 2", null, createdAt);
        recommendationDtoList.addAll(List.of(recommendationDto1, recommendationDto2));
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        Page<RecommendationDto> recommendationDtoPage = new PageImpl<>(recommendationDtoList);

        when(recommendationRepository.findAllByReceiverId(receiverId, pageable)).thenReturn(recommendationPage);

        Page<RecommendationDto> result = recommendationService.getAllReceiverRecommendations(
                receiverId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        verify(recommendationRepository).findAllByReceiverId(receiverId, pageable);
        verify(recommendationMapper).toDto(recommendation1);
        verify(recommendationMapper).toDto(recommendation2);
        assertEquals(2, recommendationDtoList.size());
        assertEquals(recommendationDtoPage.getContent(), result.getContent());
    }

    @Test
    public void testGetAllAuthorRecommendations() {
        Long authorId = 2L;
        LocalDateTime createdAt = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 5);

        User author = new User();
        author.setId(authorId);

        Recommendation recommendation1 = Recommendation
                .builder()
                .id(1L)
                .content("recommendation 1")
                .author(author)
                .receiver(User
                        .builder()
                        .id(1L)
                        .build())
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        Recommendation recommendation2 = Recommendation
                .builder()
                .id(2L)
                .content("recommendation 2")
                .author(author)
                .receiver(User
                        .builder()
                        .id(4L)
                        .build())
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();


        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.addAll(List.of(recommendation1, recommendation2));

        List<RecommendationDto> recommendationDtoList = new ArrayList<>();

        RecommendationDto recommendationDto1 = new RecommendationDto(1L, authorId, 1L, "recommendation 1", null, createdAt);
        RecommendationDto recommendationDto2 = new RecommendationDto(2L, authorId, 4L, "recommendation 2", null, createdAt);
        recommendationDtoList.addAll(List.of(recommendationDto1, recommendationDto2));
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        Page<RecommendationDto> recommendationDtoPage = new PageImpl<>(recommendationDtoList);

        when(recommendationRepository.findAllByAuthorId(authorId, pageable)).thenReturn(recommendationPage);

        Page<RecommendationDto> result = recommendationService.getAllAuthorRecommendations(
                authorId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        verify(recommendationRepository).findAllByAuthorId(authorId, pageable);
        verify(recommendationMapper).toDto(recommendation1);
        verify(recommendationMapper).toDto(recommendation2);
        assertEquals(2, recommendationDtoList.size());
        assertEquals(recommendationDtoPage.getContent(), result.getContent());
    }

    @Test
    public void testDelete() {
        Long recommendationId = 1L;
        doNothing().when(recommendationRepository).deleteById(recommendationId);
        recommendationService.delete(recommendationId);
        verify(recommendationRepository, times(1)).deleteById(recommendationId);
    }
}