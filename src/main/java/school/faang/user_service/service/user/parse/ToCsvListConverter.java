package school.faang.user_service.service.user.parse;

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

@Component
public class ToCsvListConverter {

    public List<CSVPart> convertToCsvList(InputStream inputStream) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new DataValidationException(INPUT_OUTPUT_EXCEPTION.getMessage());
        }
        if (lines.isEmpty()) {
            throw new DataValidationException(INPUT_IS_EMPTY.getMessage());
        }
        String header = lines.get(0);
        lines.remove(0);
        int totalLines = lines.size();
        int amountOfParts = 4;
        int linesPerPart = totalLines / amountOfParts;
        int lineCount = 0;
        int partCount = 0;
        List<String> currentPartLines = new ArrayList<>();
        List<CSVPart> parts = new ArrayList<>();

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
}
