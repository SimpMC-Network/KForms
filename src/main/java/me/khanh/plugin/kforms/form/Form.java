package me.khanh.plugin.kforms.form;

import me.khanh.plugin.kforms.KForms;
import me.khanh.plugin.kforms.action.ActionTask;
import me.khanh.plugin.kforms.action.ActionType;
import me.khanh.plugin.kforms.commands.FormCommands;
import me.khanh.plugin.kforms.config.MainConfig;
import me.khanh.plugin.kforms.requirement.*;
import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.ModalForm;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Form implements Utils{
    private static final KForms plugin = KForms.getInstance();
    public static final Set<Form> forms = new HashSet<>();
    private final String name;
    private String title = null;
    private FormType formType = null;
    private List<String> commands = new ArrayList<>();
    private final List<FormContent> contents = new ArrayList<>();
    private final FileConfiguration configuration;
    private boolean canParse = true;
    private RequirementList requirementList;
    private final List<Requirement> requirements = new ArrayList<>();
    private static CommandMap commandMap = null;
    private Command mainCommand;
    private boolean register_command;


    Form(String name, FileConfiguration c){
        this.name = name;
        this.configuration = c;
    }
    public static void load(){
        for (String s : Objects.requireNonNull(plugin.getConfig().getConfigurationSection("forms")).getKeys(false)) {
            FileConfiguration fileConfiguration =  MainConfig.formsConfig.get(s);
            Form form = (new Form(s, fileConfiguration)).parseForm();
            if (form.isCanParse()){
                forms.add(form);
                Utils.write("successfully loaded " + form.getName());
            }
        }
    }

    public Form parseForm(){
        initRequirements();
        if (!configuration.getStringList("open_commands").isEmpty()){
            commands = configuration.getStringList("open_commands").stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
        } else {
            if (configuration.getString("open_commands") != null){
                commands = Collections.singletonList(Objects.requireNonNull(configuration.getString("open_commands")).toLowerCase());
            }
        }
        if (configuration.get("register_command") == null){
            configuration.set("register_command", false);
        }
        register_command = configuration.getBoolean("register_command");
        if (register_command){
            addCommand();
        }
        for (String s: commands){
          for (Form f: Form.forms){
              for (String s1: f.getCommands()){
                  if (s.equalsIgnoreCase(s1)){
                      print("ERROR: Couldn't load " + name + ". There is a form using this command: " + s);
                      setCanParse(false);
                      return this;
                  }
              }
          }
        }
        if (configuration.get("title") == null){
            print("An error occurred during loading " + getName()  +
                    ". Please set title for form" + configuration.getName());
            setCanParse(false);
            return this;
        } else {
            title = configuration.getString("title");
        }
        if (configuration.get("type") == null){
            print("An error occurred during loading " + getName()  +
                    ". Please set type for form" + configuration.getName());
            setCanParse(false);
            return this;
        } else {
            if (!isVCT(Objects.requireNonNull(configuration.getString("type")).toUpperCase())){
                print("An error occurred during loading " + getName()  +
                        ". Please incorrect type for form" + configuration.getName());
                setCanParse(false);
                return this;
            } else {
                formType = FormType.valueOf(configuration.getString("type"));
            }
        }
        if (configuration.get("contents") == null){
            print("An error occurred during loading " + getName()  +
                    ". Please set form content for form" + configuration.getName());
            setCanParse(false);
            return this;
        } else {
            if (Objects.requireNonNull(configuration.getConfigurationSection("contents")).getKeys(false).isEmpty()){
                print("An error occurred during loading " + getName()  +
                        ". Please form content is empty for form" + configuration.getName());
                setCanParse(false);
                return this;
            } else {
                for (String s : Objects.requireNonNull(configuration.getConfigurationSection("contents")).getKeys(false)){
                    FormContent content = (new FormContent(this, Objects.requireNonNull(configuration.getConfigurationSection("contents." + s)))).parseFormContent();
                    if (content.isCanParse()){
                        contents.add(content);
                    }
                }

            }
        }
        return this;
    }

    public FormType getFormType() {
        return formType;
    }

    public String getName() {
        return name;
    }

    public boolean isCanParse() {
        return canParse;
    }

    public void setCanParse(boolean canParse) {
        this.canParse = canParse;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCommands(){
        return commands;
    }

    public boolean isRegistered(){
        return register_command;
    }

    enum FormType {
        MODAL_FORM,
        SIMPLE_FORM,
//        CUSTOM_FORM
    }

    public List<FormContent> getContents(){
        return contents;
    }

    public RequirementList getRequirementList(){
        return requirementList;
    }

    public boolean isVCT(String formType){
        try {
            FormType.valueOf(formType);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    public  ModalForm.Builder modal_build(FloodgatePlayer player){
        contents.clear();
        for (String s : Objects.requireNonNull(configuration.getConfigurationSection("contents")).getKeys(false)){
            FormContent content = (new FormContent(this, Objects.requireNonNull(configuration.getConfigurationSection("contents." + s)))).reParseFormContent();
            if (content.isCanParse()){
                contents.add(content);
            }
        }
        ModalForm.Builder builder = ModalForm.builder();
        builder.title(Utils.placeholder(getTitle(), Bukkit.getPlayerExact(player.getCorrectUsername())));
        for (FormContent content: getContents()){
            if (content.getContentType().equals(FormContent.ContentType.BUTTON1)){
                builder.button1(Utils.placeholder(content.getButton1(), Bukkit.getPlayerExact(player.getCorrectUsername())));
            }
            if (content.getContentType().equals(FormContent.ContentType.BUTTON2)){
                builder.button1(Utils.placeholder(content.getButton1(), Bukkit.getPlayerExact(player.getCorrectUsername())));
            }
            if (content.getContentType().equals(FormContent.ContentType.CONTENT)){
                StringBuilder content_string = new StringBuilder();
                for (String s:content.getContent()){
                    content_string.append(s).append("\n");
                }
                builder.content(Utils.placeholder(content_string.substring(0, content_string.length() - 2), Bukkit.getPlayerExact(player.getCorrectUsername())));
            }
        }
        builder.responseHandler(((modalForm, s) -> {
            ModalFormResponse response = modalForm.parseResponse(s);
            if (!response.isCorrect()){
                return;
            }
            FormContent content = getFormContent(getName(), response.getClickedButtonId());
            assert content != null;
            if (content.getRequirementList() == null){
                return;
            }
            if (!content.getRequirementList().evaluate(Bukkit.getPlayerExact(player.getCorrectUsername()))){
                if (content.getRequirementList().getDenyHandler() != null){
                    runCommands(player, content.getRequirementList().getDenyHandler());
                    return;
                }
            }
            List<String> commands = getCommands(getName(), response.getClickedButtonId());
            runCommands(player, commands);
        }));
        return builder;
    }

    public SimpleForm.Builder simple_build(FloodgatePlayer player){
        contents.clear();
        for (String s : Objects.requireNonNull(configuration.getConfigurationSection("contents")).getKeys(false)){
            FormContent content = (new FormContent(this, Objects.requireNonNull(configuration.getConfigurationSection("contents." + s)))).reParseFormContent();
            if (content.isCanParse()){
                contents.add(content);
            }
        }
        SimpleForm.Builder build = SimpleForm.builder();
        build.title(Utils.placeholder(getTitle(), Bukkit.getPlayerExact(player.getCorrectUsername())));
        for (FormContent content : getContents()){
            if (content.getContentType().equals(FormContent.ContentType.CONTENT)){
                StringBuilder content_string = new StringBuilder();
                for (String s:content.getContent()){
                    content_string.append(s).append("\n");
                }
                build.content(Utils.placeholder(content_string.substring(0, content_string.length() - 2), Bukkit.getPlayerExact(player.getCorrectUsername())));
            }
            if (content.getContentType().equals(FormContent.ContentType.BUTTON)){
                if (content.getUrl() != null){
                    build.button(Utils.placeholder(content.getButton(), Bukkit.getPlayer(player.getJavaUniqueId())),
                            FormImage.Type.URL, content.getUrl());
                } else {
                    if (content.getPath() != null){
                        build.button(Utils.placeholder(content.getButton(), Bukkit.getPlayer(player.getJavaUniqueId())),
                                FormImage.Type.PATH, content.getPath());
                    } else {
                        build.button(Utils.placeholder(content.getButton(), Bukkit.getPlayer(player.getJavaUniqueId())));
                    }
                }
            }
        }

        build.responseHandler((simpleForm, s) -> {
            SimpleFormResponse response = simpleForm.parseResponse(s);
            if (!response.isCorrect()){
                return;
            }
            FormContent content = getFormContent(getName(), response.getClickedButtonId());
            assert content != null;
            if (content.getRequirementList() != null){
                if (!content.getRequirementList().evaluate(Bukkit.getPlayerExact(player.getCorrectUsername()))){
                    if (content.getRequirementList().getDenyHandler() != null){
                        runCommands(player, content.getRequirementList().getDenyHandler());
                    }
                    return;
                }
            }
            List<String> commands = getCommands(getName(), response.getClickedButtonId());
            runCommands(player, commands);
        });
        return build;
    }

    public static void runCommands(FloodgatePlayer player, List<String> commands){
        for (String str: commands){
            ActionType actionType = ActionType.getActionType(str);
            if (actionType == null){
                Utils.write("Cannot run command: " + str);
                continue;
            }
            ActionTask task = new ActionTask(player.getCorrectUsername(), actionType, ActionType.getExec(str));
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static Form getForm(String name){
        for (Form form : forms){
            if (form.getName().equals(name)){
                return form;
            }
        }
        return null;
    }

    public static Form getFormFromCommands(String command){
        for (Form form : forms){
            if (form.getCommands().contains(command)){
                return form;
            }
        }
        return null;
    }

    public static void openForm(String form, FloodgatePlayer player){
        Form f = getForm(form);
        if (f == null){
            Utils.write("Couldn't found form with name" + form);
            return;
        }
        Objects.requireNonNull(Bukkit.getPlayerExact(player.getCorrectUsername())).closeInventory();
        switch (f.getFormType()){
            case MODAL_FORM:
                if (f.getRequirementList() != null){
                    if (!f.getRequirementList().evaluate(Bukkit.getPlayerExact(player.getCorrectUsername()))){
                        if (f.getRequirementList().getDenyHandler() != null){
                            runCommands(player, f.getRequirementList().getDenyHandler());
                        }
                        return;
                    }
                }
                player.sendForm(f.modal_build(player));
                return;
            case SIMPLE_FORM:
                if (f.getRequirementList() != null){
                    if (!f.getRequirementList().evaluate(Bukkit.getPlayerExact(player.getCorrectUsername()))){
                        if (f.getRequirementList().getDenyHandler() != null){
                            runCommands(player, f.getRequirementList().getDenyHandler());
                        }
                        return;
                    }
                }
                player.sendForm(f.simple_build(player));
        }
    }

    public static List<String> getCommands(String form, int id){
        Form f = getForm(form);
        if (f == null){
            Utils.write("Couldn't found form with name" + form);
            return new ArrayList<>();
        }
        List<FormContent> contents = f.getContents();
        contents.removeIf(formContent -> !formContent.getContentType().name().toLowerCase().contains("button"));
        FormContent content = contents.get(id);
        return content.getClickCommands();
    }

    public static FormContent getFormContent(String form, int id){
        Form f = getForm(form);
        if (f == null){
            Utils.write("Couldn't found form with name" + form);
            return null;
        }
        List<FormContent> contents = f.getContents();
        contents.removeIf(formContent -> !formContent.getContentType().name().toLowerCase().contains("button"));
        return contents.get(id);
    }

    public void initRequirements(){
        if (configuration.get("requirements") != null){
            for (String s: Objects.requireNonNull(configuration.getConfigurationSection("requirements")).getKeys(false)){
                if (s.equalsIgnoreCase("minimum_requirements") || s.equalsIgnoreCase("stop_at_success") || s.equalsIgnoreCase("deny_commands")){
                    continue;
                }
                ConfigurationSection reqSection = configuration.getConfigurationSection("requirements." + s);
                if (Objects.requireNonNull(reqSection).get("type") == null){
                    severe("ERROR: Can't find TYPE requirements of " + s + " " + getName());
                } else {
                    RequirementType requirementType = RequirementType.getType(reqSection.getString("type"));
                    if (requirementType == null){
                        severe("ERROR: Can't find TYPE requirements with name " + reqSection.getString("type") +
                                "in " + getName());
                    } else {
                        switch (requirementType){
                            case HAS_EXP:
                            case DOES_NOT_HAVE_EXP:
                                int amount;
                                if (reqSection.get("amount") == null){
                                    severe("ERROR: Please set amount for" + getName());
                                    continue;
                                }
                                try {
                                    amount = Integer.parseInt(Objects.requireNonNull(reqSection.getString("amount")));
                                } catch (NumberFormatException e){
                                    severe("ERROR: " + reqSection.getCurrentPath() + ".amount is not a number");
                                    continue;
                                }
                                boolean level = Boolean.parseBoolean(reqSection.getString("level"));
                                boolean invert = requirementType == RequirementType.DOES_NOT_HAVE_EXP;
                                HasExpRequirement hasExpRequirement = new HasExpRequirement(amount, invert, level);
                                setSuccessCommands(reqSection, hasExpRequirement);
                                setDenyCommands(reqSection, hasExpRequirement);
                                hasExpRequirement.setOptional(reqSection.getBoolean("optional"));
                                requirements.add(hasExpRequirement);
                                break;
                            case EQUAL_TO:
                            case GREATER_THAN:
                            case GREATER_THAN_EQUAL_TO:
                            case LESS_THAN:
                            case LESS_THAN_EQUAL_TO:
                            case STRING_CONTAINS:
                            case STRING_EQUALS:
                            case STRING_EQUALS_IGNORECASE:
                            case STRING_DOES_NOT_CONTAIN:
                            case STRING_DOES_NOT_EQUAL:
                            case STRING_DOES_NOT_EQUAL_IGNORECASE:
                                if (reqSection.getString("input") == null){
                                    severe("ERROR: Please set INPUT for " + reqSection.getCurrentPath() + ".input");
                                    continue;
                                }
                                if (reqSection.getString("output") == null){
                                    severe("ERROR: Please set OUTPUT for " + reqSection.getCurrentPath() + ".output");
                                    continue;
                                }
                                InputResultRequirement inputResultRequirement = new InputResultRequirement(requirementType, reqSection.getString("input"), reqSection.getString("output"));
                                setSuccessCommands(reqSection, inputResultRequirement);
                                setDenyCommands(reqSection, inputResultRequirement);
                                inputResultRequirement.setOptional(reqSection.getBoolean("optional"));
                                requirements.add(inputResultRequirement);
                                break;
                            case JAVASCRIPT:
                                if (reqSection.getString("expression") == null){
                                    severe("ERROR: Please set expression for " + reqSection.getCurrentPath() + ".expression");
                                    continue;
                                }
                                JavascriptRequirement javascriptRequirement = new JavascriptRequirement(reqSection.getString("expression"));
                                setSuccessCommands(reqSection, javascriptRequirement);
                                setDenyCommands(reqSection, javascriptRequirement);
                                javascriptRequirement.setOptional(reqSection.getBoolean("optional"));
                                requirements.add(javascriptRequirement);
                                break;
                            case REGEX_DOES_NOT_MATCH:
                            case REGEX_MATCHES:
                                if (reqSection.getString("input") == null){
                                    severe("ERROR: Please set INPUT for " + reqSection.getCurrentPath() + ".input");
                                    continue;
                                }
                                if (reqSection.getString("regex") == null){
                                    severe("ERROR: Please set regex for " + reqSection.getCurrentPath() + ".regex");
                                    continue;
                                }
                                Pattern p = Pattern.compile(Objects.requireNonNull(reqSection.getString("regex")));
                                invert = requirementType == RequirementType.REGEX_DOES_NOT_MATCH;
                                RegexMatchesRequirement regexMatchesRequirement = new RegexMatchesRequirement(p, reqSection.getString("input"), invert);
                                setSuccessCommands(reqSection, regexMatchesRequirement);
                                setDenyCommands(reqSection, regexMatchesRequirement);
                                regexMatchesRequirement.setOptional(reqSection.getBoolean("optional"));
                                requirements.add(regexMatchesRequirement);
                                break;
                        }
                    }
                }
            }
            requirementList = new RequirementList(requirements);
            ConfigurationSection reqSection = configuration.getConfigurationSection("requirements");
            if (Objects.requireNonNull(reqSection).get("deny_commands") != null && !reqSection.getStringList("deny_commands").isEmpty()) {
                requirementList.setDenyHandler(reqSection.getStringList("deny_commands"));
            }
            requirementList.setStopAtSuccess(reqSection.getBoolean("stop_at_success"));
            if (reqSection.get("minimum_requirements") != null){
                requirementList.setMinimumRequirements(reqSection.getInt("minimum_requirements"));
            } else {
                int required = 0;
                for (Requirement req : requirementList.getRequirements()){
                    if (!req.isOptional()) {
                        required = required + 1;
                    }
                }
                requirementList.setMinimumRequirements(required);
            }
        }
    }

    public void setSuccessCommands(ConfigurationSection reqSection, Requirement requirement){
        if (reqSection.get("success_commands") != null && !reqSection.getStringList("success_commands").isEmpty()) {
            requirement.setSuccessHandler(reqSection.getStringList("success_commands"));
        }
    }

    public void setDenyCommands(ConfigurationSection reqSection, Requirement requirement){
        if (reqSection.get("deny_commands") != null && !reqSection.getStringList("deny_commands").isEmpty()) {
            requirement.setDenyHandler(reqSection.getStringList("deny_commands"));
        }
    }

    private void addCommand() {
        boolean registered;
        if (commandMap == null) {
            try {
                Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap)f.get(Bukkit.getServer());
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        mainCommand = new FormCommands(commands.get(0));
        mainCommand.setAliases(commands.subList(1, commands.size()));
        if (registered = commandMap.register("kforms", mainCommand)) {
            print("Registered command: " + mainCommand.getName() + " for form: " + getName());
        }
    }

    public void removeCommand() {
        if (commandMap != null && isRegistered()) {
            try {
                Field cMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                cMap.setAccessible(true);
                Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommands.setAccessible(true);
                ((Map)knownCommands.get(cMap.get(Bukkit.getServer()))).remove(mainCommand.getName());
                boolean unregistered = mainCommand.unregister((CommandMap)cMap.get(Bukkit.getServer()));
                mainCommand.unregister(commandMap);
                String msg = unregistered ? "Successfully unregistered command: " + mainCommand.getName() : "Failed to register command: " + mainCommand.getName();
                print(msg);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

