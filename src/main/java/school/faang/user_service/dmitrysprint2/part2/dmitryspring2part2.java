package school.faang.user_service.dmitrysprint2.part2;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class dmitryspring2part2 {

    public static void main(String[] args) {
        Set<Integer> intSet = new HashSet<>();
        intSet.add(1);
        intSet.add(2);
        intSet.add(3);
        intSet.add(4);
        intSet.add(5);
        intSet.add(6);
        specialPairCreator(intSet, 6);
        System.out.println(specialPairCreator(intSet, 6));

    }

    public static Set<Pair> specialPairCreator(Set<Integer> intSet, Integer sample) {


        intSet.stream().flatMap(a -> intSet.stream().map(b -> new Pair(a, b))).filter(pair -> pair.first + pair.second == sample).collect(Collectors.toSet());


        return intSet.stream().flatMap(a -> intSet.stream().map(b -> new Pair(a, b))).filter(pair -> pair.first + pair.second == sample).collect(Collectors.toSet());
    }
}
