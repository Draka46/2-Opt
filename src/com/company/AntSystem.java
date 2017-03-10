package com.company;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

class AntSystem {

    private final float[][] pheromoneMatrix;
    private List<Integer> bestSoFarTour;
    private int[][] dist;
    private Trail[] trails;
    private int n;
    private int optimum;
    private float bestSoFar;
    private Point[] map;
    ArrayList<Point> mst;

    AntSystem(String fileName) {
        map = readInstance(fileName);
        n = map.length;
        dist = mapToDistanceMatrix(map);
        bestSoFarTour = new ArrayList<>();
        bestSoFar = Float.MAX_VALUE;
        trails = new Trail[n];
        pheromoneMatrix = new float[n][n];
        for (int i = 0; i < n; i++) {
            List<Integer> tour = nearestNeighbourTour(dist, i);
            updatePheromones(tour);
        }
    }

    void go() {
        JFrame frame = new JFrame();
        float w = 1200;
        float h = 1000;
        MyDrawPanel drawPanel = new MyDrawPanel(map, w, h);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.getContentPane().add(drawPanel);
        frame.setSize((int) w, (int) h);
        frame.setVisible(true);
        PrimesMst primesMst = new PrimesMst(dist);
        ArrayList<Point> mst = primesMst.mst();
        while (bestSoFar > optimum) {
            for (int i = 0; i < n; i++) {
                List<Integer> path = constructSolution(dist);
                float length = countTourLength(path,dist);
                if(length < bestSoFar){
                    bestSoFar = length;
                    bestSoFarTour = path;
                }
                trails[i] = new Trail(path);
            }
            vaporize(pheromoneMatrix, 1 / 2);
            for (int i = 0; i < n; i++) {
                updatePheromones(trails[i].trail);
            }
            for (int i = 0; i < n; i++) {
                kOptSearch(bestSoFarTour);
                updatePheromones(bestSoFarTour);
            }

            try {
                String result = Float.toString(100 - 100 * (bestSoFar - optimum) / optimum);
                String s = "length=" + (int) (bestSoFar) + " "  + " optimum="
                        + optimum + " precision=" + result + "%";
                //System.out.println(s);
                drawPanel.passToGraphics(bestSoFarTour, s);
                drawPanel.repaint();

                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    private void updatePheromones(List<Integer> tour) {
        float length = countTourLength(tour, dist);
        pheromoneMatrix[tour.get(n - 1)][tour.get(0)] += n / length;
        for (int j = 1; j < (n - 1); j++) {
            pheromoneMatrix[tour.get(j - 1)][tour.get(j)] += n / length;
        }
    }

    private void vaporize(float[][] pheromoneMatrix, float rate) {
        float scalar = 1 - rate;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                pheromoneMatrix[i][j] = pheromoneMatrix[i][j] * scalar;
            }
        }
    }

    private List<Integer> constructSolution(int[][] distanceMatrix) {
        List<Integer> tour = new ArrayList<>();
        List<Integer> candidates = new ArrayList<>();
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(distanceMatrix[i], 0, matrix[i], 0, n);
        }
        int pointer = (int) (Math.random() * n);
        tour.add(pointer);
        for (int k = 0; k < (n - 1); k++) {
            for (int j = 0; j < n; j++) {
                if (matrix[pointer][j] < Integer.MAX_VALUE) {
                    candidates.add(j);
                }
                matrix[j][pointer] = Integer.MAX_VALUE;
            }
            if (k == n - 1) {
                pointer = candidates.get(0);
            } else {
                pointer = stepDecision(pointer, candidates);
            }
            candidates.clear();
            tour.add(pointer);
        }
        return tour;
    }

    private int stepDecision(int pointer, List<Integer> candidates) {
        List<Float> numerators = new ArrayList<>();
        List<Float> probabilities = new ArrayList<>();
        int choice;
        float denominator = 0;
        int numberOfCandidates = candidates.size();
        for (Integer candidate : candidates) {
            float numerator = pheromoneMatrix[pointer][candidate] /
                    (dist[pointer][candidate] *
                            dist[pointer][candidate]);
            numerators.add(numerator);
            denominator += numerator;
        }
        if (denominator == 0) denominator = 1;
        float sumOfProbabilities = 0;
        for (int candidate = 0; candidate < numberOfCandidates; candidate++) {
            float probabilityToBeChosen = numerators.get(candidate) / denominator;
            probabilities.add(probabilityToBeChosen);
            sumOfProbabilities += probabilityToBeChosen;
        }
        float dice = (float) (Math.random() * sumOfProbabilities);
        boolean found = false;
        int candidate = 0;
        float limit = probabilities.get(candidate);
        while (!found) {
            if (dice <= limit) {
                found = true;
            } else {
                candidate++;
                limit += probabilities.get(candidate);
            }
        }
        choice = candidates.get(candidate);
        return choice;
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

    private void kOptSearch(List<Integer> perm) {
        int depth = 2;
        // int gain;
        int deletedFirst = dist[perm.get(0)][perm.get(1)];
        for (int i = 1; i < n - 2; i++) {
            int deletedLast = dist[perm.get(i + 1)][perm.get(i + 2)];
            int addedFirst = dist[perm.get(0)][perm.get(i + 1)];
            int addedLast = dist[perm.get(1)][perm.get(i + 2)];
            if ((deletedFirst + deletedLast) > (addedFirst + addedLast)) {
                //gain = deletedFirst + deletedLast - (addedFirst + addedLast);
                int prev = 1;
                int next = i + 2;
                //System.out.println("depth " + depth + " gain " + gain);
                search(next, prev, depth, addedLast, perm);
                flip(prev, i + 1, perm);
                //System.out.println("LENGTH " + countTourLength(perm, dist));
                break;
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
                //int gain = deletedFirst + deletedLast - (addedFirst + addedLast);
                prev = next;
                next = i + 2;
                //System.out.println("depth " + depth + " gain " + gain);
                search(next, prev, depth, addedLast, perm);
                flip(prev, i + 1, perm);
                break;
            }
        }
    }

    private void randomDoubleBridgeKick(List<Integer> perm) {
        int a = 1 + (int) (Math.random() * perm.size() / 4);
        int b = a + 1 + (int) (Math.random() * perm.size() / 4);
        int c = b + 1 + (int) (Math.random() * perm.size() / 4);
        int d = c + 1 + (int) (Math.random() * perm.size() / 4);
        flip(a, c - 1, perm);
        flip(b, d - 1, perm);
        flip(d, b - 1, perm);
        flip(c, a - 1, perm);
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
}
