package school.faang.user_service.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validation.SkillValidator;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    private static final long ANY_ID = 123L;
    private final String SKILL_TITLE = "squating";
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

    }


}
