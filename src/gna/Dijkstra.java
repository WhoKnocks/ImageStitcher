package gna;


import libpract.Position;

import java.util.*;

/**
 * Created by GJ on 26/04/2015.
 */
public class Dijkstra {

    public int[][] image1;
    public int[][] image2;

    public HashMap<Position, Vertex> vertexes;

    public Dijkstra(int[][] image1, int[][] image2) {
        this.image1 = image1;
        this.image2 = image2;
        vertexes = new HashMap<>();

        for (int i = 0; i < image1.length; i++) {
            for (int j = 0; j < image1[i].length; j++) {
                Vertex newVertex = new Vertex(new Position(j, i));
                vertexes.put(newVertex.getPosition(), newVertex);
            }
        }
    }

    /**
     * Stelt de vertex voor in de grafe
     * Gebruikt een de Position klasse en heeft een Edge naar elke neighbor
     * Houdt de minimale afstand naar deze Vertex bij, inclusief de vorige 'parent' Vertex.
     * <p>
     * De neighbors worden pas toegekend wanneer deze nodig zijn.
     */
    class Vertex implements Comparable<Vertex> {
        public Position position;
        public Edge[] neighbors = null;
        public int minDistance = Integer.MAX_VALUE;
        public Vertex previous;

        public int compareTo(Vertex other) {
            return Integer.compare(minDistance, other.minDistance);
        }

        public Vertex(Position position) {
            this.position = position;
        }

        public Position getPosition() {
            return position;
        }

        public void setPosition(Position position) {
            this.position = position;
        }

        public Edge[] getNeighbors() {
            if (neighbors == null) {
                List<Position> neighborPositions = getNeighborsOfPoint(position.getX(), position.getY());

                neighbors = new Edge[neighborPositions.size()];

                for (int i = 0; i < neighbors.length; i++) {
                    neighbors[i] = new Edge(vertexes.get(neighborPositions.get(i)),
                            ImageCompositor.pixelSqDistance(
                                    image1[neighborPositions.get(i).getY()][neighborPositions.get(i).getX()],
                                    image2[neighborPositions.get(i).getY()][neighborPositions.get(i).getX()]));

                }
            }
            return neighbors;
        }
    }

    /**
     * Stelt de Edge in de grafe voor
     * Elke Edge heeft een gewicht
     */
    class Edge {
        public Vertex neighbor;
        public int weight;

        public Edge(Vertex neighbor, int weight) {
            this.neighbor = neighbor;
            this.weight = weight;
        }
    }

    /**
     * Berekent alle neighbors op een elegante manier (geen if structuur)
     *
     * @param x
     * @param y
     * @return
     */
    private List<Position> getNeighborsOfPoint(int x, int y) {
        List<Position> neighbors = new ArrayList<>();
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                if (xx == 0 && yy == 0) {
                    continue; // You are not neighbor to yourself
                }

                if (isOnImage(x + xx, y + yy)) {
                    neighbors.add(new Position(x + xx, y + yy));
                }
            }
        }
        return neighbors;
    }

    /**
     * Controleert of een gegeven x en y coordinaat op de image vallen
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isOnImage(int x, int y) {
        return x >= 0 && y >= 0 && x < image1[0].length && y < image1.length;
    }

    /**
     * Berekent het korste pad volgens het dijkstra algoritme
     *
     * @return
     */
    public List<Position> computeShortestPath() {
        Vertex start = vertexes.get(new Position(0, 0));
        Vertex end = null;
        start.minDistance = 0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(start);

        while (!vertexQueue.isEmpty()) {
            Vertex polled = vertexQueue.poll();

            //check if the end is polled
            if (polled.getPosition().getX() == image1[0].length - 1 && polled.getPosition().getY() == image1.length - 1) {
                end = polled;
                break;
            }

            // Visit each neighbor of the polled vertex
            for (Edge e : polled.getNeighbors()) {
                Vertex neighbor = e.neighbor;
                int weight = e.weight;
                int distanceThroughPolled = polled.minDistance + weight;

                if (distanceThroughPolled < neighbor.minDistance) {
                    neighbor.minDistance = distanceThroughPolled;
                    neighbor.previous = polled;
                    vertexQueue.add(neighbor);
                }
            }
        }

        List<Position> path = new ArrayList<>();
        for (Vertex vertex = end; vertex != null; vertex = vertex.previous)
            path.add(vertex.getPosition());

        Collections.reverse(path);
        return path;
    }


}