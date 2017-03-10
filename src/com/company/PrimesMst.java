package com.company;

import java.util.ArrayList;
import java.util.List;

class PrimesMst {
    private int n;
    private int[][] dist;

    PrimesMst(int[][] array) {
        n = array.length;
        dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(array[i], 0, dist[i], 0, n);
        }

    }

    ArrayList<Point> mst() {
        ArrayList<Point> MST = new ArrayList<>();
        List<Integer> inMST = new ArrayList<>();
        List<Boolean> key = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            key.add(i, false);
        }
        Point firstEdge = shortestEdge(dist);
        MST.add(firstEdge);
        inMST.add((int) firstEdge.x);
        inMST.add((int) firstEdge.y);
        key.set((int) firstEdge.x, true);
        key.set((int) firstEdge.y, true);
        while (inMST.size() < n) {
            int cut = Integer.MAX_VALUE;
            Point edge = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
            for (Integer anInMst : inMST) {
                for (int i = 0; i < key.size(); i++) {
                    if (!key.get(i)) {
                        if ((dist[anInMst][i] < cut)) {
                            edge.x = anInMst;
                            edge.y = i;
                            cut = dist[anInMst][i];
                        }
                    }
                }
            }
            MST.add(edge);
            inMST.add((int) edge.y);
            key.set((int) edge.y, true);
        }
        return MST;
    }

    private Point shortestEdge(int[][] dist) {
        int shortest = dist[0][1];
        Point edge = new Point(0, 1);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (dist[i][j] < shortest) {
                    shortest = dist[i][j];
                    edge.x = i;
                    edge.y = j;
                }
            }
        }
        return edge;
    }

    int sum(ArrayList<Point> arrayList){
        int sum=0;
        for (Point anArrayList : arrayList) {
            sum += dist[(int) anArrayList.x][(int) anArrayList.y];
        }
        return sum;
    }
}

