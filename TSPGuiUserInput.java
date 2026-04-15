import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class TSPGuiUserInput extends JFrame {
    private int cityCount = 0;
    private final java.util.List<Point> cities = new ArrayList<>();
    private final java.util.List<Integer> path = new ArrayList<>();
    private boolean tspSolved = false;
    private final DrawPanel panel = new DrawPanel();
    private Point mouseHover = null;

    public TSPGuiUserInput() {
        setTitle("TSP - Clear City Selection and Visualization");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ask user for number of cities
        String input = JOptionPane.showInputDialog(this, "Enter number of cities:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No input provided. Exiting.");
            System.exit(0);
        }

        try {
            cityCount = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number. Exiting.");
            System.exit(0);
        }

        add(panel);
        setVisible(true);

        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (cities.size() < cityCount && !tspSolved) {
                    cities.add(e.getPoint());
                    panel.repaint();

                    if (cities.size() == cityCount) {
                        solveTSP();
                        tspSolved = true;
                        panel.repaint();
                        JOptionPane.showMessageDialog(panel, "TSP Solved!");
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                mouseHover = e.getPoint();
                panel.repaint();
            }
        });
    }

    private void solveTSP() {
        boolean[] visited = new boolean[cityCount];
        int current = 0;
        path.add(current);
        visited[current] = true;

        for (int i = 1; i < cityCount; i++) {
            int nearest = -1;
            double minDist = Double.MAX_VALUE;

            for (int j = 0; j < cityCount; j++) {
                if (!visited[j]) {
                    double dist = cities.get(current).distance(cities.get(j));
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = j;
                    }
                }
            }

            visited[nearest] = true;
            path.add(nearest);
            current = nearest;
        }

        path.add(path.get(0)); // Return to start
    }

    class DrawPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));

            // Show hover dot for preview
            if (!tspSolved && mouseHover != null && cities.size() < cityCount) {
                g.setColor(Color.LIGHT_GRAY);
                g.drawOval(mouseHover.x - 6, mouseHover.y - 6, 12, 12);
            }

            // Connect clicked cities while selecting
            if (!tspSolved && cities.size() > 1) {
                g.setColor(Color.GRAY);
                for (int i = 0; i < cities.size() - 1; i++) {
                    Point p1 = cities.get(i);
                    Point p2 = cities.get(i + 1);
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            // Draw clicked cities
            for (int i = 0; i < cities.size(); i++) {
                Point p = cities.get(i);
                if (!tspSolved) {
                    g.setColor(Color.ORANGE); // Clear color for selection
                } else if (i == path.get(0)) {
                    g.setColor(Color.GREEN); // Start city
                } else {
                    g.setColor(Color.RED); // Others
                }
                g.fillOval(p.x - 6, p.y - 6, 12, 12);
                g.setColor(Color.BLACK);
                g.drawString("City " + i, p.x + 8, p.y - 8);
            }

            // Draw TSP path
            if (tspSolved) {
                g.setColor(Color.BLUE);
                for (int i = 0; i < path.size() - 1; i++) {
                    Point p1 = cities.get(path.get(i));
                    Point p2 = cities.get(path.get(i + 1));
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);

                    // Step number in middle
                    int midX = (p1.x + p2.x) / 2;
                    int midY = (p1.y + p2.y) / 2;
                    g.setColor(Color.MAGENTA);
                    g.drawString((i + 1) + "", midX, midY);
                    g.setColor(Color.BLUE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TSPGuiUserInput::new);
    }
}