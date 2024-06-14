package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@AllArgsConstructor
public class Converter {

    private CountryRepository countryRepository;

    private UserMapper userMapper;

    private Adder adder;

    public List<CSVPart> convertToCsvList(InputStream inputStream) throws IOException {
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

    public List<InputStream> convertToInputStreamList(List<CSVPart> parts) {
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

    public List<User> convertToUserList(List<Person> persons) {
        List<User> users = new ArrayList<>();
        Iterable<Country> iterableForCountries = countryRepository.findAll();

        for (Person person : persons) {
            Iterator<Country> iteratorForCountries = iterableForCountries.iterator();
            String countryName = person.getContactInfo().getAddress().getCountry();
            User user = userMapper.toUser(person);

            while (iteratorForCountries.hasNext()) {
                Country existingCountry = iteratorForCountries.next();
                if (countryName.equals(existingCountry.getTitle())) {
                    users.add(adder.addCountryAndPassword(user, existingCountry));
                }
            }

            if (user.getCountry() == null) {
                Country newCountry = new Country();
                newCountry.setTitle(countryName);
                countryRepository.save(newCountry);
                iterableForCountries = countryRepository.findAll();

                for (Country existingNewCountry : iterableForCountries) {
                    if (countryName.equals(existingNewCountry.getTitle())) {
                        user = adder.addCountryAndPassword(user, countryRepository.findById(existingNewCountry.getId()).get());
                    }
                }
                users.add(user);
            }
        }
        return users;
    }
}
