package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillDtoSkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillDtoSkillMapper skillMapper;

    @Spy
    private SkillDtoSkillMapper skillMapperSpy;

    @InjectMocks
    private SkillService skillService;

    @Captor
    private ArgumentCaptor<Skill> skillCaptor;

    @Captor ArgumentCaptor<Long> userIdCaptor;

    @Captor
    private ArgumentCaptor<UserSkillGuarantee> userSkillGuaranteeCaptor;


    private SkillOffer skillOffer1;
    private SkillOffer skillOffer2;
    private SkillOffer skillOffer3;
    private SkillOffer skillOffer4;
    private Skill skill;


    @BeforeEach
    void setUp() {
        skill = new Skill(1L, "One", null, null, null, null, null, null);
        Recommendation recommendation1 = Recommendation.builder().receiver(User.builder().id(1L).username("Вася").build()).build();
        Recommendation recommendation2 = Recommendation.builder().receiver(User.builder().id(1L).username("Андрей").build()).build();
        Recommendation recommendation3 = Recommendation.builder().receiver(User.builder().id(1L).username("Саша").build()).build();
        Recommendation recommendation4 = Recommendation.builder().receiver(User.builder().id(1L).username("Рома").build()).build();
        skillOffer1 = new SkillOffer(1L, skill, recommendation1);
        skillOffer2 = new SkillOffer(1L, skill, recommendation2);
        skillOffer3 = new SkillOffer(1L, skill, recommendation3);
        skillOffer4 = new SkillOffer(1L, skill, recommendation4);
    }

    @Test
    public void testCreateSkillWithoutTitle(){
        SkillDto dto = new SkillDto();
        dto.setTitle( "  " );

        assertThrows( DataValidationException.class, () -> skillService.create( dto ) );
    }

    @Test
    public void testCreateSkillWithExistingTitle(){
        SkillDto dto = prepareDate( true );

        assertThrows( DataValidationException.class, () -> skillService.create( dto ) );
    }

    @Test
    public void testCreateSaveSkill(){
        SkillDto dto = prepareDate( false );
        SkillDto result = skillService.create( dto );

        verify(skillRepository, times( 1 )).save( skillCaptor.capture() );
        Skill skill = skillCaptor.getValue();
        assertEquals( dto.getTitle(), skill.getTitle() );
        assertEquals( dto.getTitle(), result.getTitle() );
    }

    private SkillDto prepareDate(boolean existsByTitle){
        SkillDto dto = new SkillDto();
        dto.setTitle( "title" );
        when(skillRepository.existsByTitle( dto.getTitle() )).thenReturn(existsByTitle);
        return dto;
    }


    @Test
    public void testGetUserSkills(){
        long userId = 1L;
        when(skillRepository.findAllByUserId( userId ) ).thenReturn( anyList() );
        skillService.getUserSkills( userId );
        verify( skillRepository, times(1) ).findAllByUserId( userId);
    }


    @Test
    public void testGetOfferedSkills(){
        long userId = 1L;
        when(skillRepository.findSkillsOfferedToUser( userId )).thenReturn( anyList() );
        skillService.getOfferedSkills( userId);
        verify( skillRepository,times( 1 ) ).findSkillsOfferedToUser( userId );
    }


    @Test
    public void testAcquireUserDoesNotExists(){
        when(userRepository.findById( anyLong() )).thenReturn(  Optional.empty()); //TODO: check not empty

        assertThrows( EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(  100L, 1L));
    }

    @Test
    public void testAcquireSkillDoesNotExists(){
        long userId = 1L, skillId = 1L;
        User user = User.builder().id( userId ).build();
        when( userRepository.findById( userId  ) ).thenReturn( Optional.ofNullable( user ) );
        when( skillRepository.findById( skillId ) ).thenReturn( Optional.empty()) ;

        assertThrows( EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(  skillId, userId));
    }

    @Test
    public void testAcquireUserHasThatSkill(){
        long userId = 1L, skillId = 1L;
        User user = User.builder().id( userId ).build();
        Skill skill = new Skill();
        skill.setId(skillId );
        when( userRepository.findById( userId  ) ).thenReturn( Optional.ofNullable( user ) );
        when( skillRepository.findById( skillId ) ).thenReturn( Optional.ofNullable( skill )) ;
        when( skillRepository.findUserSkill( skillId, userId ) ).thenReturn( Optional.ofNullable( skill) );

        assertThrows( DataValidationException.class, () -> skillService.acquireSkillFromOffers(  skillId, userId));
    }
    @Test
    public void testAcquireSkillOfferedLessThanThreeTimes(){
        long userId = 1L, skillId = 1L;
        User user = User.builder().id( userId ).build();
        Skill skill = new Skill();
        skill.setId(skillId );
        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setSkill( skill );
        List<SkillOffer> skillOfferList = new ArrayList<>();
        skillOfferList.add( skillOffer );
        when( userRepository.findById( userId  ) ).thenReturn( Optional.ofNullable( user ) );
        when( skillRepository.findById( skillId ) ).thenReturn( Optional.ofNullable( skill ) ) ;
        when( skillOfferRepository.findAllOffersOfSkill( skillId, userId) ).thenReturn(skillOfferList );

        assertThrows( DataValidationException.class, () -> skillService.acquireSkillFromOffers(  skillId, userId));
    }

    @Test
    public void testAcquireAssignSkillsToUser() {
        long userId = 1L, skillId = 1L;
        User user = User.builder().id( userId ).build();
        Skill skill = Skill.builder().id( skillId ).build();
        SkillOffer skillOffer = new SkillOffer();
        SkillOffer skillOffer2 = new SkillOffer();
        SkillOffer skillOffer3 = new SkillOffer();
        List<SkillOffer> skillOfferList = new ArrayList<>();
        skillOfferList.add( skillOffer );
        skillOfferList.add( skillOffer2 );
        skillOfferList.add( skillOffer3 );
        Recommendation recommendation = Recommendation.builder().id( 1L ).build();
        recommendation.setAuthor( user);
        skillOffer.setRecommendation( recommendation );
        skillOffer2.setRecommendation( recommendation );
        skillOffer3.setRecommendation( recommendation );

        when( userRepository.findById( userId  ) ).thenReturn( Optional.ofNullable( user ) );
        when( skillRepository.findById( skillId ) ).thenReturn( Optional.ofNullable( skill ) ) ;
        when( skillOfferRepository.findAllOffersOfSkill( skillId, userId) ).thenReturn(skillOfferList );
        skillService.acquireSkillFromOffers(skillId, userId);

        verify(skillRepository).assignSkillToUser(skillId, userId);
    }

    @Test
    public void testAcquireSaveUserSkillGuarantees() {
        long userId = 1L, skillId = 1L;

        User user = User.builder().id( userId ).build();

        User author = User.builder().id( 2L ).build();
        User author2 = User.builder().id( 3L).build();
        User author3 = User.builder().id( 4L ).build();

        Skill skill = Skill.builder().id( skillId ).build();
        SkillOffer skillOffer = new SkillOffer();
        SkillOffer skillOffer2 = new SkillOffer();
        SkillOffer skillOffer3 = new SkillOffer();
        skillOffer.setSkill(skill);
        skillOffer2.setSkill(skill);
        skillOffer3.setSkill(skill);

        List<SkillOffer> skillOfferList = new ArrayList<>();
        skillOfferList.add( skillOffer );
        skillOfferList.add( skillOffer2 );
        skillOfferList.add( skillOffer3 );

        Recommendation recommendation = Recommendation.builder().id( 1L ).build();
        Recommendation recommendation2 = Recommendation.builder().id( 2L ).build();
        Recommendation recommendation3 = Recommendation.builder().id( 3L ).build();
        recommendation.setAuthor( author);
        recommendation2.setAuthor( author2);
        recommendation3.setAuthor( author3);

        skillOffer.setRecommendation( recommendation );
        skillOffer2.setRecommendation( recommendation2 );
        skillOffer3.setRecommendation( recommendation3 );


        when( userRepository.findById( userId  ) ).thenReturn( Optional.ofNullable( user ) );
        when( userRepository.findById( author.getId()  ) ).thenReturn( Optional.ofNullable( author ) );
        when( userRepository.findById( author2.getId()  ) ).thenReturn( Optional.ofNullable( author2 ) );
        when( userRepository.findById( author3.getId()  ) ).thenReturn( Optional.ofNullable( author3 ) );
        when( skillRepository.findById( skillId ) ).thenReturn( Optional.ofNullable( skill ) ) ;
        when( skillOfferRepository.findAllOffersOfSkill( skillId, userId) ).thenReturn(skillOfferList );
        skillService.acquireSkillFromOffers(skillId, userId);


        verify( userSkillGuaranteeRepository, times( 3 ) ).save( any(UserSkillGuarantee.class));
    }


}