package school.faang.user_service.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    private static final long ANY_ID = 123L;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private SkillCandidateMapper skillCandidateMapper;

    @BeforeEach
    void setup() {
        skillService = new SkillService(skillRepository, skillOfferRepository, userSkillGuaranteeRepository, userRepository, skillMapper, skillCandidateMapper);
    }

    @Nested
    class NegativeTests {
        @Test
        @DisplayName("Ошибка валидации когда название null")
        void whenNullValueThenThrowValidationException() {
            assertThrows(DataValidationException.class,
                    () -> skillService.create(new SkillDto(ANY_ID, null)), "Название скила не должно быть пустым");
        }

        @Test
        @DisplayName("Ошибка валидации когда название пустое")
        void whenEmptyValueThenThrowValidationException() {
            assertThrows(DataValidationException.class,
                    () -> skillService.create(new SkillDto(ANY_ID, "  ")), "Название скила не должно быть пустым");
        }

        @Test
        @DisplayName("Ошибка при передаче null")
        void whenNullDtoThenThrowValidationException() {
            assertThrows(DataValidationException.class,
                    () -> skillService.create(null), "DTO = null");
        }

        @Test
        @DisplayName("Ошибка когда скилл, который мы хотим создать, уже существует")
        void whenSkillExistThenThrowException() {
            SkillDto skillDto = new SkillDto(ANY_ID, "Анжуманя");
            Mockito.when(skillRepository.existsByTitle(skillDto.getTitle()))
                    .thenReturn(true);
            assertThrows(DataValidationException.class,
                    () -> skillService.create(skillDto), "Такой скилл уже существует");
        }

        @Test
        @DisplayName("Ошибка, когда у пользователя нет умений")
        void whenUserHaveNoSkillsThenThrowException() {
            Mockito.when(userRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong()))
                    .thenReturn(new ArrayList<>());
            assertThrows(DataValidationException.class,
                    () -> skillService.getUserSkills(ANY_ID), "У пользователя нет умений");
        }

        @Test
        @DisplayName("Ошибка, когда указан айди несуществующего пользователя")
        void whenUserNotExistThenThrowException() {
            assertThrows(DataValidationException.class,
                    () -> skillService.getValidUser(ANY_ID), "Указанный пользователь не существует");
        }

        @Test
        @DisplayName("Ошибка, когда указан айди несуществующего скилла")
        void whenSkillNotExistThenThrowException() {
            assertThrows(DataValidationException.class,
                    () -> skillService.getValidSkill(ANY_ID), "Указанный скилл не существует");
        }

        @Test
        @DisplayName("Ошибка, когда у пользователя уже существует скилл")
        void whenUserAlreadyHaveSkillThenThrowException() {
            Mockito.when(userRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(new Skill()));
            Mockito.when(skillRepository.findUserSkill(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.of(new Skill()));
            assertThrows(DataValidationException.class,
                    () -> skillService.acquireSkillFromOffers(ANY_ID, ANY_ID), "У пользователя уже есть данный скилл");
        }

        @Test
        @DisplayName("Ошибка, когда скилл предложен менее 3 раз")
        void whenFewRecommendationsThenThrowException() {
            List<SkillOffer> offers = new ArrayList<>();
            offers.add(new SkillOffer());

            Mockito.when(userRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(new Skill()));
            Mockito.when(skillOfferRepository.findAllOffersOfSkill(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(offers);
            assertThrows(DataValidationException.class,
                    () -> skillService.acquireSkillFromOffers(ANY_ID, ANY_ID), "Скилл предложен менее 3 раз");
        }

        @Test
        @DisplayName("Ошибка, когда пользователю не предложены скиллы")
        void whenNoSkillsOfferedThenThrowException() {
            assertThrows(DataValidationException.class,
                    () -> skillService.getOfferedSkills(ANY_ID), "Пользователю не предложены скиллы");
        }
    }

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("Сохранение скилла вызывается")
        void whenCreatedThenSuccess() {
            skillService.create(new SkillDto(ANY_ID, "Прес качат"));
            Mockito.verify(skillRepository).save(Mockito.any());
        }

        @Test
        @DisplayName("Получение скиллов вызывается")
        void whenSkillsFoundThenSuccess() {
            List<Skill> skills = new ArrayList<>();
            skills.add(new Skill());

            Mockito.when(userRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong()))
                    .thenReturn(skills);
            skillService.getUserSkills(ANY_ID);
            Mockito.verify(skillRepository).findAllByUserId(ANY_ID);
            Mockito.verify(userRepository).findById(ANY_ID);
        }

        @Test
        @DisplayName("Сохранение гаранта вызывается 3 раза")
        void whenThreeGuaranteeAndGuaranteeSavedThreeTimesThenSuccess() {
            List<SkillOffer> offers = new ArrayList<>();
            offers.add(new SkillOffer(ANY_ID, new Skill(), new Recommendation()));
            offers.add(new SkillOffer(ANY_ID, new Skill(), new Recommendation()));
            offers.add(new SkillOffer(ANY_ID, new Skill(), new Recommendation()));

            Mockito.when(userRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(new Skill()));
            Mockito.when(skillOfferRepository.findAllOffersOfSkill(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(offers);
            skillService.acquireSkillFromOffers(ANY_ID, ANY_ID);
            Mockito.verify(userSkillGuaranteeRepository, Mockito.times(3)).save(Mockito.any());
        }

        @Test
        @DisplayName("Запущено преобразование в List<SkillCandidateDto>")
        void whenListSillsMappedThenSuccess() {
            List<Skill> skills = new ArrayList<>();
            skills.add(new Skill());

            Mockito.when(userRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findSkillsOfferedToUser(Mockito.anyLong()))
                    .thenReturn(skills);
            skillService.getOfferedSkills(ANY_ID);
            Mockito.verify(skillCandidateMapper).toDtoList(Mockito.anyList());
        }

    }


}
