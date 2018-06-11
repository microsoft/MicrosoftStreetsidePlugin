// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.streetside;

import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.streetside.cubemap.CubemapUtils;
import org.openstreetmap.josm.plugins.streetside.utils.StreetsideProperties;

public class StreetsideCubemap extends StreetsideAbstractImage implements Comparable<StreetsideAbstractImage>{

	private static Map<String,Map<String,BufferedImage>> face2TilesMap = new HashMap<String,Map<String,BufferedImage>>();

	/**
	* If two values for field cd differ by less than EPSILON both values are considered equal.
	*/
	private static final float EPSILON = 1e-5f;


	/**
	   * Main constructor of the class StreetsideCubemap
	   *
	   * @param key    The unique identifier of the cubemap.
	   * @param latLon The latitude and longitude where it is positioned.
	   * @param cd     The direction of the images in degrees, meaning 0 north.
	   */

	  public StreetsideCubemap(String quadId, LatLon latLon, double ca) {
	    super(quadId, latLon, ca);
	    face2TilesMap = new HashMap();

	    EnumSet.allOf(CubemapUtils.CubemapFaces.class).forEach(face -> {
			face2TilesMap.put(face.getValue(),new HashMap<String,BufferedImage>());
		});

	  }

	  public StreetsideCubemap(String quadId, LatLon latLon) {
	    super(quadId, latLon, 0.0);
	  }

	  // Default constructor for Jackson/JSON Deserializattion
	  public StreetsideCubemap(String quadId, LatLon latlon, List<StreetsideImage> tiles, double he) {
	    super(quadId, latlon, he);
	  }

	/**
	 * @return the face2TilesMap
	 */
	public Map<String, Map<String,BufferedImage>> getFace2TilesMap() {
		return face2TilesMap;
	}

    @Override
	  public int compareTo(StreetsideAbstractImage image) {
	    if (image instanceof StreetsideImage) {
	      return id.compareTo(((StreetsideImage) image).getId());
	    }
	    return hashCode() - image.hashCode();
	  }

	  @Override
	  public int hashCode() {
	    return id.hashCode();
	  }

	  @Override
	  public void stopMoving() {
	    super.stopMoving();
	  }

	  @Override
	  public void turn(double ca) {
	    super.turn(ca);
	  }

	public int getHeight() {
		return StreetsideProperties.SHOW_HIGH_RES_STREETSIDE_IMAGERY.get().booleanValue()?1016:510;
	}

	public void resetFaces2TileMap() {
		face2TilesMap = new HashMap<>();

		EnumSet.allOf(CubemapUtils.CubemapFaces.class).forEach(face -> {
			face2TilesMap.put(face.getValue(), new HashMap<String, BufferedImage>());
		});
	}
}