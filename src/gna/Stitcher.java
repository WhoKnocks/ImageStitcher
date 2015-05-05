package gna;

import libpract.Position;
import libpract.Stitch;

import java.util.*;

/**
 * Implement the methods stitch, seam and floodfill.
 */
public class Stitcher {
    /**
     * Return the sequence of positions on the seam. The first position in the
     * sequence is (0, 0) and the last is (width - 1, height - 1). Each position
     * on the seam must be adjacent to its predecessor and successor (if any).
     * Positions that are diagonally adjacent are considered adjacent.
     * <p>
     * image1 and image2 are both non-null and have equal dimensions.
     * <p>
     * Remark: Here we use the default computer graphics coordinate system,
     * illustrated in the following image:
     * <p>
     * +-------------> X
     * |  +---+---+
     * |  | A | C |
     * |  +---+---+
     * |  | B | D |
     * |  +---+---+
     * Y v
     * <p>
     * The historical reasons behind using this layout is explained on the following
     * website: http://programarcadegames.com/index.php?chapter=introduction_to_graphics
     * <p>
     * Position (x, y) corresponds to the pixels image1[x][y] and image2[x][y]. This
     * also means that, when an automated test mentioned that it used the array
     * {{A,B},{C,D}} as a test image, this corresponds to the image layout as shown in
     * the illustration above.
     */
    public List<Position> seam(int[][] image1, int[][] image2) {
        long start = System.currentTimeMillis();
        Dijkstra d = new Dijkstra(image1, image2);
        long duration = (System.currentTimeMillis() - start);
        System.out.println("Dijkstra: " + duration);
        return d.computePaths();
    }


    /**
     * Apply the floodfill algorithm described in the assignment to mask. You can assume the mask
     * contains a seam from the upper left corner to the bottom right corner. Each position in the
     * seam is adjacent to only one other seam position. For example, the seam will never go vertical
     * and then horizontal (it will immediately go diagonal). The seam is represented using Stitch.SEAM
     * and all other positions contain the default value Stitch.EMPTY. So your algorithm must replace
     * all Stitch.EMPTY values with either Stitch.IMAGE1 or Stitch.IMAGE2.
     * <p>
     * Positions left to the seam should contain Stitch.IMAGE1, and those right to the seam
     * should contain Stitch.IMAGE2. You can run `ant test` for a basic (but not complete) test
     * to check whether your implementation does this properly.
     */
    public void floodfill(Stitch[][] mask) {
        long start = System.currentTimeMillis();
        mark(mask, Stitch.IMAGE1);
        mark(mask, Stitch.IMAGE2);
        long duration = (System.currentTimeMillis() - start);
        System.out.println("Floodfill: " + duration);
    }

    private void mark(Stitch[][] mask, Stitch image) {

        Position startPositon = new Position(0, 0);

        //find a start point
        for (int y = 0; y < mask.length; y++) {
            for (int x = 0; x < mask[0].length; x++) {
                if (mask[y][x] == Stitch.EMPTY) {
                    startPositon = new Position(x, y);
                    break;
                }
            }
        }

        //linked-list is ideal for a queue implementation
        Queue<Position> posQueue = new LinkedList<>();

        //map to see if a node is processed
        HashMap<Position, Boolean> isProcessed = new HashMap<>();

        //add the start point to the empty queue
        posQueue.add(startPositon);

        isProcessed.put(startPositon, false);
        while (!posQueue.isEmpty()) {
            Position p = posQueue.poll();

            if (mask[p.getY()][p.getX()] == Stitch.EMPTY && !isProcessed.get(p)) {
                mask[p.getY()][p.getX()] = image;
                isProcessed.put(p, true);
                List<Position> positionList = getNeighborsOfPoint(p.getX(), p.getY(), mask);
                for (Position position : positionList) {
                    isProcessed.put(position, false);
                    posQueue.add(position);
                }
            }
        }


    }

    private List<Position> getNeighborsOfPoint(int x, int y, Stitch[][] mask) {
        List<Position> neighbors = new ArrayList<Position>();
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                if (xx == 0 && yy == 0) {
                    continue; // You are not neighbor to yourself
                }

                if (Math.abs(xx) + Math.abs(yy) > 1) {
                    continue;
                }

                if (isOnMap(x + xx, y + yy, mask)) {
                    neighbors.add(new Position(x + xx, y + yy));
                }
            }
        }
        return neighbors;
    }

    public boolean isOnMap(int x, int y, Stitch[][] mask) {
        return x >= 0 && y >= 0 && x < mask[0].length && y < mask.length;
    }


    /**
     * Return the mask to stitch two images together. The seam runs from the upper
     * left to the lower right corner, where in general the rightmost part comes from
     * the first image (but remember that the seam can be complex, see the spiral example
     * in the assignment). A pixel in the mask is Stitch.IMAGE1 on the places where
     * image1 should be used, and Stitch.IMAGE2 where image2 should be used. On the seam
     * record a value of Stitch.SEAM.
     * <p>
     * ImageCompositor will only call this method (not seam and floodfill) to
     * stitch two images.
     * <p>
     * image1 and image2 are both non-null and have equal dimensions.
     */
    public Stitch[][] stitch(int[][] image1, int[][] image2) {
        long start = System.currentTimeMillis();
        HashSet<Position> positions = new HashSet<>(seam(image1, image2));

        Stitch[][] stitches = new Stitch[image1.length][image1[0].length];

        for (int y = 0; y < stitches.length; y++) {
            for (int x = 0; x < stitches[0].length; x++) {
                if (positions.contains(new Position(x, y))) {
                    stitches[y][x] = Stitch.SEAM;
                } else {
                    stitches[y][x] = Stitch.EMPTY;
                }
            }
        }

        floodfill(stitches);

        long duration = (System.currentTimeMillis() - start);
        System.out.println("Stitch: " + duration);
        return stitches;
    }
}


