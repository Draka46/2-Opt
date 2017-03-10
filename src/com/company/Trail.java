package com.company;

import java.util.ArrayList;
import java.util.List;

class Trail {
    List<Integer> trail;

    Trail(List<Integer> path) {
        trail = new ArrayList<>();
        for (Integer aPath : path) {
            trail.add(aPath);
        }
    }
}
