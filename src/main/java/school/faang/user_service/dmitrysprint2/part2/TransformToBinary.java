package school.faang.user_service.dmitrysprint2.part2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransformToBinary {
    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        ints.add(4);
        System.out.println(transform(ints));

    }
    public static List<String> transform(List<Integer> ints){
        return ints.stream().map(z -> Integer.toBinaryString(z)).collect(Collectors.toList());


    }
}
