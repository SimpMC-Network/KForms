package me.khanh.plugin.kforms.commands;

import me.khanh.plugin.kforms.KForms;
import me.khanh.plugin.kforms.form.Form;
import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;


public class KFormsCommands implements CommandExecutor, Utils {
    private Player player;
    private FloodgatePlayer floodgatePlayer;
    private FloodgateApi floodgateApi = FloodgateApi.getInstance();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args == null){
            sender.sendMessage(color("&b&lKForms version: &f" + KForms.getInstance().getDescription().getVersion()));
            sender.sendMessage(color("&fAuthor: &aKhanhHuynh"));
            return false;
        }
        if (args.length >= 1){
            switch (args[0].toLowerCase()){
                case "open":
                    if (sender.hasPermission("kforms.open")){
                        if (args.length == 1){
                            sender.sendMessage(color("&eSyntax error: /kforms open <menu> [player]"));
                            return false;
                        }
                        if (args.length == 2){
                            Form form = Form.getForm(args[1]);
                            if (form == null){
                                sender.sendMessage(color("&cCouldn't find form with name: " + args[1]));
                                return false;
                            }
                            if (sender instanceof Player){
                                player = (Player) sender;
                                player.chat("/" + args[1]);
                            } else {
                                print(color("&eWARN: Only player using this command."));
                            }
                            return false;
                        }
                        if (args.length == 3){
                            Form form = Form.getForm(args[1]);
                            if (form == null){
                                sender.sendMessage(color("&cCouldn't find form with name: " + args[1]));
                                return false;
                            }
                            player = Bukkit.getPlayerExact(args[2]);
                            if (player == null){
                                sender.sendMessage(color("Couldn't find player with name" + args[2]));
                                return false;
                            }
                            if (!player.isOnline()){
                                sender.sendMessage(color("Player is offline" + args[2]));
                                return false;
                            }
                            player.chat("/" + args[1]);
                            return false;
                        }
                        sender.sendMessage(color("&eSyntax error: /kforms open <menu> [player]"));
                    } else {
                        sender.sendMessage(color("&cBạn không có quyền dùng lệnh này!"));
                    }
                    return false;
                case "list":
                    if (!sender.hasPermission("kforms.list")){
                        sender.sendMessage(color("&cBạn không có quyền dùng lệnh này!"));
                        return false;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Form f: Form.forms){
                        stringBuilder.append(f.getName())
                                .append(", ");
                    }
                    stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                    sender.sendMessage(color("&bForms: &f" + stringBuilder));
                    return false;
                case "reload":
                    if (!sender.hasPermission("kforms.reload")){
                        sender.sendMessage(color("&cBạn không có quyền dùng lệnh này!"));
                        return false;
                    }
                    sender.sendMessage(color("&aReloading config!"));
                    KForms.getInstance().reload();
                    return false;
                case "help":
                    if (!sender.hasPermission("kforms.help")){
                        sender.sendMessage(color("&cBạn không có quyền dùng lệnh này!"));
                        return false;
                    }
                    sender.sendMessage(color("&f/kforms: Displays some info about the plugin."));
                    sender.sendMessage(color("&f/kforms open <menu> [player]: Opens the specified menu."));
                    sender.sendMessage(color("&f/kforms list: Lists loaded menus."));
                    sender.sendMessage(color("&f/kforms reload: Reload a menu."));

            }
        }
        return false;
    }
}
