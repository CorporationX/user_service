package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.UserSkillGuaranteeService;
import school.faang.user_service.validator.skill.SkillOfferValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferService skillOfferService;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private SkillCandidateMapper skillCandidateMapper;
    @Mock
    private UserSkillGuaranteeService userSkillGuaranteeService;
    @Mock
    private UserService userService;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private SkillOfferValidator skillOfferValidator;
    @Mock
    private SkillAcquiredEventPublisher publisher;

    private static final long ANY_ID = 123L;
    private static final String SKILL_TITLE = "squating";
    private static final List<Long> IDS = List.of(1L);
    private static final List<Skill> SKILLS = List.of(new Skill());

    private SkillDto skillDto;
    private List<SkillOffer> skillOfferList;
    private SkillOffer skillOffer;

    @BeforeEach
    public void init() {
        skillDto = SkillDto.builder()
                .id(ANY_ID)
                .title(SKILL_TITLE)
                .build();
        skillOffer = SkillOffer.builder()
                .id(ANY_ID)
                .skill(new Skill())
                .recommendation(new Recommendation())
                .build();

        skillOfferList = List.of(skillOffer, skillOffer, skillOffer);
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Exception when skill with such id doesn't exist")
        void whenSkillNotExistThenThrowException() {
            when(skillRepository.findById(ANY_ID)).thenReturn(null);

            assertThrows(NullPointerException.class,
                    () -> skillService.getSkill(ANY_ID), "Skill with id " + ANY_ID + " doesn't exist");
        }

        @Test
        @DisplayName("Ошибка если List равен null")
        public void whenGetSkillByIdsWithNullThenException() {
            assertThrows(DataValidationException.class, () -> skillService.getSkillByIds(null));
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Verify saving skills")
        void whenCreatedThenSuccess() {
            skillService.create(skillDto);

            verify(skillRepository).save(any());
        }

        @Test
        @DisplayName("Verify finding skills by userId")
        void whenSkillsFoundThenSuccess() {
            skillService.getUserSkills(ANY_ID);

            verify(skillRepository).findAllByUserId(ANY_ID);
        }

        @Test
        @DisplayName("Verify saving skillGuarantees")
        void whenThreeGuaranteeAndGuaranteeSavedThreeTimesThenSuccess() {
            when(skillRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Skill()));
            when(skillOfferService.findAllOffersOfSkill(any(), any()))
                    .thenReturn(skillOfferList);

            skillService.acquireSkillFromOffers(ANY_ID, ANY_ID);

            verify(userSkillGuaranteeService).saveAll(any());
        }

        @Test
        @DisplayName("Verify mapping")
        void whenListSillsMappedThenSuccess() {
            skillService.getOfferedSkills(ANY_ID);

            verify(skillCandidateMapper).toSkillCandidateDtoList(anyList());
        }

        @Test
        @DisplayName("Успех при получении List<Skill>")
        public void whenGetSkillByIdsThenSuccess() {
            when(skillRepository.findByIdIn(IDS)).thenReturn(SKILLS);

            List<Skill> resultSkills = skillService.getSkillByIds(IDS);

            assertNotNull(resultSkills);
            assertEquals(SKILLS, resultSkills);
            verify(skillRepository).findByIdIn(IDS);
        }

        @Test
        @DisplayName("Успех при сохранении skill")
        public void whenSaveSkillThenSuccess() {
            Skill skill = new Skill();

            skillService.saveSkill(skill);

            verify(skillRepository).save(skill);
        }
    }
}