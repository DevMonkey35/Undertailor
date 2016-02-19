package me.scarlet.undertailor.texts.parse;

public enum TextParam {

    FONT("FONT", ""), //TODO: Have Xemiru fill in default values.
    STYLE("STYLE", ""), //TODO: Have Xemiru fill in default values.
    COLOR("COLOR", ""), //TODO: Have Xemiru fill in default values.
    SOUND("SOUND", ""), //TODO: Have Xemiru fill in default values.
    SPEED("SPEED", ""), //TODO: Have Xemiru fill in default values.
    DELAY("DELAY", ""), //TODO: Have Xemiru fill in default values.
    BOLD("BOLD", ""), //TODO: Have Xemiru fill in default values.
    ITALIC("ITALIC", ""), //TODO: Have Xemiru fill in default values.
    UNDERLINE("UNDERLINE", ""), //TODO: Have Xemiru fill in default values.
    UNDEFINED("UNDEFINED", ""), //TODO: Have Xemiru fill in default values.
    ;

    private final String name;
    private final String defaultValue;

    TextParam(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public String toString() {
        return String.format("[%s:%s]", this.getName(), this.getDefaultValue());
    }

    public static TextParam of(String string) {
        for (TextParam param : values()) {
            if (param.getName().equalsIgnoreCase(string)) {
                return param;
            }
        }

        return UNDEFINED;
    }
}
