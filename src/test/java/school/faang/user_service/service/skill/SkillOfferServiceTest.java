package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.user.UserSkillGuaranteeService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillOfferServiceTest {

    private static final long ID = 1L;
    @InjectMocks
    private SkillOfferService skillOfferService;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserSkillGuaranteeService userSkillGuaranteeService;
    @Mock
    private SkillService skillService;
    private User author;
    private User receiver;
    private Skill skill;
    private UserSkillGuarantee userSkillGuarantee;
    private RecommendationDto recommendationDto;

    @BeforeEach
    public void init() {
        skill = Skill.builder()
                .id(ID)
                .guarantees(new ArrayList<>(List.of(new UserSkillGuarantee())))
                .build();
        author = User.builder()
                .id(ID)
                .skills(List.of(skill))
                .build();
        receiver = User.builder()
                .id(ID)
                .skills(List.of(skill))
                .build();
        userSkillGuarantee = UserSkillGuarantee.builder()
                .id(ID)
                .skill(skill)
                .user(receiver)
                .guarantor(author)
                .build();
        recommendationDto = RecommendationDto.builder()
                .id(ID)
                .receiverId(ID)
                .authorId(ID)
                .build();
    }

    @Test
    @DisplayName("Успешное добавление скилов и гарантов")
    public void whenAddSkillsWithGuaranteesThenSuccess() {
        when(userService.getUserById(ID)).thenReturn(author);
        when(userSkillGuaranteeService.saveUserSkillGuarantee(skill, receiver, author))
                .thenReturn(userSkillGuarantee);
        when(skillOfferRepository.create(skill.getId(), recommendationDto.getId())).thenReturn(ID);

        skillOfferService.addSkillsWithGuarantees(List.of(skill), recommendationDto.getId(), recommendationDto);

        verify(userService, times(2)).getUserById(ID);
        verify(skillOfferRepository).create(skill.getId(), recommendationDto.getId());
        verify(userSkillGuaranteeService).saveUserSkillGuarantee(skill, receiver, author);
        verify(skillService).saveSkill(skill);
    }

    @Test
    @DisplayName("Успешное удаление рекомендаций")
    public void whenDeleteAllByRecommendationIdThenSuccess() {
        skillOfferService.deleteAllByRecommendationId(ID);

        verify(skillOfferRepository).deleteAllByRecommendationId(ID);
    }
}