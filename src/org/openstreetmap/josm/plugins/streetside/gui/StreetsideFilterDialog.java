// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.streetside.gui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Calendar;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.SideButton;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.widgets.DisableShortcutsOnFocusGainedTextField;
import org.openstreetmap.josm.plugins.streetside.StreetsideAbstractImage;
import org.openstreetmap.josm.plugins.streetside.StreetsideDataListener;
import org.openstreetmap.josm.plugins.streetside.StreetsideImage;
import org.openstreetmap.josm.plugins.streetside.StreetsideImportedImage;
import org.openstreetmap.josm.plugins.streetside.StreetsideLayer;
import org.openstreetmap.josm.tools.ImageProvider;

import org.openstreetmap.josm.plugins.streetside.model.ImageDetection;
import org.openstreetmap.josm.plugins.streetside.model.UserProfile;

/**
 * ToggleDialog that lets you filter the images that are being shown.
 *
 * @author nokutu
 * @see StreetsideFilterChooseSigns
 */
public class StreetsideFilterDialog extends ToggleDialog implements StreetsideDataListener {

  private static final long serialVersionUID = -6465343076946050909L;

  private static StreetsideFilterDialog instance;

  private static final String[] TIME_LIST = {tr("Years"), tr("Months"), tr("Days")};

  private static final long[] TIME_FACTOR = new long[]{
    31_536_000_000L, // = 365 * 24 * 60 * 60 * 1000 = number of ms in a year
    2_592_000_000L, // = 30 * 24 * 60 * 60 * 1000 = number of ms in a month
    86_400_000 // = 24 * 60 * 60 * 1000 = number of ms in a day
  };

  private final JCheckBox filterByDateCheckbox;
  /**
   * Spinner to choose the range of dates.
   */
  private final SpinnerNumberModel spinnerModel;

  private final JCheckBox imported = new JCheckBox(tr("Imported images"));
  private final JCheckBox downloaded = new JCheckBox(new DownloadCheckBoxAction());
  private final JCheckBox onlySigns = new JCheckBox(new OnlySignsAction());
  private final JComboBox<String> time;
  private final JTextField user;

  private final JButton signChooser = new JButton(new SignChooserAction());

  private StreetsideFilterDialog() {
    super(tr("Streetside filter"), "streetside-filter", tr("Open Streetside filter dialog"), null, 200,
        false, StreetsidePreferenceSetting.class);

    signChooser.setEnabled(false);
    JPanel signChooserPanel = new JPanel();
    signChooserPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    signChooserPanel.add(signChooser);

    JPanel fromPanel = new JPanel();
    fromPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    filterByDateCheckbox = new JCheckBox(tr("Not older than: "));
    fromPanel.add(filterByDateCheckbox);
    spinnerModel = new SpinnerNumberModel(1.0, 0, 10000, .1);
    JSpinner spinner = new JSpinner(spinnerModel);
    spinner.setEnabled(false);
    fromPanel.add(spinner);
    time = new JComboBox<>(TIME_LIST);
    time.setEnabled(false);
    fromPanel.add(time);

    filterByDateCheckbox.addItemListener(itemE -> {
      spinner.setEnabled(filterByDateCheckbox.isSelected());
      time.setEnabled(filterByDateCheckbox.isSelected());
    });

    JPanel userSearchPanel = new JPanel();
    userSearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    user = new DisableShortcutsOnFocusGainedTextField(10);
    user.addActionListener(new UpdateAction());
    userSearchPanel.add(new JLabel(tr("User")));
    userSearchPanel.add(user);

    imported.setSelected(true);
    downloaded.setSelected(true);

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.LINE_START;
    panel.add(downloaded, c);
    c.gridx = 1;
    panel.add(imported, c);
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    panel.add(fromPanel, c);
    c.gridy = 2;
    panel.add(userSearchPanel, c);
    c.gridwidth = 1;
    c.gridy = 3;
    panel.add(onlySigns, c);
    c.gridx = 1;
    panel.add(signChooserPanel, c);

    createLayout(panel, true, Arrays.asList(new SideButton(new UpdateAction()), new SideButton(new ResetAction())));
  }

  /**
   * @return the unique instance of the class.
   */
  public static synchronized StreetsideFilterDialog getInstance() {
    if (instance == null)
      instance = new StreetsideFilterDialog();
    return instance;
  }

  @Override
  public void imagesAdded() {
    refresh();
  }

  @Override
  public void selectedImageChanged(StreetsideAbstractImage oldImage, StreetsideAbstractImage newImage) {
    // Do nothing when image selection changed
  }

  /**
   * Resets the dialog to its default state.
   */
  public void reset() {
    imported.setSelected(true);
    downloaded.setSelected(true);
    onlySigns.setEnabled(true);
    onlySigns.setSelected(false);
    user.setText("");
    time.setSelectedItem(TIME_LIST[0]);
    spinnerModel.setValue(1);
    refresh();
  }

  /**
   * Applies the selected filter.
   */
  public synchronized void refresh() {
    final boolean layerVisible = StreetsideLayer.hasInstance() && StreetsideLayer.getInstance().isVisible();
    final boolean imported = this.imported.isSelected();
    final boolean downloaded = this.downloaded.isSelected();
    final boolean timeFilter = filterByDateCheckbox.isSelected();
    final boolean onlySigns = this.onlySigns.isSelected();

    // This predicate returns true is the image should be made invisible
    Predicate<StreetsideAbstractImage> shouldHide =
      img -> {
        if (!layerVisible) {
          return true;
        }
        if (timeFilter && checkValidTime(img)) {
          return true;
        }
        if (!imported && img instanceof StreetsideImportedImage) {
          return true;
        }
        if (img instanceof StreetsideImage) {
          if (!downloaded) {
            return true;
          }
          if (onlySigns && (((StreetsideImage) img).getDetections().isEmpty() || !checkSigns((StreetsideImage) img))) {
            return true;
          }
          UserProfile userProfile = ((StreetsideImage) img).getUser();
          if (!"".equals(user.getText()) && (userProfile == null || !user.getText().equals(userProfile.getUsername()))) {
            return true;
          }
        }
        return false;
      };

    if (StreetsideLayer.hasInstance()) {
      StreetsideLayer.getInstance().getData().getImages().parallelStream().forEach(img -> img.setVisible(!shouldHide.test(img)));
    }

    StreetsideLayer.invalidateInstance();
  }

  private boolean checkValidTime(StreetsideAbstractImage img) {
    Long currentTime = currentTime();
    for (int i = 0; i < 3; i++) {
      if (TIME_LIST[i].equals(time.getSelectedItem()) &&
        //img.getCapturedAt() < currentTime - spinnerModel.getNumber().doubleValue() * TIME_FACTOR[i]) {
    	img.getCd() < currentTime - spinnerModel.getNumber().doubleValue() * TIME_FACTOR[i]) {
    	return true;
      }
    }
    return false;
  }

  /**
   * Checks if the image fulfills the sign conditions.
   *
   * @param img The {@link StreetsideAbstractImage} object that is going to be
   * checked.
   *
   * @return {@code true} if it fulfills the conditions; {@code false}
   * otherwise.
   */
  private static boolean checkSigns(StreetsideImage img) {
    for (int i = 0; i < StreetsideFilterChooseSigns.SIGN_TAGS.length; i++) {
      if (checkSign(img, StreetsideFilterChooseSigns.getInstance().signCheckboxes[i], StreetsideFilterChooseSigns.SIGN_TAGS[i]))
        return true;
    }
    return false;
  }

  private static boolean checkSign(StreetsideImage img, JCheckBox signCheckBox, String signTag) {
    boolean contains = false;
    for (ImageDetection detection : img.getDetections()) {
      if (Pattern.compile(signTag).matcher(detection.getValue()).find()) {
        contains = true;
      }
    }
    return contains == signCheckBox.isSelected() && contains;
  }

  private static long currentTime() {
    Calendar cal = Calendar.getInstance();
    return cal.getTimeInMillis();
  }

  /**
   * Destroys the unique instance of the class.
   */
  public static synchronized void destroyInstance() {
    instance = null;
  }

  private class DownloadCheckBoxAction extends AbstractAction {

    private static final long serialVersionUID = 4672634002899519496L;

    DownloadCheckBoxAction() {
      putValue(NAME, tr("Downloaded images"));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      onlySigns.setEnabled(downloaded.isSelected());
    }
  }

  private static class UpdateAction extends AbstractAction {

    private static final long serialVersionUID = -7417238601979689863L;

    UpdateAction() {
      putValue(NAME, tr("Update"));
      new ImageProvider("dialogs", "refresh").getResource().attachImageIcon(this, true);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      StreetsideFilterDialog.getInstance().refresh();
    }
  }

  private static class ResetAction extends AbstractAction {
    private static final long serialVersionUID = 1178261778165525040L;

    ResetAction() {
      putValue(NAME, tr("Reset"));
      new ImageProvider("preferences", "reset").getResource().attachImageIcon(this, true);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      StreetsideFilterDialog.getInstance().reset();
    }
  }

  private class OnlySignsAction extends AbstractAction {

    private static final long serialVersionUID = -2937440338019185723L;

    OnlySignsAction() {
      putValue(NAME, tr("Only images with signs"));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      signChooser.setEnabled(onlySigns.isSelected());
    }
  }

  /**
   * Opens a new window where you can specifically filter signs.
   *
   * @author nokutu
   */
  private static class SignChooserAction extends AbstractAction {

    private static final long serialVersionUID = 8706299665735930148L;

    SignChooserAction() {
      putValue(NAME, tr("Choose signs"));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final JOptionPane pane = new JOptionPane(
        StreetsideFilterChooseSigns.getInstance(),
        JOptionPane.PLAIN_MESSAGE,
        JOptionPane.OK_CANCEL_OPTION
      );
      JDialog dlg = pane.createDialog(Main.parent, tr("Choose signs"));
      dlg.setVisible(true);
      if ((int) pane.getValue() == JOptionPane.OK_OPTION)
        StreetsideFilterDialog.getInstance().refresh();
      dlg.dispose();
    }
  }
}
