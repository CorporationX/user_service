package school.faang.user_service.service;

import org.junit.Assert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    @BeforeEach
    void Setup() {
        skillService = new SkillService(skillRepository, skillMapper, skillOfferRepository, userSkillGuaranteeRepository, userRepository);
    }

    @Test
    void testNullSkillTitle() { //У скилла название null
        SkillDto skillTest = new SkillDto(1, null);
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> skillService.create(skillTest));
        Assert.assertEquals("Название скила не должно быть пустым", exception.getMessage());
    }

    @Test
    void testEmptySkillTitle() { //У скилла пустое название
        SkillDto skillTest = new SkillDto(1, "  ");
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> skillService.create(skillTest));
        Assert.assertEquals("Название скила не должно быть пустым", exception.getMessage());
    }

    @Test
    void testSkillExist() { //Скилл уже существует
        SkillDto skillDto = new SkillDto(1, "Анжуманя");
        Mockito.when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));
        Assert.assertEquals("Такой скилл уже существует", exception.getMessage());
    }

    @Test
    void testCreate() { //Проверяем что скидд создается и возвращается как ДТО
        SkillDto skillDto = new SkillDto(1, "Прес качат");
        Skill skill = new Skill(1, "Прес качат", null, null, null, null, null, null);
        Mockito.when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        Mockito.when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        Mockito.when(skillRepository.save(skill)).thenReturn(skill);
        Mockito.when(skillMapper.toDto(skill)).thenReturn(skillDto);
        SkillDto result = skillService.create(skillDto);
        Assert.assertEquals(skillDto, result);
    }

    @Test
    void testWrongUser() {
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> skillService.getUserSkills(123));
        Assert.assertEquals("Указан несуществующий пользователь", exception.getMessage());
    }

    @Test
    void testNoSkills() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> skillService.getUserSkills(123));
        Assert.assertEquals("У пользователя нет умений", exception.getMessage());
    }

    @Test
    void testGetUserSkills() { //Получение скиллов вызывается
        List<Skill> skills = new ArrayList<>();
        skills.add(new Skill());
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(skills);
        skillService.getUserSkills(123);
        Mockito.verify(skillRepository, Mockito.times(1)).findAllByUserId(123);
    }

    @Test
    void testWrongUserFromOffers() {
        Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(new Skill()));
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(1, 123));
        Assert.assertEquals("Указанный пользователь не существует", exception.getMessage());
    }

    @Test
    void testWrongSkillFromOffers() {
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(1, 123));
        Assert.assertEquals("Указанный скилл не существует", exception.getMessage());
    }

    @Test
    void testExistSkill() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Skill()));
        Mockito.when(skillRepository.findUserSkill(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(new Skill()));
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(1, 123));
        Assert.assertEquals("У пользователя уже есть данный скилл", exception.getMessage());
    }

    @Test
    void testFewRecommendations() {
        List<SkillOffer> offers = new ArrayList<>();
        offers.add(new SkillOffer());
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Skill()));
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(Mockito.anyLong(), Mockito.anyLong())).thenReturn(offers);
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(1, 123));
        Assert.assertEquals("Скилл предложен менее 3 раз", exception.getMessage());
    }

    @Test
    void testNumberGarantee() { //Тест колличества вызовов создания гарантов
        List<SkillOffer> offers = new ArrayList<>();
        offers.add(new SkillOffer(123, new Skill(), new Recommendation()));     //(long)123,"Author","","","",true,""
        offers.add(new SkillOffer(123, new Skill(), new Recommendation()));
        offers.add(new SkillOffer(123, new Skill(), new Recommendation()));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Skill()));
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(Mockito.anyLong(), Mockito.anyLong())).thenReturn(offers);
        skillService.acquireSkillFromOffers(1, 123);
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(3)).save(Mockito.any());
    }

}
