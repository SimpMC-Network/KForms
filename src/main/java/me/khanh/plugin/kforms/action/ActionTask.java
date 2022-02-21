package me.khanh.plugin.kforms.action;

import me.clip.placeholderapi.PlaceholderAPI;
import me.khanh.plugin.kforms.KForms;
import me.khanh.plugin.kforms.form.Form;
import me.khanh.plugin.kforms.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public class ActionTask implements Utils, Runnable {

    private final String name;
    private final ActionType actionType;
    private final String exec;

    public ActionTask(String player_name, ActionType actionType, String exec){
        this.name = player_name;
        this.actionType = actionType;
        this.exec = exec;
    }

    public void run() {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null){
            return;
        }
        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        String executable = PlaceholderAPI.setPlaceholders((OfflinePlayer)player, exec);
        switch (actionType){
            case PLAYER:
                player.chat("/" + executable);
                break;
            case PLACEHOLDER:
                PlaceholderAPI.setPlaceholders((OfflinePlayer) player, executable);
                break;
            case CHAT:
                player.chat(executable);
            case CONSOLE:
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executable);
                break;
            case MESSAGE:
                player.sendMessage(color(executable));
                break;
            case BROADCAST:
                Bukkit.broadcastMessage(color(executable));
                break;
            case PLAYER_COMMAND_EVENT:
                Bukkit.getPluginManager().callEvent(new PlayerCommandPreprocessEvent(player, "/" + executable));
                break;
            case OPEN_FORM:
                Form f = Form.getForm(executable);
                if (f == null){
                    severe("ERROR: Couldn't find form with name: " + executable);
                    break;
                }
                Form.openForm(executable, floodgatePlayer);
                break;
            case CONNECT:
                KForms.getInstance().connect(player, executable);
                break;
            case REFRESH:
                Form f1 = Form.getForm(executable);
                if (f1 == null){
                    break;
                }
            case BROADCAST_SOUND:
            case BROADCAST_WORLD_SOUND:
            case PLAY_SOUND:
                Sound sound = null;
                float volume= 1.0f;
                float pitch = 1.0f;
                if (executable.contains(" ")) {
                    final String[] parts = executable.split(" ");
                    try {
                        sound = Sound.valueOf(parts[0].toUpperCase());
                    }
                    catch (Exception exc3) {
                        return;
                    }
                    if (parts.length >= 2) {
                        if (parts.length == 3) {
                            try {
                                pitch = Float.parseFloat(parts[2]);
                            }
                            catch (NumberFormatException ex) {
                                ex.printStackTrace();
                            }
                        }
                        try {
                            volume = Float.parseFloat(parts[1]);
                        }
                        catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                else {
                    try {
                        sound = Sound.valueOf(executable.toUpperCase());
                    }
                    catch (Exception ex2) {
                        ex2.printStackTrace();
                    }
                }
                if (sound == null) {
                    return;
                }
                if (this.actionType == ActionType.BROADCAST_SOUND) {
                    for (final Player p2 : Bukkit.getOnlinePlayers()) {
                        p2.playSound(p2.getLocation(), sound, volume, pitch);
                    }
                    break;
                }
                if (this.actionType == ActionType.BROADCAST_WORLD_SOUND) {
                    player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
                    break;
                }
                player.playSound(player.getLocation(), sound, volume, pitch);
                break;
        }
    }
}
