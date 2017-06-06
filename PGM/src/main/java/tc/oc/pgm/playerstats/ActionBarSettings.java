package tc.oc.pgm.playerstats;

import me.anxuiz.settings.Setting;
import me.anxuiz.settings.SettingBuilder;
import me.anxuiz.settings.types.EnumType;
import me.anxuiz.settings.types.Name;

public class ActionBarSettings {
    private static final Setting inst = new SettingBuilder()
            .name("ActionbarMessage").alias("hotbarmsg")
            .summary("Kill/Death information above your hotbar")
            .description("Options: \n" +
                        "ALWAYS: Always display the information. \n" +
                        "DEATH: Displays information only after a death and may fade away. \n" +
                        "NEVER: Never display this information.")
            .type(new EnumType<>("Actionbar settings", Options.class))
            .defaultValue(Options.DEATH).get();

    public static Setting get() {
        return inst;
    }

    public enum Options {
        @Name("all") ALL,
        @Name("death")DEATH,
        @Name("never")NEVER

    }

}
