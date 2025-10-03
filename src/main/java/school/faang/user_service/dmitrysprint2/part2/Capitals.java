package school.faang.user_service.dmitrysprint2.part2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Capitals {
    public static void main(String[] args) {
        Map<String, String> coutriesMap = new HashMap<String, String>();
        coutriesMap.put("Russia", "Moscow");
        coutriesMap.put("USA", "Washington");
        coutriesMap.put("Germany", "Berlin");


        System.out.println(showCapitals(coutriesMap));

    }

    public static List<String> showCapitals(Map<String, String> sample){
        Set<Map.Entry<String, String>> setFromMap = sample.entrySet();

        return setFromMap.stream().map(m -> m.getValue()).collect(Collectors.toList());

    }

}
