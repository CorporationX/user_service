package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.pojo.Person;
import school.faang.user_service.mapper.PersonCsvMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper mapper;
    private final ExecutorService executor;
    private final PersonCsvMapper personCsvMapper;

    @Override
    public UserDto getUser(long id) {
        return userRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<UserDto> uploadFile(InputStream inputStream) {
        List<Person> persons = personCsvMapper.getPersonsFromInputStream(inputStream);
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
}
