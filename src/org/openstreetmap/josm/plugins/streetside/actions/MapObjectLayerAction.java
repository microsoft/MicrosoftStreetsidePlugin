// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.streetside.actions;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.util.GuiHelper;
import org.openstreetmap.josm.plugins.streetside.StreetsidePlugin;
import org.openstreetmap.josm.plugins.streetside.gui.layer.MapObjectLayer;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.ImageProvider.ImageSizes;

public class MapObjectLayerAction extends JosmAction {

  private static final long serialVersionUID = 302786746122185446L;


  public MapObjectLayerAction() {
    super(
      I18n.tr("Mapillary object layer"),
      StreetsidePlugin.LOGO.setSize(ImageSizes.DEFAULT),
      I18n.tr("Displays the layer displaying the map objects detected by the Bing API"),
      null,
      false,
      "streetsideObjectLayer",
      false
    );
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    GuiHelper.runInEDTAndWait(() -> {
      // Synchronization lock must be held by EDT thread
      // See {@link LayerManager#addLayer(org.openstreetmap.josm.gui.layer.Layer, boolean)}.
      synchronized (MainApplication.getLayerManager()) {
        if (!MainApplication.getLayerManager().containsLayer(MapObjectLayer.getInstance())) {
          MainApplication.getLayerManager().addLayer(MapObjectLayer.getInstance());
        }
      }
    });
  }
}
