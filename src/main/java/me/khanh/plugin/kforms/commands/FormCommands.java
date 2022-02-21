package me.khanh.plugin.kforms.commands;


import me.khanh.plugin.kforms.form.Form;
import me.khanh.plugin.kforms.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FormCommands extends Command implements Utils {
    public FormCommands(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());

                Form.openForm(Objects.requireNonNull(Form.getFormFromCommands(commandLabel.split(":", 2)[1])).getName(), floodgatePlayer);
            }
        } else {
            print(color("&eWARN: Only player using this command."));
        }
        return false;
    }
}
