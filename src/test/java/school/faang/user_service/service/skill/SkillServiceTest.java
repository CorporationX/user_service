package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static school.faang.user_service.exception.message.ExceptionMessage.NO_SUCH_USER_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    private static final long SKILL_NEW_ID = 0L;
    private static final long SKILL_CREATED_ID = 1L;
    private static final String SKILL_TITLE = "Java";
    private static final long USER_ID = 1L;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private SkillService skillService;

    SkillDto skillDto;
    SkillDto savedSkillDto;
    Skill savedSkill;

    @BeforeEach
    public void init() {
        skillDto = new SkillDto(SKILL_NEW_ID, SKILL_TITLE, null);
        savedSkillDto = new SkillDto(SKILL_CREATED_ID, SKILL_TITLE, null);
        savedSkill = Skill.builder()
                .id(SKILL_CREATED_ID)
                .title(SKILL_TITLE)
                .build();
    }

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("should return user skills when such user exists")
        void shouldReturnUserSkillsWhenSuchUserExists() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
            when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of());
            when(skillMapper.toDtoList(anyList())).thenReturn(List.of());

            assertDoesNotThrow(() -> skillService.getUserSkills(anyLong()));
            verify(skillMapper).toDtoList(anyList());
        }

        @Test
        @DisplayName("Skill should be created successfully")
        public void createSuccess() {
            when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
            when(skillMapper.toEntity(skillDto)).thenReturn(savedSkill);
            when(skillRepository.save(savedSkill)).thenReturn(savedSkill);
            when(skillMapper.toDtoList(savedSkill)).thenReturn(savedSkillDto);

            SkillDto actualResult = skillService.create(skillDto);
            assertEquals(savedSkillDto, actualResult);
        }

        @Test
        @DisplayName("Skill should be acquired from offers when skill does not exist but enough offers")
        public void acquireSkillFromOffersWhenSkillNoExists() {
            User receiver = User.builder().id(USER_ID).build();
            User author1 = new User();
            User author2 = new User();
            User author3 = new User();
            Recommendation recommendation1 = Recommendation.builder().author(author1).receiver(receiver).build();
            Recommendation recommendation2 = Recommendation.builder().author(author2).receiver(receiver).build();
            Recommendation recommendation3 = Recommendation.builder().author(author3).receiver(receiver).build();
            SkillOffer offer1 = new SkillOffer(1, savedSkill, recommendation1);
            SkillOffer offer2 = new SkillOffer(2, savedSkill, recommendation2);
            SkillOffer offer3 = new SkillOffer(3, savedSkill, recommendation3);
            List<SkillOffer> skillOfferList = List.of(offer1, offer2, offer3);

            when(skillOfferRepository.findAllOffersOfSkill(SKILL_CREATED_ID, USER_ID)).thenReturn(skillOfferList);
            when(skillRepository.findUserSkill(SKILL_CREATED_ID, USER_ID)).thenReturn(Optional.of(savedSkill));
            when(skillMapper.toDtoList(savedSkill)).thenReturn(savedSkillDto);

            SkillDto actualResult = skillService.acquireSkillFromOffers(SKILL_CREATED_ID, USER_ID);
            assertEquals(savedSkillDto, actualResult);
        }

        @Test
        @DisplayName("Skill should be acquired from offers when skill exists and enough offers")
        public void acquireSkillFromOffersWhenSkillExists() {
            User receiver = User.builder().id(USER_ID).build();
            User author1 = new User();
            User author2 = new User();
            User author3 = new User();
            Recommendation recommendation1 = Recommendation.builder().author(author1).receiver(receiver).build();
            Recommendation recommendation2 = Recommendation.builder().author(author2).receiver(receiver).build();
            Recommendation recommendation3 = Recommendation.builder().author(author3).receiver(receiver).build();
            SkillOffer offer1 = new SkillOffer(1, savedSkill, recommendation1);
            SkillOffer offer2 = new SkillOffer(2, savedSkill, recommendation2);
            SkillOffer offer3 = new SkillOffer(3, savedSkill, recommendation3);
            List<SkillOffer> skillOfferList = List.of(offer1, offer2, offer3);

            when(skillOfferRepository.findAllOffersOfSkill(SKILL_CREATED_ID, USER_ID)).thenReturn(skillOfferList);
            when(skillRepository.findUserSkill(SKILL_CREATED_ID, USER_ID)).thenReturn(Optional.of(savedSkill));
            when(skillMapper.toDtoList(savedSkill)).thenReturn(savedSkillDto);

            SkillDto actualResult = skillService.acquireSkillFromOffers(SKILL_CREATED_ID, USER_ID);
            assertEquals(savedSkillDto, actualResult);
        }

        @Test
        @DisplayName("Should return empty list when user has no skills")
        public void getAllSkillsByUserIdWhenNoSkills() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(new User()));
            when(skillRepository.findAllByUserId(USER_ID)).thenReturn(List.of());
            when(skillMapper.toDtoList(List.of())).thenReturn(List.of());

            List<SkillDto> actualResult = skillService.getUserSkills(USER_ID);
            assertTrue(actualResult.isEmpty());
        }

        @Test
        @DisplayName("Should return all skills by user id")
        public void getAllSkillsByUserId() {
            List<Skill> skills = List.of(savedSkill);
            List<SkillDto> skillDtoList = List.of(savedSkillDto);

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(new User()));
            when(skillRepository.findAllByUserId(USER_ID)).thenReturn(skills);
            when(skillMapper.toDtoList(skills)).thenReturn(skillDtoList);

            List<SkillDto> actualResult = skillService.getUserSkills(USER_ID);

            assertEquals(1, actualResult.size());
            assertEquals(savedSkillDto, actualResult.get(0));
        }

        @Test
        @DisplayName("Should return empty list when no offered skills found for user")
        public void getAllOfferedSkillsByUserIdWhenNoSkills() {
            when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(List.of());

            List<SkillCandidateDto> actualResult = skillService.getOfferedSkills(USER_ID);
            assertTrue(actualResult.isEmpty());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw exception when such user doesn't exist")
        @Test
        void shouldThrowExceptionWhenSuchUserDoesntExist() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> skillService.getUserSkills(anyLong()));

            verifyNoInteractions(skillRepository);
            assertEquals(NO_SUCH_USER_EXCEPTION.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when creating existing skill")
        public void createExistsSkillWithException() {
            String ERR_MSG = "Skill with title: " + skillDto.getTitle() + " already exists.";
            when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

            DataValidationException exception = assertThrows(DataValidationException.class, () -> skillService.create(skillDto));

            assertEquals(ERR_MSG, exception.getMessage());
        }

        @Test
        @DisplayName("Should return offered skills for user")
        public void getOfferedSkills() {
            List<Skill> skills = List.of(savedSkill);
            Map<Skill, Long> skillCountMap = Map.of(savedSkill, 1L);

            when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(skills);
            when(skillMapper.toDtoList(savedSkill)).thenReturn(savedSkillDto);

            List<SkillCandidateDto> result = skillService.getOfferedSkills(USER_ID);

            assertEquals(1, result.size());
            assertEquals(savedSkillDto, result.get(0).getSkillDto());
            assertEquals(1, result.get(0).getOffersAmount());
        }
        @Test
        @DisplayName("Should throw exception when skill offers are too small")
        public void acquireSkillFromOffersWhenSkillOffersTooSmall() {
            final Long SKILL_CREATED_ID = 1L;
            final Long USER_ID = 2L;
            final String OFFER_ERR_MSG = "Skill with ID: " + SKILL_CREATED_ID + " hasn't enough offers for user with ID: " + USER_ID;

            SkillOffer offer1 = new SkillOffer();
            offer1.setRecommendation(new Recommendation());
            SkillOffer offer2 = new SkillOffer();
            offer2.setRecommendation(new Recommendation());
            List<SkillOffer> skillOfferList = List.of(offer1, offer2);

            when(skillOfferRepository.findAllOffersOfSkill(SKILL_CREATED_ID, USER_ID)).thenReturn(skillOfferList);
            when(skillRepository.findUserSkill(SKILL_CREATED_ID, USER_ID)).thenReturn(Optional.of(savedSkill));

            DataValidationException exception = assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL_CREATED_ID, USER_ID));
            assertThat(exception.getMessage()).isEqualTo(OFFER_ERR_MSG);
        }
    }
}