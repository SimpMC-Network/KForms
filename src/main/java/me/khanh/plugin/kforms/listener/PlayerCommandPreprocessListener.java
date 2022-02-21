package me.khanh.plugin.kforms.listener;

import me.khanh.plugin.kforms.form.Form;
import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.geysermc.floodgate.api.FloodgateApi;

public class PlayerCommandPreprocessListener implements Listener {

    private final FloodgateApi floodgateApi = FloodgateApi.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent e){
        String cmd = e.getMessage().substring(1);
        Form form = Form.getFormFromCommands(cmd.toLowerCase());
        if (form == null){
            return;
        }
        if (floodgateApi.isFloodgatePlayer(e.getPlayer().getUniqueId())){
            if (form.isRegistered()){
                e.setMessage("/kforms:" + cmd);
                return;
            }
            Form.openForm(form.getName(), floodgateApi.getPlayer(e.getPlayer().getUniqueId()));
            e.setCancelled(true);
        }
    }
}
