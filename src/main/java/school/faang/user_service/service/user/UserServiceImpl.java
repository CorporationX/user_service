package school.faang.user_service.service.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.pojo.*;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final MentorshipService mentorshipService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final CountryRepository countryRepository;
    private final UserMapper mapper;
    private final ExecutorService executor;

    @Transactional
    public void deactivateUser(@NonNull Long id) {
        log.info("deactivating user with id: {}", id);
        goalRepository.deleteUnusedGoalsByMentorId(id);
        eventRepository.deleteAllByOwnerId(id);
        mentorshipService.stopMentorship(id);
        userRepository.updateUserActive(id, false);
        log.info("deactivated user with id: {}", id);
    }

    @Override
    public UserDto getUser (long id) {
        return mapper.toDto(userRepository.findById(id).get());
    }

    @Override
    public List<UserDto> getUsersByIds (List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return users.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<UserDto> uploadFile(InputStream inputStream) {
        List<Person> persons = getPersonsFromInputStream(inputStream);
        List<String> countriesTitles = persons.stream()
                .map(person -> person.getContactInfo()
                        .getAddress()
                        .getCountry())
                .distinct()
                .toList();
        List<Country> countries = countryRepository.findAllByTitles(countriesTitles);
        List<Country> newCountries = new ArrayList<>();
        ConcurrentHashMap<String, Country> countryMap = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> countryMapFutures = getCountryMapFutures(countriesTitles, countryMap,
                countries, newCountries);
        CompletableFuture.allOf(countryMapFutures.toArray(CompletableFuture[]::new)).join();
        if (!newCountries.isEmpty()) {
            countryRepository.saveAll(newCountries);
        }
        List<User> users = new ArrayList<>();
        List<CompletableFuture<Void>> usersFutures = getUsersFutures(persons, countryMap, users);
        CompletableFuture.allOf(usersFutures.toArray(CompletableFuture[]::new)).join();
        return mapper.toDtoList(userRepository.saveAll(users));
    }

    private List<CompletableFuture<Void>> getCountryMapFutures(List<String> countriesTitles,
                                                               ConcurrentHashMap<String, Country> countryMap,
                                                               List<Country> countries,
                                                               List<Country> newCountries) {
        return countriesTitles.stream()
                .parallel()
                .map(title -> CompletableFuture.runAsync(() -> {
                    countryMap.put(title, getValueForCountryMap(title, countries, newCountries));
                }, executor))
                .toList();
    }

    private List<CompletableFuture<Void>> getUsersFutures(List<Person> persons,
                                                          ConcurrentHashMap<String, Country> countryMap,
                                                          List<User> users) {
        return persons.stream()
                .parallel()
                .map(person -> CompletableFuture.runAsync(() -> {
                    User newUser = mapper.toEntity(person, countryMap.get(person.getContactInfo()
                                    .getAddress()
                                    .getCountry()),
                            generatePassword());
                    synchronized (persons) {
                        users.add(newUser);
                    }
                }, executor))
                .toList();
    }

    private Country getValueForCountryMap(String title, List<Country> countries, List<Country> newCountries) {
        return countries.stream()
                .filter(country -> country.getTitle().equals(title))
                .findAny()
                .orElseGet(() -> saveNewCountryInList(title, newCountries));
    }

    private Country saveNewCountryInList(String title, List<Country> countries) {
        Country country = Country.builder()
                .title(title)
                .build();
        synchronized (this) {
            countries.add(country);
        }
        return country;
    }

    private String generatePassword() {
        Random random = new Random();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return PASSWORD_ENCODER.encode(password.toString());
    }

    private List<Person> getPersonsFromInputStream(InputStream inputStream) {
        List<Person> people = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                Person.PersonBuilder personBuilder = Person.builder();
                personBuilder.firstName(parts[0].trim());
                personBuilder.lastName(parts[1].trim());
                personBuilder.yearOfBirth(Integer.parseInt(parts[2].trim()));
                personBuilder.group(parts[3].trim());
                personBuilder.studentID(parts[4].trim());

                Address address = new Address(parts[7].trim(), parts[8].trim(), parts[9].trim(), parts[10].trim(),
                        parts[11].trim());
                ContactInfo contactInfo = new ContactInfo(parts[5].trim(), parts[6].trim(), address);

                contactInfo.setAddress(address);
                personBuilder.contactInfo(contactInfo);

                Education education = new Education(parts[12].trim(), Integer.parseInt(parts[13].trim()),
                        parts[14].trim(), Double.parseDouble(parts[15].trim()));
                personBuilder.education(education);

                personBuilder.status(parts[16].trim());
                personBuilder.admissionDate(parts[17].trim());
                personBuilder.graduationDate(parts[18].trim());

                List<PreviousEducation> previousEducationList = new ArrayList<>();
                PreviousEducation previousEducation = new PreviousEducation(parts[19].trim(), parts[20].trim(),
                        Integer.parseInt(parts[21].trim()));
                previousEducationList.add(previousEducation);
                personBuilder.previousEducation(previousEducationList);

                personBuilder.scholarship(Boolean.parseBoolean(parts[22].trim()));
                personBuilder.employer(parts[23].trim());

                people.add(personBuilder.build());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to convert file to Person object");
        }
        return people;
    }
}
