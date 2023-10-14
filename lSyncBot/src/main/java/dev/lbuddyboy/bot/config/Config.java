package dev.lbuddyboy.bot.config;

import dev.lbuddyboy.bot.Bot;
import lombok.Getter;
import lombok.Setter;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.*;

/**
 * Represents a Configuration file to retrieve data.
 */

@Getter
public class Config extends YamlConfiguration {

    private final String fileName;
    private final Bot plugin;
    private final File folder;
    @Setter private File file;

    /**
     * Creates a new config with a given name.
     *
     * @param plugin   the plugin to create for
     * @param fileName the name of the file
     */
    public Config(Bot plugin, String fileName) {
        this(plugin, fileName, ".yml", plugin.getDataFolder());
    }

    /**
     * Creates a new config with a given name and file extension.
     *
     * @param plugin        the plugin to create for
     * @param fileName      the name of the file
     * @param folder the parent folder of the file
     */
    public Config(Bot plugin, String fileName, File folder) {
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
    public Config(Bot plugin, String fileName, String fileExtension, File folder) {
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
                    if (this.getClass().getClassLoader().getResourceAsStream(fileName) != null) {
                        plugin.saveResource(fileName, false);
                    }
                } else {
                    if (this.getClass().getClassLoader().getResource(folder.getName() + "/" + fileName) != null) {
                        plugin.saveResource(folder.getName() + File.separator + fileName, false);
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
