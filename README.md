Microsoft Streetside JOSM Plugin
======

The Microsoft JOSM Streetside (https://www.microsoft.com/en-us/maps/streetside) Plugin enables the viewing of Microsoft Streetside 360 degree imagery in the JOSM editor. It is based on the implementation of the Mapillary plugin (https://wiki.openstreetmap.org/wiki/JOSM/Plugins/Mapillary).

Once JOSM is started and the MicrosoftStreetside plugin is selected in the JOSM Preferences, available Streetside imagery can be display within the boundary of the downloaded JOSM area, by selecting Imagery -> Streetside, from the main menu. Once blue bubbles appear on the map signifying Streetside imagery, clicking on a bubble results in the 360 degree imagery being displayed in the 360 degree viewer, which can be undocked and enlarged to allow for better viewing.

## Building from source
Checkout the JOSM source, compile it and checkout the plugin source (the last gradle command is optional, but contains code checking and unit test functionality - requires a Gradle installation):

    svn co http://svn.openstreetmap.org/applications/editors/josm josm
    cd josm/core
    ant
    cd ../plugins
    rm -rf MicrosoftStreetside
    git clone https://github.com/JOSM/MicrosoftStreetside.git MicrosoftStreetside
    cd MicrosoftStreetside
    ant clean
    ant dist
    gradle build
    
Now Restart JOSM and activate the MicrosoftStreeside plugin in your preferences. 
The MicrosoftStreetside menu items will appear in the JOSM main menu after JOSM is
restarted.

Details about plugin development can be found [in the JOSM wiki](https://josm.openstreetmap.de/wiki/DevelopersGuide/DevelopingPlugins).

## License

This plugin is based on the Mapilary and extended to display Streetside imagery.

This software is licensed under [GPL v3](https://www.gnu.org/licenses/gpl-3.0.en.html). 

### Third party resources

The MicrosoftStreetside plugin used JavaFX to implement the Streetside 360 degree viewer [JavaFX](https://en.wikipedia.org/wiki/JavaFX), which, while bundled in the Oracle Java SE 1.8, is not an official part of the 
Java SE 1.8 specification, and may not function properly with alternative JDKs (e.g. OpenJDK is not currently supported). JavaFX is licensed under the same terms as Java SE (http://www.oracle.com/technetwork/java/javase/terms/license/index.html).

The plugin also makes use of the compact Resty libary for communicating with RESTful web services from Java (https://beders.github.io/Resty/Resty/Overview.html). Resty is licensed under the MIT license (https://github.com/go-resty/resty/blob/master/LICENSE).


### Code of Conduct
This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
