package school.faang.user_service.dto;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFilterDtoTest {
    UserFilterDto filter = new UserFilterDto();
    List<User> expectedMatches = new ArrayList<>();
    List<User> allUsers = new ArrayList<>();

    @BeforeEach
    void init() {
        var nadir = new User();
        nadir.setId(1L);
        nadir.setUsername("nadir");
        nadir.setAboutMe("About nadir");
        nadir.setEmail("nadir@gmail.com");

        var contactA = new Contact();
        contactA.setContact("Cianid");
        var contactB = new Contact();
        contactB.setContact("Zenith");
        nadir.setContacts(List.of(contactA, contactB));

        nadir.setCountry(new Country(1L, "Russia", new ArrayList<>()));
        nadir.setCity("Moscow");
        nadir.setPhone("12345");

        var skillA = new Skill();
        skillA.setTitle("SQL");
        var skillB = new Skill();
        skillB.setTitle("Java");
        nadir.setSkills(List.of(skillA, skillB));

        nadir.setExperience(4);

        allUsers.add(nadir);


        var cianid = new User();
        cianid.setId(2L);
        cianid.setUsername("cianid");
        cianid.setAboutMe("About cianid");
        cianid.setEmail("cianid@gmail.com");

        var contactC = new Contact();
        contactC.setContact("Nadir");
        var contactD = new Contact();
        contactD.setContact("Zenith");
        cianid.setContacts(List.of(contactC, contactD));

        cianid.setCountry(new Country(2L, "UK", new ArrayList<>()));
        cianid.setCity("London");
        cianid.setPhone("98765");

        var skillC = new Skill();
        skillC.setTitle("SQL");
        var skillD = new Skill();
        skillD.setTitle("Postgres");
        cianid.setSkills(List.of(skillC, skillD));

        cianid.setExperience(1);
        allUsers.add(cianid);


        var zenith = new User();
        zenith.setId(3L);
        zenith.setUsername("zenith");
        zenith.setAboutMe("About zenith");
        zenith.setEmail("zenith@gmail.com");


        var contactE = new Contact();
        contactE.setContact("Nadir");
        var contactF = new Contact();
        contactF.setContact("Cianid");
        zenith.setContacts(List.of(contactE, contactF));

        zenith.setCountry(new Country(1L, "Russia", new ArrayList<>()));
        zenith.setCity("Kirov");
        zenith.setPhone("777111");

        var skillE = new Skill();
        skillE.setTitle("C++");
        var skillF = new Skill();
        skillF.setTitle("Qt");
        zenith.setSkills(List.of(skillE, skillF));

        zenith.setExperience(10);
        allUsers.add(zenith);
    }

    @Test
    void matchesByNamePatternTest() {
        //before
        filter.setNamePattern("nadir");
        expectedMatches = List.of(allUsers.get(0));

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void matchesByAboutMePatternTest() {
        //before
        filter.setAboutPattern("About nadir");
        expectedMatches = List.of(allUsers.get(0));

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void matchesByEmailPatternTest() {
        //before
        filter.setEmailPattern("nadir@gmail.com");
        expectedMatches = List.of(allUsers.get(0));

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void matchesByContactPatternTest() {
        //before
        filter.setContactPattern("Nadir");
        expectedMatches = List.of(allUsers.get(1), allUsers.get(2));

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void matchesByCountryPatternTest() {
        //before
        filter.setCountryPattern("Russia");
        expectedMatches = List.of(allUsers.get(0), allUsers.get(2));

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void matchesByCityPatternTest() {
        //before
        filter.setCityPattern("Lon.*");
        expectedMatches = List.of(allUsers.get(1));

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void matchesByPhonePatternTest() {
        //before
        filter.setPhonePattern("123..");
        expectedMatches = List.of(allUsers.get(0));

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void matchesBySkillPatternTest() {
        //before
        filter.setSkillPattern("SQL");
        expectedMatches = List.of(allUsers.get(0), allUsers.get(1));

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void matchesByExperienceMinMaxPatternTest() {
        //before
        filter.setExperienceMin(3);
        filter.setExperienceMax(12);
        expectedMatches = List.of(allUsers.get(0), allUsers.get(2));

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void matchesByExperienceMinPatternTest() {
        //before
        filter.setExperienceMin(1);
        expectedMatches = allUsers;

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void matchesByExperienceMaxPatternTest() {
        //before
        filter.setExperienceMax(4);
        expectedMatches = List.of(allUsers.get(0), allUsers.get(1));

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @Test
    void nonmatchingArgTest() {
        //before
        filter.setNamePattern("nadir");
        filter.setCountryPattern("Russia");
        filter.setPhonePattern("tgdfg");
        expectedMatches = List.of();

        //when
        var actualResult = getMatches();

        //then
        assertEquals(expectedMatches, actualResult);
    }

    @NotNull
    private List<User> getMatches() {
        return allUsers.stream()
                .filter(filter::matches)
                .toList();
    }
}