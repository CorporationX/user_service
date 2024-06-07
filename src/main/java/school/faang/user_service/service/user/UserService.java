package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final CountryRepository countryRepository;

    private final UserRepository userRepository;

    private static final int THREAD_POOL_SIZE = 4;

    public List<UserDto> saveUsers(InputStream inputStream) throws IOException, ExecutionException, InterruptedException {
        List<CSVPart> csvParts = splitCsvFile(inputStream);
        List<InputStream> inputStreamParts = listOfCsvPartToListOfInputStreamConverter(csvParts);
        List<Person> allPersons = multiParser(inputStreamParts);
        List<User> allUsers = countryAndPasswordAdderAndListOfPersonsToListOfUsersConverter(allPersons);

        List<User> updatedUsers = new ArrayList<>(allUsers);
        Iterable<User> iterableForExistingUsers = userRepository.findAll();

        for (User existingUser : iterableForExistingUsers) {
            for (User user : allUsers) {
                if (existingUser.getUsername().equals(user.getUsername())
                        || existingUser.getEmail().equals(user.getEmail())
                        || existingUser.getPhone().equals(user.getPhone())) {
                    updatedUsers.remove(user);
                }
            }
        }
        userRepository.saveAll(updatedUsers);
        return userMapper.toDto(updatedUsers);
    }

    private List<CSVPart> splitCsvFile(InputStream inputStream) throws IOException {
        List<CSVPart> parts = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        String header = lines.get(0);
        lines.remove(0);
        int totalLines = lines.size();
        int amountOfParts = 4;
        int linesPerPart = totalLines / amountOfParts;
        int lineCount = 0;
        int partCount = 0;
        List<String> currentPartLines = new ArrayList<>();

        for (String currentLine : lines) {
            currentPartLines.add(header);
            currentPartLines.add(currentLine);
            lineCount++;

            if (amountOfParts == partCount && totalLines > partCount) {
                parts.add(new CSVPart(currentPartLines));
                break;
            }

            if (lineCount == linesPerPart) {
                parts.add(new CSVPart(currentPartLines));
                currentPartLines = new ArrayList<>();
                lineCount = 0;
                partCount++;
            }
        }
        return parts;
    }

    private List<InputStream> listOfCsvPartToListOfInputStreamConverter(List<CSVPart> parts) {
        List<StringBuilder> stringBuilders = new ArrayList<>();
        List<InputStream> inputStreams = new ArrayList<>();
        for (CSVPart part : parts) {
            List<String> lines = part.getLines();
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line).append(System.lineSeparator());
            }
            stringBuilders.add(sb);
        }
        for (StringBuilder stringBuilder : stringBuilders) {
            byte[] bytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
            inputStreams.add(new ByteArrayInputStream(bytes));
        }
        return inputStreams;
    }


    private List<Person> multiParser(List<InputStream> parts) throws ExecutionException, InterruptedException {
        List<Person> allPersons = new ArrayList<>();

        ExecutorService executorsService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        Future<List<Person>> result = null;

        for (InputStream part : parts) {
            result = executorsService.submit(() -> parser(part));
            allPersons.addAll(result.get());
        }

        result.get();
        return allPersons;
    }

    private List<Person> parser(InputStream part) throws IOException {
        CsvMapper mapper = new CsvMapper();

        MappingIterator<Person> iterator = mapper
                .readerFor(Person.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(part);
        return iterator.readAll();
    }

    private List<User> countryAndPasswordAdderAndListOfPersonsToListOfUsersConverter(List<Person> persons) {
        List<User> users = new ArrayList<>();
        Iterable<Country> iterableForCountries = countryRepository.findAll();

        for (Person person : persons) {
            String password = passwordGenerator();
            Iterator<Country> iteratorForCountries = iterableForCountries.iterator();
            String countryName = person.getContactInfo().getAddress().getCountry();
            User user = userMapper.toUser(person);

            while (iteratorForCountries.hasNext()) {
                Country existingCountry = iteratorForCountries.next();
                if (countryName.equals(existingCountry.getTitle())) {
                    user.setCountry(existingCountry);
                    user.setPassword(password);
                    users.add(user);
                }
            }

            if (user.getCountry() == null) {
                Country newCountry = new Country();
                newCountry.setTitle(countryName);
                countryRepository.save(newCountry);
                iterableForCountries = countryRepository.findAll();

                for (Country existingNewCountry : iterableForCountries) {
                    if (countryName.equals(existingNewCountry.getTitle())) {
                        user.setCountry(countryRepository.findById(existingNewCountry.getId()).get());
                        user.setPassword(password);
                    }
                }
                users.add(user);
            }
        }
        return users;
    }

    private String passwordGenerator() {
        String combination = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()_+{}:<>?";
        int length = 10;
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(combination.charAt(random.nextInt(combination.length())));
        }
        return password.toString();
    }
}
