package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationDtoValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationDtoValidator recommendationDtoValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    private static final long ID = 1L;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успешное создание рекомендации")
        public void testCreateWithSaveRecommendation() {
            RecommendationDto recommendationDto = new RecommendationDto();
            recommendationDto.setContent("content");
            recommendationDto.setAuthorId(ID);
            recommendationDto.setReceiverId(ID);
            recommendationDto.setSkillOffers(getSkillOfferDtos());
            recommendationDto.setCreatedAt(LocalDateTime.now());

            Recommendation recommendation = new Recommendation();
            recommendation.setId(ID);
            recommendation.setSkillOffers(new ArrayList<>());

            Skill skill = new Skill();
            skill.setId(ID);
            List<Skill> skills = Arrays.asList(skill);

            SkillOfferDto skillOfferDto = new SkillOfferDto();
            skillOfferDto.setSkillId(ID);

            User receiver = new User();
            receiver.setId(ID);
            receiver.setSkills(skills);
            User author = new User();
            author.setId(ID);
            author.setSkills(skills);

            UserSkillGuarantee guarantee = new UserSkillGuarantee();
            guarantee.setUser(receiver);
            guarantee.setGuarantor(author);
            guarantee.setSkill(skill);

            UserSkillGuarantee existedGuarantee = new UserSkillGuarantee();
            guarantee.setId(ID);
            existedGuarantee.setUser(receiver);
            existedGuarantee.setGuarantor(author);
            existedGuarantee.setSkill(skill);

            List<UserSkillGuarantee> guarantees = new ArrayList<>();
            guarantees.add(guarantee);

            SkillOffer skillOffer = new SkillOffer();
            skillOffer.setId(ID);
            skillOffer.setSkill(skill);

            skill.setGuarantees(guarantees);
            recommendation.getSkillOffers().add(skillOffer);

            when(skillRepository.findById(skillOfferDto.getSkillId())).thenReturn(Optional.of(skill));
            when(userRepository.findById(recommendationDto.getReceiverId())).thenReturn(Optional.of(receiver));
            when(userRepository.findById(recommendationDto.getAuthorId())).thenReturn(Optional.of(author));
            when(recommendationRepository
                    .create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent()))
                    .thenReturn(recommendation.getId());
            when(recommendationRepository.findById(recommendation.getId())).thenReturn(Optional.of(recommendation));
            when(skillOfferRepository.create(skill.getId(), recommendation.getId())).thenReturn(skillOffer.getId());
            when(userSkillGuaranteeRepository.save(existedGuarantee)).thenReturn(guarantee);
            when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));
            when(userSkillGuaranteeRepository
                    .findByReceiverIdAndSkillIdAndAuthorId(receiver.getId(), skill.getId(), author.getId()))
                    .thenReturn(Optional.of(existedGuarantee));
            when(skillRepository.save(skill)).thenReturn(skill);
            when(skillOfferRepository.findById(skillOffer.getId())).thenReturn(Optional.of(skillOffer));
            when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

            RecommendationDto resultRecommendationDto = recommendationService.create(recommendationDto);

            assertEquals(recommendationDto, resultRecommendationDto);
            verify(recommendationDtoValidator).validateIfRecommendationContentIsBlank(recommendationDto);
            verify(recommendationDtoValidator).validateIfRecommendationCreatedTimeIsShort(recommendationDto);
            verify(userRepository, times(2)).findById(ID);
            verify(recommendationRepository)
                    .create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
            verify(recommendationMapper).toDto(recommendation);
        }

        @Test
        @DisplayName("Успешное удаление рекомендации")
        public void testDelete() {
            recommendationService.delete(ID);
            verify(recommendationRepository).deleteById(ID);
        }

        @Test
        @DisplayName("Успешное получение всех рекомендаций пользователя")
        public void testGetAllUserRecommendations() {
            List<Recommendation> recommendations = List.of(new Recommendation(), new Recommendation());
            List<RecommendationDto> recommendationDtos = List.of(new RecommendationDto(), new RecommendationDto());
            when(recommendationRepository.findAllByReceiverId(ID, Pageable.unpaged()))
                    .thenReturn(new PageImpl<>(recommendations, Pageable.unpaged(), recommendations.size()));
            when(recommendationMapper.toDtos(recommendations)).thenReturn(recommendationDtos);

            List<RecommendationDto> resultRecommendationDtos = recommendationService.getAllUserRecommendations(ID);

            assertEquals(recommendationDtos, resultRecommendationDtos);
            verify(recommendationRepository, times(1))
                    .findAllByReceiverId(ID, Pageable.unpaged());
            verify(recommendationMapper, times(1)).toDtos(recommendations);
        }

        @Test
        @DisplayName("Успешное получение всех рекомендации автора")
        public void testGetAllGivenRecommendations() {
            List<Recommendation> recommendations = List.of(new Recommendation(), new Recommendation());
            List<RecommendationDto> recommendationDtos = List.of(new RecommendationDto(), new RecommendationDto());
            when(recommendationRepository.findAllByAuthorId(ID, Pageable.unpaged()))
                    .thenReturn(new PageImpl<>(recommendations, Pageable.unpaged(), recommendations.size()));
            when(recommendationMapper.toDtos(recommendations)).thenReturn(recommendationDtos);

            List<RecommendationDto> resultRecommendationDtos = recommendationService.getAllGivenRecommendations(ID);

            assertEquals(recommendationDtos, resultRecommendationDtos);
            verify(recommendationRepository, times(1))
                    .findAllByAuthorId(ID, Pageable.unpaged());
            verify(recommendationMapper, times(1)).toDtos(recommendations);
        }
    }

    private List<SkillOfferDto> getSkillOfferDtos() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setSkillId(ID);
        return List.of(skillOfferDto);
    }
}
