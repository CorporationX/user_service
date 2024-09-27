package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {
    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationValidator validator;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationServiceImpl recommendationServiceImpl;


    @Test
    void testSuccessfullyCreate() {

        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .content("23")
                .build();

        User author = new User();
        author.setId(1L);
        author.setSkills(new ArrayList<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));

        User receiver = new User();
        receiver.setId(2L);
        receiver.setSkills(new ArrayList<>());
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        List<SkillOfferDto> skillOffersDto = List.of(
                new SkillOfferDto(),
                new SkillOfferDto());
        List<Skill> returnedSkills = List.of(
                new Skill(),
                new Skill());

        when(skillRepository.findAllById(any())).thenReturn(returnedSkills);

        recommendationServiceImpl.saveNewSkills(author, receiver, skillOffersDto);

        recommendationServiceImpl.create(recommendationDto);

        verify(validator, times(1)).validateAuthorAndReceiver(recommendationDto, author, receiver);
        verify(recommendationRepository, times(1)).create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        RecommendationServiceImpl spyService = spy(recommendationServiceImpl);
        spyService.create(recommendationDto);
        verify(spyService, times(1)).processSkillsAndGuarantees(recommendationDto);
    }

    @Test
    void updateRecommendation_success() {
        RecommendationDto updatedDto = RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .content("Updated content")
                .build();

        User author = new User();
        author.setId(1L);
        author.setSkills(new ArrayList<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));

        User receiver = new User();
        receiver.setId(2L);
        receiver.setSkills(new ArrayList<>());
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        List<SkillOfferDto> skillOffersDto = List.of(
                new SkillOfferDto(),
                new SkillOfferDto());
        List<Skill> returnedSkills = List.of(
                new Skill(),
                new Skill());

        when(skillRepository.findAllById(any())).thenReturn(returnedSkills);

        recommendationServiceImpl.saveNewSkills(author, receiver, skillOffersDto);

        recommendationServiceImpl.updateRecommendation(updatedDto);

        verify(validator).validateAuthorAndReceiver(updatedDto, author, receiver);
        verify(skillOfferRepository).deleteAllByRecommendationId(1);
        verify(recommendationRepository).update(1, 2, "Updated content");
    }

    @Test
    void deleteRecommendation_success() {
        Optional<Recommendation> recommendationToDelete = Optional.ofNullable(Recommendation.builder()
                .id(1L)
                .content("content")
                .build());

        long recommendationId = 1;
        when(recommendationRepository.findById(recommendationId)).thenReturn(recommendationToDelete);


        recommendationServiceImpl.delete(recommendationId);

        verify(validator).checkIfRecommendationNotExist(recommendationId);
        verify(recommendationRepository).deleteById(recommendationId);
    }

    @Test
    void findUserByUserId_notFound() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationServiceImpl.findUserByUserId(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void testSaveNewSkills() {

        List<SkillOfferDto> skillOffersDto = List.of(new SkillOfferDto(), new SkillOfferDto());
        List<Skill> returnedSkills = List.of(new Skill(), new Skill());

        User author = new User();
        author.setId(1L);
        author.setSkills(new ArrayList<>());

        User receiver = new User();
        receiver.setId(2L);
        receiver.setSkills(new ArrayList<>());

        when(skillRepository.findAllById(any())).thenReturn(returnedSkills);

        recommendationServiceImpl.saveNewSkills(author, receiver, skillOffersDto);

        verify(skillRepository, times(1)).findAllById(any());
        verify(userRepository, times(1)).save(receiver);
        assertEquals(receiver.getSkills().size(), 2);
    }

    @Test
    void getAllUserRecommendations_success() {
        Long receiverId = 1L;
        List<Recommendation> recommendations = List.of(new Recommendation());
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        when(recommendationRepository.findAllByReceiverId(eq(receiverId), any())).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(any())).thenReturn(new RecommendationDto());

        List<RecommendationDto> result = recommendationServiceImpl.getAllUserRecommendations(receiverId);

        verify(recommendationRepository).findAllByReceiverId(eq(receiverId), any());
        verify(recommendationMapper, times(recommendations.size())).toDto(any());
        assertEquals(1, result.size());
    }

    @Test
    void getAllGivenRecommendations_success() {
        Long authorId = 1L;
        List<Recommendation> recommendations = List.of(new Recommendation());
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        when(recommendationRepository.findAllByAuthorId(eq(authorId), any())).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(any())).thenReturn(new RecommendationDto());

        List<RecommendationDto> result = recommendationServiceImpl.getAllGivenRecommendations(authorId);

        verify(recommendationRepository).findAllByAuthorId(eq(authorId), any());
        verify(recommendationMapper, times(recommendations.size())).toDto(any());
        assertEquals(1, result.size());
    }

    @Test
    void findUserByUserId_success() {
        long userId = 1;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = recommendationServiceImpl.findUserByUserId(userId);

        assertEquals(user, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void processSkillsAndGuarantees_success() {
        User author = new User();
        author.setId(1L);
        author.setSkills(new ArrayList<>());

        User receiver = new User();
        receiver.setId(2L);
        receiver.setSkills(new ArrayList<>());

        Skill existingSkill = new Skill();
        existingSkill.setId(3L);

        List<Skill> existingSkills = List.of(existingSkill);

        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setSkillId(3L);
        skillOfferDto.setRecommendationId(5L);

        List<SkillOfferDto> skillOffers = new ArrayList<>(List.of(skillOfferDto));

        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setSkillOffers(skillOffers);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(skillRepository.findAllByUserId(2L)).thenReturn(existingSkills);

        recommendationServiceImpl.processSkillsAndGuarantees(recommendationDto);

        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(skillRepository).findAllByUserId(2L);
    }


    @Test
    void saveSkillOffers_success() {

        SkillOfferDto skillOfferDto1 = SkillOfferDto.builder()
                .skillId(1L)
                .recommendationId(2L)
                .build();
        SkillOfferDto skillOfferDto2 = SkillOfferDto.builder()
                .skillId(2L)
                .recommendationId(1L)
                .build();

        List<SkillOfferDto> skillOffersDto = List.of(
                skillOfferDto1,
                skillOfferDto2);

        recommendationServiceImpl.saveSkillOffers(skillOffersDto);
        verify(skillOfferRepository, times(2)).create(anyLong(), anyLong());
    }
}