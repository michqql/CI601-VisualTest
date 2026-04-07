package me.mp1282.visualtest.ui;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Loads and saves application-level settings (last project directory, UI preferences)
 * to a JSON file in the user's home directory. Settings are loaded eagerly on first
 * access and saved whenever a value changes.
 */
public class AppSettingsService {

    private static final File SETTINGS_FILE = new File(
            System.getProperty("user.home"), ".visualtest/settings.json");
    private static final AppSettingsService INSTANCE = new AppSettingsService();

    private final Gson gson = new Gson();
    private AppSettings settings;

    private AppSettingsService() {
        settings = load();
    }

    public static AppSettingsService getInstance() {
        return INSTANCE;
    }

    public AppSettings getSettings() {
        return settings;
    }

    /** Returns the last saved project directory, or {@code null} if none or it no longer exists. */
    public File getLastProjectDirectory() {
        if (settings.lastProjectDirectory == null) return null;
        File dir = new File(settings.lastProjectDirectory);
        return dir.isDirectory() ? dir : null;
    }

    /** Updates the last project directory and immediately persists the settings. */
    public void setLastProjectDirectory(File dir) {
        settings.lastProjectDirectory = (dir != null) ? dir.getAbsolutePath() : null;
        save();
    }

    /** Persists the current settings to disk. */
    public void save() {
        try {
            SETTINGS_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(SETTINGS_FILE)) {
                gson.toJson(settings, writer);
            }
        } catch (Exception e) {
            System.err.println("AppSettingsService: failed to save settings — " + e.getMessage());
        }
    }

    private AppSettings load() {
        if (!SETTINGS_FILE.exists()) return new AppSettings();
        try (FileReader reader = new FileReader(SETTINGS_FILE)) {
            AppSettings loaded = gson.fromJson(reader, AppSettings.class);
            return (loaded != null) ? loaded : new AppSettings();
        } catch (Exception e) {
            System.err.println("AppSettingsService: failed to load settings — " + e.getMessage());
            return new AppSettings();
        }
    }
}
