package school.faang.user_service.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RecommendationServiceImplTest {
    @Autowired
    private RecommendationService recommendationService;
    @MockBean
    private RecommendationRepository recommendationRepository;
    @MockBean
    private SkillRepository skillRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SkillOfferRepository skillOfferRepository;

    private RecommendationDto buildRecommendationDto() {
        return RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("Test recommendation")
                .skillOffers(List.of(SkillOfferDto.builder().skillId(1L).build()))
                .build();
    }

    @Test
    public void testCreateRecommendation() {
        RecommendationDto recommendationDto = buildRecommendationDto();

        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(skillRepository.countExisting(anyList())).thenReturn(1);

        recommendationService.createOrUpdate(recommendationDto);

        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        verify(skillOfferRepository, times(1)).save(any());
    }

    @Test
    public void testUpdateRecommendation() {
        RecommendationDto recommendationDto = buildRecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setContent("Updated recommendation");

        Recommendation existingRecommendation = Recommendation.builder()
                .id(1L)
                .content("Old recommendation")
                .author(new User())
                .receiver(new User())
                .createdAt(LocalDateTime.now())
                .build();

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(existingRecommendation));

        recommendationService.createOrUpdate(recommendationDto);

        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    @Test(expected = DataValidationException.class)
    public void testCreateRecommendationWithInvalidAuthor() {
        RecommendationDto recommendationDto = buildRecommendationDto();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        recommendationService.createOrUpdate(recommendationDto);
    }

    @Test
    public void testCreateRecommendationWithExistingSkill() {
        RecommendationDto recommendationDto = buildRecommendationDto();

        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(skillRepository.countExisting(anyList())).thenReturn(1);

        recommendationService.createOrUpdate(recommendationDto);

        verify(skillOfferRepository, times(1)).save(any());
    }

}