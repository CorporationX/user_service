package school.faang.user_service.service;

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
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RecommendationMapper recommendationMapper = RecommendationMapper.INSTANCE;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void getAllUserRecommendations_positive() {


    }

    @Test
    public void testGetAllUserRecommendations() {
        Long receiverId = 2L;
        Pageable pageable = PageRequest.of(0, 5);

        User receiver = new User();
        receiver.setId(receiverId);

        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        recommendation1.setContent("recommendation 1");
        recommendation1.setReceiver(receiver);
        recommendation1.setCreatedAt(LocalDateTime.now());
        recommendation1.setUpdatedAt(LocalDateTime.now());

        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        recommendation2.setContent("recommendation 2");
        recommendation2.setReceiver(receiver);
        recommendation2.setCreatedAt(LocalDateTime.now());
        recommendation2.setUpdatedAt(LocalDateTime.now());

        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.add(recommendation1);
        recommendations.add(recommendation2);

        List<RecommendationDto> recommendationDtoList = new ArrayList<>();
        RecommendationDto recommendationDto1 = new RecommendationDto(1L, 1L, 2L, "recommendation 1", List.of(new SkillOfferDto[]{}), LocalDateTime.now());
        RecommendationDto recommendationDto2 = new RecommendationDto(2L, 4L, 2L, "recommendation 2", List.of(new SkillOfferDto[]{}), LocalDateTime.now());
        recommendationDtoList.add(recommendationDto1);
        recommendationDtoList.add(recommendationDto2);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        Page<RecommendationDto> recommendationDtoPage = new PageImpl<>(recommendationDtoList);

        when(recommendationRepository.findAllByReceiverId(receiverId, pageable)).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
        when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

        Page<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiverId, pageable.getPageNumber(), pageable.getPageSize());

        verify(recommendationRepository).findAllByReceiverId(receiverId, pageable);
        verify(recommendationMapper).toDto(recommendation1);
        verify(recommendationMapper).toDto(recommendation2);
        assertEquals(2, recommendationDtoList.size());
        assertEquals(recommendationDtoPage, result);
    }
    @Test
    public void testGetAllGivenRecommendations() {
        Long authorId = 2L;
        Pageable pageable = PageRequest.of(0, 5);

        User author = new User();
        author.setId(authorId);

        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        recommendation1.setContent("recommendation 1");
        recommendation1.setReceiver(author);
        recommendation1.setCreatedAt(LocalDateTime.now());
        recommendation1.setUpdatedAt(LocalDateTime.now());

        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        recommendation2.setContent("recommendation 2");
        recommendation2.setReceiver(author);
        recommendation2.setCreatedAt(LocalDateTime.now());
        recommendation2.setUpdatedAt(LocalDateTime.now());

        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.add(recommendation1);
        recommendations.add(recommendation2);

        List<RecommendationDto> recommendationDtoList = new ArrayList<>();
        RecommendationDto recommendationDto1 = new RecommendationDto(1L, 1L, 2L, "recommendation 1", List.of(new SkillOfferDto[]{}), LocalDateTime.now());
        RecommendationDto recommendationDto2 = new RecommendationDto(2L, 4L, 2L, "recommendation 2", List.of(new SkillOfferDto[]{}), LocalDateTime.now());
        recommendationDtoList.add(recommendationDto1);
        recommendationDtoList.add(recommendationDto2);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        Page<RecommendationDto> recommendationDtoPage = new PageImpl<>(recommendationDtoList);

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(recommendationRepository.findAllByAuthorId(authorId, pageable)).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
        when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

        Page<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId, pageable.getPageNumber(), pageable.getPageSize());

        verify(userRepository).findById(authorId);
        verify(recommendationRepository).findAllByAuthorId(authorId, pageable);
        verify(recommendationMapper).toDto(recommendation1);
        verify(recommendationMapper).toDto(recommendation2);
        assertEquals(2, recommendationDtoList.size());
        assertEquals(recommendationDtoPage, result);
    }





    @Test
    void validateSkillsNotExist() {
        List<SkillOfferDto> skills = new ArrayList<>();
        RecommendationDto recommendationDto = new RecommendationDto();
        SkillOfferDto skillOfferDto = SkillOfferDto.builder().id(1L).build();

        skills.add(skillOfferDto);

        when(skillRepository.existsAllById(anyList())).thenReturn(false);
        recommendationDto.setSkillOffers(skills);
        DataValidationException ex = assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
        assertEquals("list of skills contains not valid skills, please, check this", ex.getMessage());
    }

    @Test
    void createRecommendationTest() {
        User user = User.builder()
                .id(1L)
                .skills(List.of(new Skill())).build();
        Skill skill = Skill.builder()
                .guarantees(new ArrayList<>(List.of(new UserSkillGuarantee()))).build();
        List<Skill> userSkills = new ArrayList<>();
        user.setSkills(userSkills);
        userSkills.add(skill);
        List<SkillOfferDto> skills = new ArrayList<>();
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setSkillOffers(skills);

        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(user);
        recommendation.setReceiver(user);
        recommendation.setSkillOffers(List.of(new SkillOffer()));

        SkillOfferDto skillOfferDto = SkillOfferDto.builder().id(1L).skillId(1L).build();

        skills.add(skillOfferDto);

        when(skillRepository.existsAllById(anyList())).thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.of(user));
        when(skillRepository.findAllById(anyList())).thenReturn(userSkills);
        when(userSkillGuaranteeRepository.saveAll(anyList())).thenReturn(null);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        RecommendationDto result = recommendationService.create(recommendationDto);

        assertNotNull(result);
        assertEquals(1, result.getAuthorId());
        assertEquals(1, result.getReceiverId());
        assertEquals(1, result.getSkillOffers().size());
    }
}