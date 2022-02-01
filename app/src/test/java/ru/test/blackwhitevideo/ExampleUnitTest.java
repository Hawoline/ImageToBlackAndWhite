package ru.test.blackwhitevideo;

import junit.framework.TestCase;
import org.junit.Test;
import ru.test.blackwhitevideo.model.ImageMatrix;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testMatrix() {
        int[] pixels = {
                255, 255, 255, 10, 10, 10,
                255, 255, 255, 10, 10, 10,
                255, 255, 255, 10, 10, 10,
                255, 255, 255, 10, 10, 10,
                255, 255, 255, 10, 10, 10,
                255, 255, 255, 10, 10, 10,
                255, 255, 255, 10, 10, 10,
                255, 255, 255, 10, 10, 10
        };
        int[] filter = {
                1, 0, -1,
                1, 0, -1,
                1, 0, -1
        };

        ImageMatrix imageMatrix = new ImageMatrix(pixels, 6, 8);
        imageMatrix.setFilter(filter);

        int[] expectedResult = {
                0, 255, 255, 0,
                0, 255, 255, 0,
                0, 255, 255, 0,
                0, 255, 255, 0,
                0, 255, 255, 0,
                0, 255, 255, 0
        };

        int[] filteredMatrix = imageMatrix.colorMatrixToBlackAndWhite().getMatrix();
        for (int i = 0; i < filteredMatrix.length; i++) {
            TestCase.assertEquals(expectedResult[i], filteredMatrix[i]);
        }
    }
}