package me.khanh.plugin.kforms.requirement;

import me.khanh.plugin.kforms.KForms;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Requirement {
    private List<String> successHandler;
    private List<String> denyHandler;
    private boolean optional;

    public Requirement() {
        this.setOptional(false);
    }

    public abstract boolean evaluate(Player player);

    public KForms getInstance() {
        return KForms.getInstance();
    }

    public boolean hasDenyHandler() {
        return this.denyHandler != null;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public List<String> getDenyHandler() {
        return denyHandler;
    }

    public void setDenyHandler(List<String> denyHandler) {
        this.denyHandler = denyHandler;
    }

    public List<String> getSuccessHandler() {
        return successHandler;
    }

    public void setSuccessHandler(List<String> successHandler) {
        this.successHandler = successHandler;
    }
}
