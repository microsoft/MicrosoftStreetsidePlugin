// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.streetside.cubemap;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openstreetmap.josm.plugins.streetside.utils.StreetsideProperties;
import org.openstreetmap.josm.tools.Logging;

import javafx.application.Platform;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

@SuppressWarnings({ "restriction"})
public class GraphicsUtils {

	public static javafx.scene.image.Image convertBufferedImage2JavaFXImage(BufferedImage bf) {
		WritableImage wr = null;
		if (bf != null) {
			wr = new WritableImage(bf.getWidth(), bf.getHeight());
			PixelWriter pw = wr.getPixelWriter();
			for (int x = 0; x < bf.getWidth(); x++) {
				for (int y = 0; y < bf.getHeight(); y++) {
					pw.setArgb(x, y, bf.getRGB(x, y));
				}
			}
		}
		return wr;
	}

	public static class PlatformHelper {

        public static void run(Runnable treatment) {
            if(treatment == null) throw new IllegalArgumentException("The treatment to perform can not be null");

            if(Platform.isFxApplicationThread()) treatment.run();
            else Platform.runLater(treatment);
        }
    }

	public static BufferedImage buildMultiTiledCubemapFaceImage(BufferedImage[] tiles) {

		BufferedImage res = null;

		int pixelBuffer = StreetsideProperties.SHOW_HIGH_RES_STREETSIDE_IMAGERY.get()?2:1;

		tiles = cropMultiTiledImages(tiles, pixelBuffer);

		int rows = StreetsideProperties.SHOW_HIGH_RES_STREETSIDE_IMAGERY.get()?4:2; //we assume the no. of rows and cols are known and each chunk has equal width and height
        int cols = StreetsideProperties.SHOW_HIGH_RES_STREETSIDE_IMAGERY.get()?4:2;

        // TODO: images still not backwards after cropping
        /*for(int i=0; i<tiles.length;i++) {
        	File outputfileBefore = new File("/Users/renerr18/Desktop/TileImagesTest/tile16" + Long.valueOf(System.currentTimeMillis()).toString() + "CroppingAfter.jpeg");
    		//File outputfileAfter= new File("/Users/renerr18/Desktop/TileImagesTest/tile16" + Long.valueOf(System.currentTimeMillis()).toString() + "ImageAfter" + Integer.valueOf(i).toString() + ".jpeg");
    	    try {
    			ImageIO.write(tiles[i], "jpeg", outputfileBefore);
    			//ImageIO.write(res[i], "jpeg", outputfileAfter);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }*/




        int chunkWidth, chunkHeight;

        chunkWidth = tiles[0].getWidth();
        chunkHeight = tiles[0].getHeight();

        //Initializing the final image
        BufferedImage img = new BufferedImage(chunkWidth*cols, chunkHeight*rows, BufferedImage.TYPE_INT_ARGB);

        int num = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                img.createGraphics().drawImage(tiles[num], chunkWidth * j, chunkHeight * i, null);
                // TODO: build images without making them backwards, so no flipping is required
                // (Change previous line?)
                // images are reversed, flip them
                // TODO: flip image - drawImage produces backwards images
                /*AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(StreetsideProperties.SHOW_HIGH_RES_STREETSIDE_IMAGERY.get()?1018:512, 0);
                AffineTransformOp op = new AffineTransformOp(tx,
                        AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                res = op.filter(img, null);*/

                // BufferedImage for mirror image

                int width = StreetsideProperties.SHOW_HIGH_RES_STREETSIDE_IMAGERY.get()?1014:510;
                int height = StreetsideProperties.SHOW_HIGH_RES_STREETSIDE_IMAGERY.get()?1014:510;

                res = new BufferedImage(StreetsideProperties.SHOW_HIGH_RES_STREETSIDE_IMAGERY.get()?1014:510, StreetsideProperties.SHOW_HIGH_RES_STREETSIDE_IMAGERY.get()?1014:510,
                                                BufferedImage.TYPE_INT_ARGB);

                // Create mirror image pixel by pixel
                for (int y = 0; y < height; y++)
                {
                    for (int lx = 0, rx = width - 1; lx < width; lx++, rx--)
                    {
                        // lx starts from the left side of the image
                        // rx starts from the right side of the image
                        // lx is used since we are getting pixel from left side
                        // rx is used to set from right side
                        // get source pixel value
                        int p = img.getRGB(lx, y);

                        // set mirror image pixel value
                        res.setRGB(rx, y, p);
                    }
                }
                num++;
            }
        }

        Logging.info("Image concatenated.....");

        File outputfileBefore = new File("/Users/renerr18/Desktop/TileImagesTest/tile16" + Long.valueOf(System.currentTimeMillis()).toString() + "StitchingAfter.jpeg");
		//File outputfileAfter= new File("/Users/renerr18/Desktop/TileImagesTest/tile16" + Long.valueOf(System.currentTimeMillis()).toString() + "ImageAfter" + Integer.valueOf(i).toString() + ".jpeg");
	    try {
			ImageIO.write(res, "jpeg", outputfileBefore);
			//ImageIO.write(res[i], "jpeg", outputfileAfter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return res;
	}

	public static BufferedImage rotateImage(BufferedImage bufImg) {
		AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
	    tx.translate(-bufImg.getWidth(null), -bufImg.getHeight(null));
	    AffineTransformOp op = new AffineTransformOp(tx,
	        AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	    bufImg = op.filter(bufImg, null);
	    return bufImg;
	}

	private static BufferedImage[] cropMultiTiledImages(BufferedImage[] tiles, int pixelBuffer) {

		BufferedImage[] res = new BufferedImage[tiles.length];

			for(int i=0; i<tiles.length;i++) {
				// TODO: compare and, if necessary, fix cropping!
				if(StreetsideProperties.SHOW_HIGH_RES_STREETSIDE_IMAGERY.get()) {
					res[i] = tiles[i].getSubimage(pixelBuffer, pixelBuffer, 256-pixelBuffer, 256-pixelBuffer);
					/*File outputfileBefore = new File("/Users/renerr18/Desktop/TileImagesTest/tile16" + Long.valueOf(System.currentTimeMillis()).toString() + "ImageBefore" + Integer.valueOf(i).toString() + ".jpeg");
					File outputfileAfter= new File("/Users/renerr18/Desktop/TileImagesTest/tile16" + Long.valueOf(System.currentTimeMillis()).toString() + "ImageAfter" + Integer.valueOf(i).toString() + ".jpeg");
				    try {
						ImageIO.write(tiles[i], "jpeg", outputfileBefore);
						ImageIO.write(res[i], "jpeg", outputfileAfter);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				} else {
					res[i] = tiles[i].getSubimage(pixelBuffer, pixelBuffer, 256-pixelBuffer, 256-pixelBuffer);
					/*File outputfileBefore = new File("/Users/renerr18/Desktop/TileImagesTest/tile4" + Long.valueOf(System.currentTimeMillis()).toString() + "ImageBefore" + Integer.valueOf(i).toString() + ".jpeg");
					File outputfileAfter= new File("/Users/renerr18/Desktop/TileImagesTest/tile4" + Long.valueOf(System.currentTimeMillis()).toString() + "ImageAfter" + Integer.valueOf(i).toString() + ".jpeg");
					try {
						ImageIO.write(tiles[i], "jpeg", outputfileBefore);
						ImageIO.write(res[i], "jpeg", outputfileAfter);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				}
			}
		return res;
	}
}