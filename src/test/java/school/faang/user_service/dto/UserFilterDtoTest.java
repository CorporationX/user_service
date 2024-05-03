package school.faang.user_service.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FieldDefaults(level = AccessLevel.PRIVATE)
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

    @DisplayName("Matching by name pattern test")
    @Test
    void matchesByNamePatternTest() {
        filter.setNamePattern("nadir");
        expectedMatches = List.of(allUsers.get(0));


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Matching by about me pattern test")
    @Test
    void matchesByAboutMePatternTest() {
        filter.setAboutPattern("About nadir");
        expectedMatches = List.of(allUsers.get(0));


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Matching by email pattern test")
    @Test
    void matchesByEmailPatternTest() {
        filter.setEmailPattern("nadir@gmail.com");
        expectedMatches = List.of(allUsers.get(0));


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Matching by contact pattern test")
    @Test
    void matchesByContactPatternTest() {
        filter.setContactPattern("Nadir");
        expectedMatches = List.of(allUsers.get(1), allUsers.get(2));


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Matching by country pattern test")
    @Test
    void matchesByCountryPatternTest() {
        filter.setCountryPattern("Russia");
        expectedMatches = List.of(allUsers.get(0), allUsers.get(2));


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Matching by city pattern test")
    @Test
    void matchesByCityPatternTest() {
        filter.setCityPattern("Lon.*");
        expectedMatches = List.of(allUsers.get(1));


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Matching by phone pattern test")
    @Test
    void matchesByPhonePatternTest() {
        filter.setPhonePattern("123..");
        expectedMatches = List.of(allUsers.get(0));


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Matching by skill pattern test")
    @Test
    void matchesBySkillPatternTest() {
        filter.setSkillPattern("SQL");
        expectedMatches = List.of(allUsers.get(0), allUsers.get(1));


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Matching by experience bounds test")
    @Test
    void matchesByExperienceMinMaxPatternTest() {
        filter.setExperienceMin(3);
        filter.setExperienceMax(12);
        expectedMatches = List.of(allUsers.get(0), allUsers.get(2));


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Matching by experience min bound test")
    @Test
    void matchesByExperienceMinPatternTest() {
        filter.setExperienceMin(1);
        expectedMatches = allUsers;


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Matching by experience max bound test")
    @Test
    void matchesByExperienceMaxPatternTest() {
        filter.setExperienceMax(4);
        expectedMatches = List.of(allUsers.get(0), allUsers.get(1));


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @DisplayName("Non matching user test")
    @Test
    void nonMatchingArgTest() {
        filter.setNamePattern("nadir");
        filter.setCountryPattern("Russia");
        filter.setPhonePattern("tgdfg");
        expectedMatches = List.of();


        var actualResult = getMatches();


        assertEquals(expectedMatches, actualResult);
    }

    @NotNull
    private List<User> getMatches() {
        return allUsers.stream()
                .filter(filter::matches)
                .toList();
    }
}