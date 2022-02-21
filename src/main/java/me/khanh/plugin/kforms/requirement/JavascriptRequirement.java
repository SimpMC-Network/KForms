package me.khanh.plugin.kforms.requirement;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import me.khanh.plugin.kforms.KForms;
import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavascriptRequirement extends Requirement implements Utils {
    private final ScriptEngineFactory factory = new NashornScriptEngineFactory();
    private final ServicesManager manager = Bukkit.getServer().getServicesManager();
    private static ScriptEngineManager engine;
    private final String expression;

    public JavascriptRequirement(String expression) {
        this.expression = expression;
        if (engine == null) {
            if (this.manager.isProvidedFor(ScriptEngineManager.class)) {
                RegisteredServiceProvider provider = this.manager.getRegistration(ScriptEngineManager.class);
                engine = (ScriptEngineManager)provider.getProvider();
            } else {
                engine = new ScriptEngineManager();
                this.manager.register(ScriptEngineManager.class, engine, KForms.getInstance(), ServicePriority.Highest);
            }
            engine.registerEngineName("JavaScript", this.factory);
            engine.put("BukkitServer", Bukkit.getServer());
        }
    }

    @Override
    public boolean evaluate(Player player) {
        String exp = message(this.expression, player);
        try {
            engine.put("BukkitPlayer", player);
            Object result = engine.getEngineByName("JavaScript").eval(exp);
            if (!(result instanceof Boolean)) {
                this.getInstance().getLogger().severe("Requirement javascript <" + this.expression + "> is invalid and does not return a boolean!");
                return false;
            }
            return (Boolean)result;
        }
        catch (NullPointerException | ScriptException ex) {
            this.getInstance().getLogger().severe("Error in requirement javascript syntax - " + this.expression);
            ex.printStackTrace();
            return false;
        }
    }
}
