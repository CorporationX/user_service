package school.faang.user_service.service.user.parse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static school.faang.user_service.exception.message.ExceptionMessage.INPUT_IS_EMPTY;
import static school.faang.user_service.exception.message.ExceptionMessage.INPUT_OUTPUT_EXCEPTION;

/**
 * Класс для преобразования InputStream в List<CsvPart>.
 * В методе readInput читается InputStream построчно и каждая строка заносится в List<String>.
 * В методе toCsvPartDivider количество строк делится на количество частей, на которые нужно поделить InputStream,
 * В каждую часть заносится заголовок.
 */
@Slf4j
@Component
public class ReadAndDivide {

    private static final int AMOUNT_OF_PARTS = 4;

    public List<CsvPart> toCsvPartDivider(InputStream inputStream) {
        List<String> lines = readInput(inputStream);
        String header = lines.remove(0);
        int totalLines = lines.size();
        int linesPerPart = totalLines / AMOUNT_OF_PARTS;
        List<CsvPart> parts = new ArrayList<>();

        for (int i = 0; i < AMOUNT_OF_PARTS; i++) {
            int start = i * linesPerPart;
            int end = (i == AMOUNT_OF_PARTS - 1) ? totalLines : (start + linesPerPart);
            List<String> partLines = new ArrayList<>();
            partLines.add(header);
            partLines.addAll(lines.subList(start, end));
            parts.add(new CsvPart(partLines));
        }
        return parts;
    }

    private List<String> readInput(InputStream inputStream) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            log.error(INPUT_OUTPUT_EXCEPTION.getMessage());
            throw new DataValidationException(INPUT_OUTPUT_EXCEPTION.getMessage());
        }
        if (lines.isEmpty()) {
            log.error(INPUT_IS_EMPTY.getMessage());
            throw new DataValidationException(INPUT_IS_EMPTY.getMessage());
        }
        return lines;
    }
}
