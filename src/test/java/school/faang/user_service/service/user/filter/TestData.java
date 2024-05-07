package school.faang.user_service.service.user.filter;

import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.ArrayList;
import java.util.List;


public class TestData {
    public final static List<User> ALL_USERS = new ArrayList<>();

    static {
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

        ALL_USERS.add(nadir);


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
        ALL_USERS.add(cianid);


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
        ALL_USERS.add(zenith);
    }
}
