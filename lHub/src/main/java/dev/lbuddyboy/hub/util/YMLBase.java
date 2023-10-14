package dev.lbuddyboy.hub.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

@Getter
public class YMLBase {

    @Setter private File file;
    private final String configName;
    protected YamlConfiguration config;

    public YMLBase(File folder, String configName) {
        this.configName = configName;
        file = new File(folder, configName + ".yml");
        init();
    }

    public YMLBase(JavaPlugin plugin, String configName) {
        this(plugin.getDataFolder(), configName);
    }

    public void init() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void loadDefaults() {
        try {
            InputStream is = getClass().getResourceAsStream("/" + configName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(readFile(is));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String content = "";
        String line;

        while ((line = reader.readLine()) != null) {
            content += line + "\n";
        }

        reader.close();
        return content.trim();
    }


    public void save() {
        try {
            if (!file.exists())
                file.createNewFile();

            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public YamlConfiguration getConfiguration() {
        return config;
    }

    public void reloadConfig() {
        init();
    }

}