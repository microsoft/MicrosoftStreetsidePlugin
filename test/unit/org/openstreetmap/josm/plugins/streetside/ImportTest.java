// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.streetside;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.imageio.IIOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.streetside.cubemap.CubemapUtils;
import org.openstreetmap.josm.plugins.streetside.utils.ImageImportUtil;

/**
 * Test the importation of images.
 *
 * @author nokutu
 *
 */

@RunWith(JUnit4.class)
public class ImportTest {

  /**
   * Test the importation of images in PNG format.
   */
  @Test
  public void importNoTagsTest() throws IOException {
    File image = new File(getClass().getResource("/exifTestImages/untagged.jpg").getFile());
    StreetsideImportedImage img = ImageImportUtil.readImagesFrom(image, new LatLon(0, 0)).get(0);
    assertEquals(0, img.getMovingHe(), 0.01);
    assertTrue(new LatLon(0, 0).equalsEpsilon(img.getMovingLatLon()));
  }

  /**
   * Test if provided an invalid file, the proper exception is thrown.
   *
   * @throws IOException
   */
  @Test(expected = IIOException.class)
  public void testInvalidFiles() throws IOException {
    StreetsideImportedImage img = new StreetsideImportedImage(CubemapUtils.IMPORTED_ID, new LatLon(0, 0), 0, null);
    assertEquals(null, img.getImage());
    assertEquals(null, img.getFile());

    img = new StreetsideImportedImage(CubemapUtils.IMPORTED_ID, new LatLon(0, 0), 0, new File(""));
    assertEquals(new File(""), img.getFile());
    img.getImage();
  }
}
