package org.mz.deepository.lego.builder;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class IteratorCheck {

    public static void main(String[] args) throws IOException {
        Buildings trainBuildings = new Buildings(args[0]);

        BrickCodec brickCodec = new BrickCodec();
        Map<Integer, Long> countStat = trainBuildings.load()
                .flatMap(building -> {
                    return building.outlines().stream();
                }).flatMap(outline -> outline.bricks().stream())
                .collect(Collectors.groupingBy(brick -> brickCodec.encode(brick), Collectors.counting()));

        TreeSet<Integer> treeSet = new TreeSet(countStat.keySet());
        for (Integer encodedValue : treeSet) {
            System.out.println("" + encodedValue + "(" + brickCodec.decode(encodedValue) + "): " + countStat.get(encodedValue));
        }

        Map<Integer, Long> only104 = trainBuildings.load()
                .flatMap(building -> building.outlines().stream())
                .flatMap(outline -> {
                    LinkedList<Pair<Brick, Brick>> acc = Lists.newLinkedList();
                    for (int i = 0; i < outline.bricks().size() - 1; i++) {
                        acc.add(Pair.of(outline.bricks().get(i), outline.bricks().get(i + 1)));
                    }
                    return acc.stream();
                }).map(pair -> Pair.of(brickCodec.encode(pair.getLeft()), brickCodec.encode(pair.getRight())))
                .filter(pair -> pair.getLeft().equals(104))
                .map(pair -> pair.getRight())
                .collect(Collectors.groupingBy(brick -> brick, Collectors.counting()));

        treeSet = new TreeSet(only104.keySet());
        for (Integer encodedValue : treeSet) {
            System.out.println("" + encodedValue + "(" + brickCodec.decode(encodedValue) + "): " + only104.get(encodedValue));
        }

    }

}
