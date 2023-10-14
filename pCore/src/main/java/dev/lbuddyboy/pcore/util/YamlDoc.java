package dev.lbuddyboy.pcore.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class YamlDoc {

    @Getter private boolean reloaded = false;

    private final File file;
    private YamlConfiguration config;
    private final String configName;

    public YamlDoc(File folder, String configName) {
        this.configName = configName;
        file = new File(folder, configName);
        init();
        Bukkit.getConsoleSender().sendMessage(CC.translate("&fSuccessfully created the &6" + configName + " &ffile."));
    }

    public void init() {
        if (!file.exists()) {
            try {
                file.createNewFile();
                loadDefaults();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            e.printStackTrace();
        }
    }


    public YamlConfiguration gc() {
        return config;
    }

    public void reloadConfig() {
        init();
        reloaded = true;
    }

}
