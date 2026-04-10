package me.mp1282.visualtest.ui;

/**
 * Plain data object serialised to/from the application settings file.
 * Add new fields here as more persistent preferences are introduced.
 */
public class AppSettings {
    public String  lastProjectDirectory = null;
    public boolean explainMode          = false;
    public String  zoomLevel            = "DEFAULT";
}
