package me.khanh.plugin.kforms.config;

import me.khanh.plugin.kforms.KForms;
import me.khanh.plugin.kforms.form.Form;
import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainConfig implements Utils {
    private final KForms plugin = KForms.getInstance();
    public static final Map<String ,FileConfiguration> formsConfig = new HashMap<>();

    private File file;
    private FileConfiguration config;

    public void createConfig(){
        file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()){
            file.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
        }
        parseYaml();
        loadFormsConfig();
        Form.load();
    }

    private void loadFormsConfig(){
        ConfigurationSection section = config.getConfigurationSection("forms");
        if (section == null || section.getKeys(false).isEmpty()){
            print("Couldn't find any forms in config file");
        } else {
            formsConfig.clear();
            for (String s : section.getKeys(false)){
                if (section.get(s + ".file") == null){
                    config.set("forms" + s + ".file", s + ".yml");
                    save();
                }
                formsConfig.put(s, (new FormConfig(s, section.getString(s + ".file"))).getConfig());
            }
        }
    }

    public FileConfiguration getConfig(){
        return config;
    }

    private void parseYaml(){
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        parseYaml();
    }

}
