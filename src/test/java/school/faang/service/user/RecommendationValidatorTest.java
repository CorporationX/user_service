package school.faang.service.user;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.service.user.entity.Skill;
import school.faang.service.user.entity.User;
import school.faang.service.user.entity.recommendation.Recommendation;
import school.faang.service.user.entity.recommendation.SkillOffer;
import school.faang.service.user.exception.DataValidationException;
import school.faang.service.user.repository.SkillRepository;
import school.faang.service.user.repository.UserRepository;
import school.faang.service.user.utils.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    private Recommendation createRecommendation(
            Long authorId,
            Long receiverId,
            String content,
            List<Long> skillOffersIds,
            LocalDateTime createdAt
    ) {
        Recommendation recommendation = new Recommendation();

        User receiver = new User();
        if (receiverId != null) {
            receiver.setId(receiverId);
        }

        User author = new User();
        if (authorId != null) {
            author.setId(authorId);
        }
        author.setRecommendationsGiven(new ArrayList<>());

        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setContent(content);
        recommendation.setCreatedAt(createdAt);

        List<SkillOffer> skillOffers = new ArrayList<>();

        if (skillOffersIds != null) {
            skillOffersIds.forEach((skillId) -> {
                SkillOffer skillOffer = new SkillOffer();
                Skill skill = new Skill();

                skill.setId(skillId);

                skillOffer.setSkill(skill);

                skillOffers.add(skillOffer);
            });
        }

        recommendation.setSkillOffers(skillOffers);

        return recommendation;
    }

    @Test
    public void testBasicValidation() {
        Assert.assertThrows("Recommendation is null", DataValidationException.class, () -> {
            recommendationValidator.validate(null);
        });

        Recommendation emptyContentRecommendation = createRecommendation(1L, 2L, "     ", List.of(1L), LocalDateTime.now());

        Assert.assertThrows("Recommendation has empty content", DataValidationException.class, () -> {
            recommendationValidator.validate(emptyContentRecommendation);
        });

        Recommendation noSkillsRecommendation = createRecommendation(1L, 2L, "Content", null, LocalDateTime.now());

        Assert.assertThrows("Recommendation skills is null", DataValidationException.class, () -> {
            recommendationValidator.validate(noSkillsRecommendation);
        });

        Recommendation noAuthorIdRecommendation = createRecommendation(null, 2L, "Content", List.of(1L), LocalDateTime.now());

        Assert.assertThrows("Recommendation author id is null", DataValidationException.class, () -> {
            recommendationValidator.validate(noAuthorIdRecommendation);
        });

        Recommendation noReceiverIdRecommendation = createRecommendation(1L, null, "Content", List.of(1L), LocalDateTime.now());

        Assert.assertThrows("Recommendation receiver id is null", DataValidationException.class, () -> {
            recommendationValidator.validate(noReceiverIdRecommendation);
        });

        Recommendation noCreatedAtRecommendation = createRecommendation(1L, 2L, "Content", List.of(1L), null);

        Assert.assertThrows("Recommendation createdAt is null", DataValidationException.class, () -> {
            recommendationValidator.validate(noCreatedAtRecommendation);
        });
    }

    @Test
    public void testAuthorValidation() {
        Recommendation recommendation = createRecommendation(1L, 2L, "Content", List.of(1L), LocalDateTime.now());

        User receiver2 = new User();
        receiver2.setId(3L);

        Recommendation recommendationGiven1 = new Recommendation();
        recommendationGiven1.setReceiver(recommendation.getReceiver());
        recommendationGiven1.setCreatedAt(LocalDateTime.now().minusMonths(3));

        Recommendation recommendationGiven2 = new Recommendation();
        recommendationGiven2.setReceiver(receiver2);
        recommendationGiven2.setCreatedAt(LocalDateTime.now().minusMonths(7));

        recommendation.getAuthor().setRecommendationsGiven(List.of(recommendationGiven1, recommendationGiven2));

        Mockito.when(userRepository.findById(recommendation.getAuthor().getId())).thenReturn(null);

        Assert.assertThrows("No author in DB", DataValidationException.class, () -> {
            recommendationValidator.validate(recommendation);
        });

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.findById(recommendation.getAuthor().getId())).thenReturn(Optional.of(recommendation.getAuthor()));

        Assert.assertThrows("Author gave a recommendation to this receiver less than 6 months ago", DataValidationException.class, () -> {
            recommendationValidator.validate(recommendation);
        });
    }

    @Test
    public void testReceiverValidation() {
        Recommendation recommendation = createRecommendation(1L, 2L, "Content", List.of(1L), LocalDateTime.now());

        Mockito.when(userRepository.existsById(recommendation.getReceiver().getId())).thenReturn(false);

        Assert.assertThrows("No receiver in DB", DataValidationException.class, () -> {
            recommendationValidator.validate(recommendation);
        });
    }

    @Test
    public void testSkillOffersValidation() {
        Recommendation recommendation = createRecommendation(1L, 2L, "Content", List.of(1L), LocalDateTime.now());

        Mockito.when(skillRepository.existsById(Mockito.anyLong())).thenReturn(false);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.findById(recommendation.getAuthor().getId())).thenReturn(Optional.of(recommendation.getAuthor()));

        Assert.assertThrows("No skill id in DB", DataValidationException.class, () -> {
            recommendationValidator.validate(recommendation);
        });
    }
}
