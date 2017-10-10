package com.senijoshua.recyclerviewstickyheaders;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CelebrityUtility {

    public static List<Celebrity> loadCelebrities(){
        List<Celebrity> celebrityList = new ArrayList<>();
        for (int i = 0, listSize = 100; i < listSize; i++){
            Random r = new Random();
            String randomSectionName = (r.nextInt(26) + 'a') + "";
            String randomParentName = (r.nextInt(5) + 'a') + "";
            celebrityList.add(new Celebrity(randomSectionName + i, randomParentName));
        }
        Collections.sort(celebrityList, new Comparator<Celebrity>() {
            @Override
            public int compare(Celebrity o1, Celebrity o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return celebrityList;
    }
}
