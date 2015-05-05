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
    public Vertex start;
    public Set<Vertex> isPolledSet = new HashSet<>();


    public Dijkstra(int[][] image1, int[][] image2) {
        this.image1 = image1;
        this.image2 = image2;
        vertexes = new HashMap<>();

        for (int i = 0; i < image1.length; i++) {
            for (int j = 0; j < image1[i].length; j++) {
                Vertex newVertex = new Vertex(new Position(j, i));
                if (i == 0 && j == 0) {
                    start = newVertex;
                }
                vertexes.put(newVertex.getPosition(), newVertex);
            }
        }
    }

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

    class Edge {
        public Vertex neighbor;
        public int weight;

        public Edge(Vertex neighbor, int weight) {
            this.neighbor = neighbor;
            this.weight = weight;
        }

    }

    private List<Position> getNeighborsOfPoint(int x, int y) {
        List<Position> neighbors = new ArrayList<Position>();
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                if (xx == 0 && yy == 0) {
                    continue; // You are not neighbor to yourself
                }

                if (isOnMap(x + xx, y + yy)) {
                    neighbors.add(new Position(x + xx, y + yy));
                }
            }
        }
        return neighbors;
    }

    public boolean isOnMap(int x, int y) {
        return x >= 0 && y >= 0 && x < image1[0].length && y < image1.length;
    }

    public List<Position> computePaths() {
        Vertex end = start;

        start.minDistance = 0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(start);

        while (!vertexQueue.isEmpty()) {
            Vertex polled = vertexQueue.poll();
            isPolledSet.add(polled);

            //check if the end is polled
            if (polled.getPosition().getX() == image1[0].length - 1 && polled.getPosition().getY() == image1.length - 1) {
                end = polled;
                break;
            }

            // Visit each edge exiting polled
            for (Edge e : polled.getNeighbors()) {
                Vertex neighbor = e.neighbor;
                int weight = e.weight;
                int distanceThroughPolled = polled.minDistance + weight;

                if (distanceThroughPolled < neighbor.minDistance) {
                    vertexQueue.remove(neighbor);

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
