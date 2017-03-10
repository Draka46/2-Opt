package com.company;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

class PathImprovement {

    int p;
    private int[][] dist;
    private int n;
    private float bestSoFar;
    private int optimum;
    private Point[] map;
    private List<Integer> bestSoFarTour;
    private String fileName;
    private Boolean MST;

    PathImprovement(String fileName) {
        this.fileName = fileName;
        map = readInstance(fileName);
        n = map.length;
        bestSoFar = Integer.MAX_VALUE;
        bestSoFarTour = new ArrayList<>();
        dist = mapToDistanceMatrix(map);
        List<Integer> tour = nearestNeighbourTour(dist, (int) (Math.random() * n));

        bestSoFar = countTourLength(tour, dist);
        bestSoFarTour = tour;
        MST = false;
    }

    void go() {
        JFrame frame = new JFrame();
        float w = 1600;
        float h = 1000;
        frame.setSize((int) w, (int) h);
        MyDrawPanel drawPanel = new MyDrawPanel(map, w, h);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.getContentPane().add(drawPanel);
        frame.setVisible(true);


        if (MST) {
            PrimesMst primesMst = new PrimesMst(dist);
            ArrayList<Point> mst = primesMst.mst();
            drawPanel.mstToGraphics(mst, true);
        }
        while (bestSoFar > optimum) {
            List<Integer> perm = new ArrayList<>();
            for (Integer integer : perm = bestSoFarTour) {

            }
            ;

            opt2(perm);

            int length = countTourLength(perm, dist);
            if (length < bestSoFar) {
                bestSoFar = length;
                bestSoFarTour = perm;
                System.out.println(length);
            }


            try {
                float pct = (int) (10000 * (bestSoFar - optimum) / optimum + 1 / 10000);
                pct = 100 - pct / 100;
                String precision = Float.toString(pct);
                String s = "length=" + (int) (bestSoFar) + " "
                        + " optimum=" + optimum + " precision=" + precision + "%" +
                        "" + " TSP " + fileName;
                drawPanel.passToGraphics(bestSoFarTour, s);
                drawPanel.repaint();
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void opt2(List<Integer> p) {
        int x = dist[p.get(0)][p.get(1)];
        int y = dist[p.get(n-1)][p.get(n-2)];
        for (int i = 2; i < (n/2); i++) {
            if (x + dist[p.get(i)][p.get(i + 1)]
                    > dist[p.get(0)][p.get(i)]
                    + dist[p.get(1)][p.get(i + 1)]){
                flip(1, i, p);
                System.out.println("FLIP");
                break;
            }
            if (y + dist[p.get(n-1-i)][p.get(n-2-i)]
                    > dist[p.get(n-1)][p.get(n-1-i)]
                    + dist[p.get(n-2)][p.get(n-2-i)]) {
                flip(n-1-i, n - 2, p);
                System.out.println("aFLIP");
                break;
            }
        }
        p.add(0, p.get(n - 1));
        p.remove(n);
    }
    private void opt3(List<Integer> p) {
        int x = dist[p.get(0)][p.get(1)];
        int y = dist[p.get(n - 1)][p.get(n - 2)];
        boolean left= false;
        boolean right= false;

        for (int i = 2;i < n / 2 - 1;i++) {

            int lGain = x + dist[p.get(i)][p.get(i + 1)]
                    - dist[p.get(0)][p.get(i)]
                    - dist[p.get(1)][p.get(i + 1)];
            int rGain = y + dist[p.get(n - 1 - i)][p.get(n - 2 - i)]
                    - dist[p.get(n - 1)][p.get(n - 1 - i)]
                    - dist[p.get(n - 2)][p.get(n - 2 - i)];
            int rlGain = dist[p.get(i)][p.get(i + 1)]
                    + dist[p.get(n - 2 - i)][p.get(n - 1 - i)]
                    - dist[p.get(2)][p.get(n - 2 - i)]
                    - dist[p.get(i + 1)][p.get(n - 3)];
            if (lGain > 0) {
                flip(1, i, p);
                left = true;
                System.out.println("L FLIP " + lGain);
            }
            if (rGain > 0) {
                flip((n - 1) - i, (n - 1) - 1, p);
                right = true;
                System.out.println("R FLIP " + rGain);
            }
            if (rlGain > 0) {
               flip(i + 1,(n - 2 - i),p);
               System.out.println("R&L FLIP <--"+rlGain);
               break;
            }else{
               break;
            }
        }
        p.add(0, p.get(n - 1));
        p.remove(n);
    }



    private void kOptSearch(List<Integer> perm) {
        int depth = 2;
        int gain;
        int deletedFirst = dist[perm.get(0)][perm.get(1)];
        if (deletedFirst > dist[perm.get(1)][perm.get(2)]) {
            for (int i = 1; i < n - 2; i++) {
                int deletedLast = dist[perm.get(i + 1)][perm.get(i + 2)];
                int addedFirst = dist[perm.get(0)][perm.get(i + 1)];
                int addedLast = dist[perm.get(1)][perm.get(i + 2)];
                if ((deletedFirst + deletedLast) > (addedFirst + addedLast)) {
                    gain = deletedFirst + deletedLast - (addedFirst + addedLast);
                    int prev = 1;
                    int next = i + 2;
                    System.out.println("depth " + depth + " gain " + gain);
                    search(next, prev, depth, addedLast, perm);
                    flip(prev, i + 1, perm);
                    // System.out.println("LENGTH " + countTourLength(perm, dist));
                    break;
                }
            }
        }
        perm.add(0, perm.get(n - 1));
        perm.remove(n);
    }


    private void search(int next, int prev, int depth, int addedLast, List<Integer> perm) {
        int deletedFirst = addedLast;
        depth++;
        for (int i = next; i < n - (2 * depth - 2); i++) {
            int deletedLast = dist[perm.get(i + 1)][perm.get(i + 2)];
            int addedFirst = dist[perm.get(prev)][perm.get(i + 1)];
            addedLast = dist[perm.get(next)][perm.get(i + 2)];
            if ((deletedFirst + deletedLast) > (addedFirst + addedLast)) {
                int gain = deletedFirst + deletedLast - (dist[perm.get(prev)][perm.get(i + 1)] + dist[perm.get(next)][perm.get(i + 2)]);
                int antiGain = deletedFirst + deletedLast - (dist[perm.get(prev)][perm.get(i + 2)] + dist[perm.get(next)][perm.get(i + 1)]);
                System.out.println("depth " + depth + " gain " + gain + " antiGain " + antiGain);
                if (antiGain > gain) {
                    System.out.println("FOOOO");
                    flip(i + 2, i + 1, perm);
                    break;
                }
                prev = next;
                next = i + 2;

                search(next, prev, depth, addedLast, perm);
                flip(prev, i + 1, perm);
                break;
            }
        }
    }

    private List<Integer> randomDoubleBridgeKick(List<Integer> perm) {
        double s = perm.size() / 4 - 2;
        int a = 1 + (int) (s - (Math.random() * s));
        int b = 1 + (int) (2 * s - Math.random() * s);
        int c = 1 + (int) (3 * s - Math.random() * s);
        int d = 1 + (int) (4 * s - Math.random() * s);
        flip(a, c + 1, perm);
        flip(a + 1, c, perm);
        flip(d + 1, b, perm);
        flip(d, b + 1, perm);
        return perm;
    }

    private void flip(int a, int b, List<Integer> perm) {
        List<Integer> fragment = new ArrayList<>();
        for (int i = a; i < b + 1; i++) {
            fragment.add(0, perm.get(i));
        }
        for (int i = a; i < b + 1; i++) {
            perm.set(i, fragment.get(i - a));
        }
    }

    private List<Integer> nearestNeighbourTour(int[][] distanceMatrix, int start) {
        List<Integer> tour = new ArrayList<>();
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(distanceMatrix[i], 0, matrix[i], 0, n);
        }
        int pointer = start;
        int next = pointer;
        tour.add(pointer);
        for (int k = 0; k < n - 1; k++) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                if (matrix[pointer][j] < min) {
                    min = matrix[pointer][j];
                    next = j;
                }
                matrix[j][pointer] = Integer.MAX_VALUE;
            }
            pointer = next;
            tour.add(pointer);
        }
        return tour;
    }

    private int countTourLength(List<Integer> tour, int[][] matrix) {
        int length = 0;
        int k = tour.size();
        length += matrix[tour.get(k - 1)][tour.get(0)];
        for (int i = 0; i < k - 1; i++) {
            length += matrix[tour.get(i)][tour.get(i + 1)];
        }
        return length;
    }

    private int[][] mapToDistanceMatrix(Point[] map) {
        int[][] distMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    distMatrix[i][j] = (int) (Math.sqrt(
                            ((map[i].x - map[j].x) * (map[i].x - map[j].x)) +
                                    ((map[i].y - map[j].y) * (map[i].y - map[j].y))) + 0.5);
                } else {
                    distMatrix[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        return distMatrix;
    }

    private Point[] readInstance(String fileName) {
        try {
            Scanner input = new Scanner(new File(fileName));
            String str = input.nextLine();
            n = Integer.parseInt(str);
            str = input.nextLine();
            optimum = Integer.parseInt(str);
            map = new Point[n];
            for (int i = 0; i < n; i++) {
                str = input.nextLine();
                String[] data;
                data = str.split(" ");
                double x = Double.parseDouble(data[1].trim());
                double y = Double.parseDouble(data[2].trim());
                map[i] = new Point(x, y);
            }
            input.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return map;
    }
}
