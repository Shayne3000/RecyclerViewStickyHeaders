package com.senijoshua.recyclerviewstickyheaders;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CelebrityUtility {

    public static List<Celebrity> loadCelebrities(){
        List<Celebrity> celebrityList = new ArrayList<>();
        Random r = new Random();
        String alphabetName="abcdefghijklmnopqrstuvwxyz";
        String alphabetStatus="abcde";
        int N = alphabetName.length();
        int S = alphabetStatus.length();
        for (int i = 0, listSize = 100; i < listSize; i++){
            String randomSectionName = (alphabetName.charAt(r.nextInt(N))) + "";
            String randomParentName = (alphabetStatus.charAt(r.nextInt(S))) + "";
            celebrityList.add(new Celebrity(randomSectionName + i, randomParentName));
        }
        Collections.sort(celebrityList, new Comparator<Celebrity>() {
            @Override
            public int compare(Celebrity o1, Celebrity o2) {
                return o1.getName().substring(0,1).compareToIgnoreCase(o2.getName().substring(0,1));
            }
        });
        return celebrityList;
    }
}
