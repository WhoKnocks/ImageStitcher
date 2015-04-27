package gna;

import libpract.Position;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * Created by GJ on 26/04/2015.
 */
public class StitcherTest {

    @Test
    public void seamTest() {
        Random random = new Random();

        Stitcher stitcher = new Stitcher();

        int[][] image1 = new int[500][700];
        int[][] image2 = new int[500][700];

        for (int y = 0; y < 500; y++) {
            for (int x = 0; x < 700; x++) {
                image1[y][x] = random.nextInt(100);
                image2[y][x] = random.nextInt(100);
            }
        }


        List<Position> positions = stitcher.seam(image1, image2);

        assertTrue(positions.get(0).getX() == 0);
        assertTrue(positions.get(0).getY() == 0);

        assertTrue(positions.get(positions.size() - 1).getX() == image1[0].length - 1);
        assertTrue(positions.get(positions.size() - 1).getY() == image1.length - 1);

        for (int i = 0; i < positions.size(); i++) {
            if (i != positions.size() - 2) {
                break;
            }
            assertTrue(positions.get(i).isAdjacentTo(positions.get(i + 1)));
        }
    }

    public boolean isNeighbor(int x, int y, int x2, int y2) {
        int difX = Math.abs(x - x2);
        int difY = Math.abs(y - y2);

        if (difX <= 1 && difY <= 1) {
            return true;
        }
        return false;
    }
}
