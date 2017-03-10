package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class MyDrawPanel extends JPanel {
    private float w;
    private float h;
    private List<Integer> perm;
    private ArrayList<Point> mst;
    private Point[] screenMap;
    private int n;
    private String s = "";
    private Boolean MST = false;

    MyDrawPanel(Point[] map, float w, float h) {
        n = map.length;
        this.w = w;
        this.h = h;
        screenMap = transform(map);
    }

    void passToGraphics(List<Integer> perm, String s) {
        this.perm = perm;
        this.s = s;
    }

    void mstToGraphics(ArrayList<Point> mst, Boolean MST) {
        this.MST = MST;
        this.mst = mst;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font f0 = new Font("Console", Font.BOLD, 16);
        Color c = new Color(255, 255, 255, 255);
        g2d.setColor(c);
        g2d.fillRect(0, 0, (int) w, (int) h);
        g2d.setFont(f0);
        int i = 0;
        if (MST) {
            if (i < n - 1) {
                do {
                    g2d.setStroke(new BasicStroke(10));
                    g2d.setColor(Color.ORANGE);
                    g2d.drawLine(
                            (int) screenMap[(int) mst.get(i).x].x,
                            (int) screenMap[(int) mst.get(i).x].y,
                            (int) screenMap[(int) mst.get(i).y].x,
                            (int) screenMap[(int) mst.get(i).y].y);
                    i++;
                } while (i < n - 1);
            }
        }

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);
        g2d.drawString(s, 10, 24);
        g2d.drawLine(
                (int) screenMap[perm.get(n - 1)].x,
                (int) screenMap[perm.get(n - 1)].y,
                (int) screenMap[perm.get(0)].x,
                (int) screenMap[perm.get(0)].y);

        for (int j = 0; j < n - 1; j++) {
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.BLACK);
            g2d.drawLine(
                    (int) screenMap[perm.get(j)].x,
                    (int) screenMap[perm.get(j)].y,
                    (int) screenMap[perm.get(j + 1)].x,
                    (int) screenMap[perm.get(j + 1)].y);
            g2d.setColor(Color.RED);
            g2d.fill(new Ellipse2D.Double(
                    screenMap[perm.get(j)].x - 2,
                    screenMap[perm.get(j)].y - 2, 4, 4));
        }

    }

    private Point[] transform(Point[] map) {
        Point[] screenPoints = new Point[map.length];
        double xMin = map[0].x;
        double xMax = map[0].x;
        double yMin = map[0].y;
        double yMax = map[0].y;
        for (int i = 1; i < map.length; i++) {
            if (map[i].x < xMin) xMin = map[i].x;
            if (map[i].x > xMax) xMax = map[i].x;
            if (map[i].y < yMin) yMin = map[i].y;
            if (map[i].y > yMax) yMax = map[i].y;
        }
        int xSpan = (int) (xMax - xMin);
        int xMargin = (int) xMin;
        int ySpan = (int) (yMax - yMin);
        int yMargin = (int) yMin;
        float xf = (w - 100) / xSpan;
        float yf = (h - 150) / ySpan;
        for (int i = 0; i < map.length; i++) {
            screenPoints[i] = new Point((map[i].x - xMargin) * xf + 50,
                    (map[i].y - yMargin) * yf + 50);
        }
        return screenPoints;
    }
}
