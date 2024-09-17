package school.faang.user_service.service;

import org.junit.Assert;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.skill.SkillCandidateDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private SkillMapper skillMapper;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private SkillCandidateMapper skillCandidateMapper;

    @BeforeEach
    void Setup() {
        skillService = new SkillService(skillRepository, skillOfferRepository, userSkillGuaranteeRepository, userRepository, skillMapper, skillCandidateMapper);
    }

    @Nested
    class NegativeTests {
        @Test
        @DisplayName("Ошибка валидации когда название null")
        void whenNullValueThenThrowValidationException() {
            SkillDto skillTest = new SkillDto(1, null);
            assertThrows(DataValidationException.class,
                    () -> skillService.create(skillTest), "Название скила не должно быть пустым");
        }

        @Test
        @DisplayName("Ошибка валидации когда название пустое")
        void whenEmptyValueThenThrowValidationException() {
            SkillDto skillTest = new SkillDto(1, "  ");
            assertThrows(DataValidationException.class,
                    () -> skillService.create(skillTest), "Название скила не должно быть пустым");
        }

        @Test
        @DisplayName("Ошибка когда скилл, который мы хотим создать, уже существует")
        void whenSkillExistThenThrowException() {
            SkillDto skillDto = new SkillDto(1, "Анжуманя");
            Mockito.when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);
            assertThrows(DataValidationException.class,
                    () -> skillService.create(skillDto), "Такой скилл уже существует");
        }

        @Test
        @DisplayName("Ошибка, когда у пользователя нет умений")
        void whenUserHaveNoSkillsThenThrowException() {
            Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(new ArrayList<>());
            Exception exception = Assert.assertThrows(DataValidationException.class,
                    () -> skillService.getUserSkills(123));
            assertEquals("У пользователя нет умений", exception.getMessage());
        }

        @Test
        @DisplayName("Ошибка, когда указан айди несуществующего пользователя")
        void whenUserNotExistThenThrowException() {
            Exception exception = Assert.assertThrows(DataValidationException.class,
                    () -> skillService.getValidUser(123));
            assertEquals("Указанный пользователь не существует", exception.getMessage());
        }

        @Test
        @DisplayName("Ошибка, когда указан айди несуществующего скилла")
        void whenSkillNotExistThenThrowException() {
            Exception exception = Assert.assertThrows(DataValidationException.class,
                    () -> skillService.getValidSkill(123));
            assertEquals("Указанный скилл не существует", exception.getMessage());
        }

        @Test
        @DisplayName("Ошибка, когда у пользователя уже существует скилл")
        void whenUserAlreadyHaveSkillThenThrowException() {
            Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Skill()));
            Mockito.when(skillRepository.findUserSkill(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(new Skill()));
            assertThrows(DataValidationException.class,
                    () -> skillService.acquireSkillFromOffers(1, 123), "У пользователя уже есть данный скилл");
        }

        @Test
        @DisplayName("Ошибка, когда скилл предложен менее 3 раз")
        void whenFewRecommendationsThenThrowException() {
            List<SkillOffer> offers = new ArrayList<>();
            offers.add(new SkillOffer());
            Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Skill()));
            Mockito.when(skillOfferRepository.findAllOffersOfSkill(Mockito.anyLong(), Mockito.anyLong())).thenReturn(offers);
            assertThrows(DataValidationException.class,
                    () -> skillService.acquireSkillFromOffers(1, 123), "Скилл предложен менее 3 раз");
        }

        @Test
        @DisplayName("Ошибка, когда пользователю не предложены скиллы")
        void whenNoSkillsOfferedThenThrowException() {
            assertThrows(DataValidationException.class,
                    () -> skillService.getOfferedSkills(123), "Пользователю не предложены скиллы");
        }
    }

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("Когда скилл создан и вернулся как ДТО ошибки нет")
        void whenSkillCreatedThenSuccess() {
            SkillDto skillDto = new SkillDto(1, "Прес качат");
            Skill skill = new Skill(1, "Прес качат", null, null, null, null, null, null);
            Mockito.when(skillMapper.toEntity(skillDto)).thenReturn(skill);
            Mockito.when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
            Mockito.when(skillRepository.save(skill)).thenReturn(skill);
            Mockito.when(skillMapper.toDto(skill)).thenReturn(skillDto);
            SkillDto result = skillService.create(skillDto);
            assertEquals(skillDto, result);
        }

        @Test
        @DisplayName("Получение скиллов вызывается")
        void whenSkillsFoundThenSuccess() {
            List<Skill> skills = new ArrayList<>();
            skills.add(new Skill());
            Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(skills);
            skillService.getUserSkills(123);
            Mockito.verify(skillRepository, Mockito.times(1)).findAllByUserId(123);
        }

        @Test
        @DisplayName("Сохранение гаранта вызывается 3 раза")
        void whenThreeGuaranteeAndGuaranteeSavedThreeTimesThenSuccess() {
            List<SkillOffer> offers = new ArrayList<>();
            offers.add(new SkillOffer(123, new Skill(), new Recommendation()));
            offers.add(new SkillOffer(123, new Skill(), new Recommendation()));
            offers.add(new SkillOffer(123, new Skill(), new Recommendation()));
            Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Skill()));
            Mockito.when(skillOfferRepository.findAllOffersOfSkill(Mockito.anyLong(), Mockito.anyLong())).thenReturn(offers);
            skillService.acquireSkillFromOffers(1, 123);
            Mockito.verify(userSkillGuaranteeRepository, Mockito.times(3)).save(Mockito.any());
        }

        @Test
        @DisplayName("Скилы преобразованы в List<SkillCandidateDto>")
        void whenListSkillsConvertedToSkillCandidateListThenSuccess() {
            List<SkillDto> skillsDto = new ArrayList<>();
            SkillDto begit = new SkillDto(1, "Бегит");
            skillsDto.add(begit);
            skillsDto.add(begit);
            skillsDto.add(new SkillDto(2, "Анжуманя"));
            skillsDto.add(new SkillDto(3, "Прес качат"));
            List<Skill> skills = new ArrayList<>();
            skills.add(new Skill());
            Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
            Mockito.when(skillRepository.findSkillsOfferedToUser(Mockito.anyLong())).thenReturn(skills);
            List<SkillCandidateDto> resultDto = skillCandidateMapper.toDtoList(skillsDto);
            Mockito.when(skillCandidateMapper.toDtoList(Mockito.anyList())).thenReturn(resultDto);
            List<SkillCandidateDto> result = skillService.getOfferedSkills(1);
            assertEquals(2, result.get(0).getOffersAmount());
            assertEquals(3, result.size());
        }

    }


}
