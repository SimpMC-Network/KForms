package me.khanh.plugin.kforms.config;

import me.khanh.plugin.kforms.KForms;
import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FormConfig implements Utils {
    public final KForms plugin = KForms.getInstance();
    private FileConfiguration config;
    String name = null;
    String file_name = null;

    FormConfig(String name, String file_name){
        this.name = name;
        this.file_name = file_name;
        load();
    }

    public void load() {
        File formsDirectory = new File(plugin.getDataFolder() + File.separator + "forms");
        try {
            if (formsDirectory.mkdirs()){
                print("Folder forms directory did not exist.");
                print("Creating...");
            }
        } catch (SecurityException e){
            severe("Something went wrong while creating forms folder");
        }
        File file = new File(plugin.getDataFolder() + File.separator + "forms" +
                File.separator + file_name);
        if (!file.exists()){
            try {
                print(file_name + " did not exist." );
                print("Creating...");
                file.createNewFile();
                InputStream example = plugin.getResource("example_forms.yml");
                assert example != null;
                Files.copy(example, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (SecurityException | IOException e){
                severe("Something went wrong while creating " + file_name + ".yml file.");
            }
        }
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig(){
        return config;
    }
}
