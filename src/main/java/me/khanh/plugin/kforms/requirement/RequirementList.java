package me.khanh.plugin.kforms.requirement;

import me.khanh.plugin.kforms.form.Form;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;

public class RequirementList {
    private List<Requirement> requirements;
    private List<String> denyHandler;
    private int minimumRequirements;
    private boolean stopAtSuccess;

    public RequirementList(List<Requirement> requirements) {
        this.setRequirements(requirements);
    }

    public boolean evaluate(Player player) {
        int successful = 0;
        for (Requirement r : this.getRequirements()) {
            if (r.evaluate(player)) {
                ++successful;
                if (r.getSuccessHandler() != null) {
                    Form.runCommands(FloodgateApi.getInstance().getPlayer(player.getUniqueId()), r.getSuccessHandler());
                }
                if (!this.stopAtSuccess || successful < this.minimumRequirements) continue;
                break;
            }
            if (r.getDenyHandler() != null) {
                Form.runCommands(FloodgateApi.getInstance().getPlayer(player.getUniqueId()), r.getDenyHandler());
            }
            if (r.isOptional()) continue;
            return false;
        }
        return successful >= this.minimumRequirements;
    }

    public List<Requirement> getRequirements() {
        return this.requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public List<String> getDenyHandler() {
        return this.denyHandler;
    }

    public void setDenyHandler(List<String> denyHandler) {
        this.denyHandler = denyHandler;
    }

    public int getMinimumRequirements() {
        return this.minimumRequirements;
    }

    public void setMinimumRequirements(int minimumRequirements) {
        this.minimumRequirements = minimumRequirements;
    }

    public boolean stopAtSuccess() {
        return this.stopAtSuccess;
    }

    public void setStopAtSuccess(boolean stop) {
        this.stopAtSuccess = stop;
    }
}
