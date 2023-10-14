package dev.lbuddyboy.crates.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Represents a Configuration file to retrieve data.
 */

@Getter
public class Config extends YamlConfiguration {

    private final String fileName;
    private final JavaPlugin plugin;
    private final File folder;
    @Setter private File file;

    /**
     * Creates a new config with a given name.
     *
     * @param plugin   the plugin to create for
     * @param fileName the name of the file
     */
    public Config(JavaPlugin plugin, String fileName) {
        this(plugin, fileName, ".yml", plugin.getDataFolder());
    }

    /**
     * Creates a new config with a given name and file extension.
     *
     * @param plugin        the plugin to create for
     * @param fileName      the name of the file
     * @param folder the parent folder of the file
     */
    public Config(JavaPlugin plugin, String fileName, File folder) {
        this(plugin, fileName, ".yml", folder);
    }

    /**
     * Creates a new config with a given name and file extension.
     *
     * @param plugin        the plugin to create for
     * @param fileName      the name of the file
     * @param fileExtension the extension of the file
     * @param folder the parent folder of the file
     */
    public Config(JavaPlugin plugin, String fileName, String fileExtension, File folder) {
        this.plugin = plugin;
        this.folder = folder;
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.createFile();
    }

    /**
     * Creates configuration file with the fileName in the plugin folder.
     */
    private void createFile() {
        File folder = this.folder;
        try {
            File file = new File(folder, fileName);
            if (!file.exists()) {
                if (folder == plugin.getDataFolder()) {
                    if (plugin.getResource(fileName) != null) {
                        plugin.saveResource(fileName, true);
                    }
                } else {
                    if (plugin.getResource(folder.getName() + "/" + fileName) != null) {
                        plugin.saveResource(folder.getName() + "/" + fileName, true);
                    }
                }
            }
            if (!file.exists()) {
                save(file);
            }
            load(file);
            save(file);
            this.file = file;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Saves the configuration file to the plugin data.
     */
    public void save() {
        File folder = this.folder;
        try {
            save(this.file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
