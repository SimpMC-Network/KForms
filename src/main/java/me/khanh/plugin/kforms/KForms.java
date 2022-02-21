package me.khanh.plugin.kforms;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.khanh.plugin.kforms.commands.KFormsCommands;
import me.khanh.plugin.kforms.config.MainConfig;
import me.khanh.plugin.kforms.form.Form;
import me.khanh.plugin.kforms.listener.PlayerCommandPreprocessListener;
import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


public final class KForms extends JavaPlugin implements Utils {
    private static KForms instance;
    private MainConfig mainConfig;
    private FileConfiguration config;

    public static KForms getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        printWatermark();
        if (!this.hookPlaceholderAPI()) {
            this.getLogger().severe("Could not hook into PlaceholderAPI!");
            this.getLogger().severe("DeluxeMenus will now disable!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        print("Successfully hooked into PlaceholderAPI!");
        loadConfig();
        Objects.requireNonNull(getCommand("kforms")).setExecutor(new KFormsCommands());
        registerEvents();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public void reload(){
        for (Form form: Form.forms){
            form.removeCommand();
        }
        Form.forms.clear();
        MainConfig.formsConfig.clear();
        loadConfig();
        registerEvents();
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getScheduler().cancelTasks(this);
        for (Form form: Form.forms){
            form.removeCommand();
        }
        Form.forms.clear();
        MainConfig.formsConfig.clear();
        instance = null;
    }

    private final String watermark = "\n\n" +
            "&b██   ██ ███████  ██████  ██████  ███    ███ ███████ \n" +
            "&b██  ██  ██      ██    ██ ██   ██ ████  ████ ██       Name: KForms\n" +
            "&b█████   █████   ██    ██ ██████  ██ ████ ██ ███████  Author: KhanhHuynh\n" +
            "&b██  ██  ██      ██    ██ ██   ██ ██  ██  ██      ██  Version: " + getDescription().getVersion() + "\n" +
            "&b██   ██ ██       ██████  ██   ██ ██      ██ ███████ \n" +
            "                                                    \n";

    private void printWatermark() {
        print(watermark);
    }

    private boolean hookPlaceholderAPI() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    private void registerEvents(){
        getServer().getPluginManager().registerEvents(new PlayerCommandPreprocessListener(), this);
    }

    public void loadConfig(){
        mainConfig = new MainConfig();
        mainConfig.createConfig();
        config = mainConfig.getConfig();
    }


    @SuppressWarnings("all")
    public void connect(Player p, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        }
        catch (Exception e) {
            this.getLogger().warning("There was a problem attempting to send " + p.getName() + " to server " + server + "!");
        }
        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
}
