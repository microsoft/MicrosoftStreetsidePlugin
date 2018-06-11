// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.streetside.cache;

import java.net.URL;
import java.util.HashMap;

import org.openstreetmap.josm.data.cache.BufferedImageCacheEntry;
import org.openstreetmap.josm.data.cache.JCSCachedTileLoaderJob;
import org.openstreetmap.josm.plugins.streetside.utils.StreetsideURL.VirtualEarth;

/**
 * Stores the downloaded pictures locally.
 *
 * @author nokutu
 *
 */
public class StreetsideCache extends JCSCachedTileLoaderJob<String, BufferedImageCacheEntry> {

	private final URL url;
	private final String id;

	/**
	 * Types of images.
	 *
	 * @author nokutu
	 */
	public enum Type {
		/** Full quality image */
		FULL_IMAGE,
		/** Low quality image */
		THUMBNAIL,
		/** cubemap faces */
		// TODO: one class per cache/load required? really?!
		CUBEMAP/*,
		CUBEMAP_FRONT,
		CUBEMAP_RIGHT,
		CUBEMAP_BACK,
		CUBEMAP_LEFT,
		CUBEMAP_UP,
		CUBEMAP_DOWN*/
	}

	/**
	 * Main constructor.
	 *
	 * @param id
	 *          The id of the image.
	 * @param type
	 *          The type of image that must be downloaded (THUMBNAIL or
	 *          FULL_IMAGE).
	 */
	public StreetsideCache(final String id, final Type type) {
		super(Caches.ImageCache.getInstance().getCache(), 50000, 50000, new HashMap<>());
		if (id == null || type == null) {
			this.id = null;
			url = null;
		} else {
			//this.id = id + (type == Type.FULL_IMAGE ? ".FULL_IMAGE" : ".THUMBNAIL");
			// Add an "01" to the Streetside imageId in order to get a frontal thumbnail image for the display
			this.id = id;
			url = VirtualEarth.streetsideTile(id, type == Type.THUMBNAIL);
		}
	}

	@Override
	public String getCacheKey() {
		return id;
	}

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	protected BufferedImageCacheEntry createCacheEntry(byte[] content) {
		return new BufferedImageCacheEntry(content);
	}

	@Override
	protected boolean isObjectLoadable() {
		if (cacheData == null) {
			return false;
		}
		final byte[] content = cacheData.getContent();
		return content != null && content.length > 0;
	}
}
