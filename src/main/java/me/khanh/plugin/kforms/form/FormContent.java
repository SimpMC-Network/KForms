package me.khanh.plugin.kforms.form;

import me.khanh.plugin.kforms.requirement.*;
import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class FormContent implements Utils {
    private final String name;
    private ContentType contentType = null;
    private List<String> content = null;
//    private String text = null;
    private String url= null;
    private String path = null;
    private String button = null;
    private String button1 = null;
    private String button2 = null;
//    private List<String> options = new ArrayList<>();
//    private float min = 0;
//    private float max = 0;
//    private float step = 0;
//    private float default_value = 0;
//    private String label = null;
//    private boolean toggle_default_value = false;
    private boolean canParse = true;
    private final ConfigurationSection section;
    private final Form form;
    private List<String> clickCommands = new ArrayList<>();
    private final List<Requirement> requirements = new ArrayList<>();
    private RequirementList requirementList;

    FormContent(Form form, ConfigurationSection section){
        this.form = form;
        this.section = section;
        this.name = section.getName();
    }

    public Form getForm(){
        return form;
    }

    public String getName() {
        return name;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public List<String> getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public String getButton1() {
        return button1;
    }

    public String getButton2() {
        return button2;
    }

    public List<String> getClickCommands(){
        return clickCommands;
    }

    public ConfigurationSection getSection(){
        return section;
    }

    public RequirementList getRequirementList(){
        return requirementList;
    }

    public FormContent reParseFormContent(){
        reInitRequirements();
        if (section.get("commands") != null){
            clickCommands = section.getStringList("commands");
        }
        if (form.getFormType().equals(Form.FormType.MODAL_FORM)){
            if (section.get("type") == null){
                setCanParse(false);
                return this;
            }
            if (!isVCT(section.getString("type"))){
                setCanParse(false);
                return this;
            }
            contentType = ContentType.valueOf(Objects.requireNonNull(section.getString("type")).toUpperCase());
            switch (contentType){
                case CONTENT:
                    if (section.get("text") == null){
                        setCanParse(false);
                        return this;
                    }
                    setCanParse(true);
                    if (!section.getStringList("text").isEmpty()){
                        content = section.getStringList("text");
                    } else {
                        content = Collections.singletonList(section.getString("text"));
                    }
                    return this;
                case BUTTON1:
                    if (section.get("text") == null){
                        setCanParse(false);
                        return this;
                    }
                    button1 = section.getString("text");
                    setCanParse(true);
                    return this;
                case BUTTON2:
                    if (section.get("text") == null){
                        setCanParse(false);
                        return this;
                    }
                    button2 = section.getString("text");
                    setCanParse(true);
                    return this;
                default:
                    setCanParse(false);
                    return this;
            }
        }
        else {
            if (form.getFormType().equals(Form.FormType.SIMPLE_FORM)){
                if (section.get("type") == null){
                    setCanParse(false);
                    return this;
                }
                if (!isVCT(Objects.requireNonNull(section.getString("type")).toUpperCase())){
                    setCanParse(false);
                    return this;
                }
                contentType = ContentType.valueOf(Objects.requireNonNull(section.getString("type")).toUpperCase());
                switch (contentType){
                    case CONTENT:
                        for (FormContent formContent: form.getContents()){
                            if (formContent.getContentType().equals(ContentType.CONTENT)){
                                setCanParse(false);
                                return this;
                            }
                        }
                        if (section.get("text") == null){
                            setCanParse(false);
                            return this;
                        }
                        setCanParse(true);
                        if (!section.getStringList("text").isEmpty()){
                            content = section.getStringList("text");
                        } else {
                            content = Collections.singletonList(section.getString("text"));
                        }
                        return this;
                    case BUTTON:
                        if (section.get("text") == null){
                            setCanParse(false);
                            return this;
                        }
                        button = section.getString("text");
                        if (section.get("url") != null){
                            url = section.getString("url");
                        }
                        if (section.get("path") != null){
                            path = section.getString("path");
                        }
                        setCanParse(true);
                        return this;
                    default:
                        setCanParse(false);
                        return this;
                }
            } else {
                return this;
            }
        }
    }

    public FormContent parseFormContent(){
        initRequirements();
        if (section.get("commands") != null){
            clickCommands = section.getStringList("commands");
        }
        if (form.getFormType().equals(Form.FormType.MODAL_FORM)){
            if (section.get("type") == null){
                print("An error occurred during loading " + form.getName() + "." + section.getName() +
                        ". Please set type for content" + section.getName());
                setCanParse(false);
                return this;
            }
            if (!isVCT(section.getString("type"))){
                print("An error occurred during loading " + form.getName() + "." + section.getName() +
                        ". Please enter the correct type of content" + section.getName());
                setCanParse(false);
                return this;
            }
            contentType = ContentType.valueOf(Objects.requireNonNull(section.getString("type")).toUpperCase());
            switch (contentType){
                case CONTENT:
                    if (section.get("text") == null){
                        print("An error occurred during loading " + form.getName() + "." + section.getName() +
                                ". Please set text for content" + section.getName());
                        setCanParse(false);
                        return this;
                    }
                    setCanParse(true);
                    if (!section.getStringList("text").isEmpty()){
                        content = section.getStringList("text");
                    } else {
                        content = Collections.singletonList(section.getString("text"));
                    }
                    return this;
                case BUTTON1:
                    if (section.get("text") == null){
                        print("An error occurred during loading " + form.getName() + "." + section.getName() +
                                ". Please set text for content" + section.getName());
                        setCanParse(false);
                        return this;
                    }
                    button1 = section.getString("text");
                    setCanParse(true);
                    return this;
                case BUTTON2:
                    if (section.get("text") == null){
                        print("An error occurred during loading " + form.getName() + "." + section.getName() +
                                ". Please set text for content" + section.getName());
                        setCanParse(false);
                        return this;
                    }
                    button2 = section.getString("text");
                    setCanParse(true);
                    return this;
                default:
                    print("An error occurred during loading " + form.getName() + "." + section.getName() +
                            ". " + form.getFormType().toString() + " not support " + section.getString("type"));
                    setCanParse(false);
                    return this;
            }
        }
        else {
            if (form.getFormType().equals(Form.FormType.SIMPLE_FORM)){
                if (section.get("type") == null){
                    print("An error occurred during loading " + form.getName() + "." + section.getName() +
                            ". Please set type for content" + section.getName());
                    setCanParse(false);
                    return this;
                }
                if (!isVCT(Objects.requireNonNull(section.getString("type")).toUpperCase())){
                    print("An error occurred during loading " + form.getName() + "." + section.getName() +
                            ". Please enter the correct type of content" + section.getName());
                    setCanParse(false);
                    return this;
                }
                contentType = ContentType.valueOf(Objects.requireNonNull(section.getString("type")).toUpperCase());
                switch (contentType){
                    case CONTENT:
                        for (FormContent formContent: form.getContents()){
                            if (formContent.getContentType().equals(ContentType.CONTENT)){
                                severe("ERROR: Each form can only contain one Content");
                                setCanParse(false);
                                return this;
                            }
                        }
                        if (section.get("text") == null){
                            print("An error occurred during loading " + form.getName() + "." + section.getName() +
                                    ". Please set text for content" + section.getName());
                            setCanParse(false);
                            return this;
                        }
                        setCanParse(true);
                        if (!section.getStringList("text").isEmpty()){
                            content = section.getStringList("text");
                        } else {
                            content = Collections.singletonList(section.getString("text"));
                        }
                        return this;
                    case BUTTON:
                        if (section.get("text") == null){
                            print("An error occurred during loading " + form.getName() + "." + section.getName() +
                                    ". Please set text for content" + section.getName());
                            setCanParse(false);
                            return this;
                        }
                        button = section.getString("text");
                        if (section.get("url") != null){
                            url = section.getString("url");
                        }
                        if (section.get("path") != null){
                            path = section.getString("path");
                        }
                        setCanParse(true);
                        return this;
                    default:
                        print("An error occurred during loading " + form.getName() + "." + section.getName() +
                                ". " + form.getFormType().toString() + " not support " + section.getString("type"));
                        setCanParse(false);
                        return this;
                }
            } else {
                severe("CUSTOM FORM not support.");
                return this;
            }
        }
    }

    @SuppressWarnings("all")
    public boolean isVCT(String contentType){
        try {
            ContentType.valueOf(contentType);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    public boolean isCanParse() {
        return canParse;
    }

    public void setCanParse(boolean canParse) {
        this.canParse = canParse;
    }

    public String getButton() {
        return button;
    }

    public void reInitRequirements(){
        if (section.get("requirements") != null){
            for (String s: Objects.requireNonNull(section.getConfigurationSection("requirements")).getKeys(false)){
                if (s.equalsIgnoreCase("minimum_requirements") || s.equalsIgnoreCase("stop_at_success") || s.equalsIgnoreCase("deny_commands")){
                    continue;
                }
                ConfigurationSection reqSection = section.getConfigurationSection("requirements." + s);
                if (reqSection.get("type") != null){
                    RequirementType requirementType = RequirementType.getType(reqSection.getString("type"));
                    if (requirementType != null){
                        switch (requirementType){
                            case HAS_EXP:
                            case DOES_NOT_HAVE_EXP:
                                int amount;
                                if (reqSection.get("amount") == null){
                                    continue;
                                }
                                try {
                                    amount = Integer.parseInt(Objects.requireNonNull(reqSection.getString("amount")));
                                } catch (NumberFormatException e){
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
                                    continue;
                                }
                                if (reqSection.getString("output") == null){
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
                                    continue;
                                }
                                if (reqSection.getString("regex") == null){
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
            ConfigurationSection reqSection = section.getConfigurationSection("requirements");
            if (reqSection.get("deny_commands") != null && !reqSection.getStringList("deny_commands").isEmpty()) {
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

    public void initRequirements(){
        if (section.get("requirements") != null){
            for (String s: Objects.requireNonNull(section.getConfigurationSection("requirements")).getKeys(false)){
                if (s.equalsIgnoreCase("minimum_requirements") || s.equalsIgnoreCase("stop_at_success") || s.equalsIgnoreCase("deny_commands")){
                    continue;
                }
                ConfigurationSection reqSection = section.getConfigurationSection("requirements." + s);
                if (reqSection.get("type") == null){
                    severe("ERROR: Can't find TYPE requirements of " + s + " " + getName() + " " + getForm().getName());
                } else {
                    RequirementType requirementType = RequirementType.getType(reqSection.getString("type"));
                    if (requirementType == null){
                        severe("ERROR: Can't find TYPE requirements with name " + reqSection.getString("type") +
                                "in " + s + " " + getName() + " " + getForm().getName());
                    } else {
                        switch (requirementType){
                            case HAS_EXP:
                            case DOES_NOT_HAVE_EXP:
                                int amount;
                                if (reqSection.get("amount") == null){
                                    severe("ERROR: Please set amount for" + reqSection.getCurrentPath());
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
            ConfigurationSection reqSection = section.getConfigurationSection("requirements");
            if (reqSection.get("deny_commands") != null && !reqSection.getStringList("deny_commands").isEmpty()) {
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

    enum ContentType {
        CONTENT,
        BUTTON,
        BUTTON1,
        BUTTON2,
        DROPDOWN,
        INPUT,
        LABEL,
        SLIDER,
        STEP_SLIDER,
        TOGGLE
    }
}
