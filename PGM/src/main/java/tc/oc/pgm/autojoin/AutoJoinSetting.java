package tc.oc.pgm.autojoin;

import me.anxuiz.settings.Setting;
import me.anxuiz.settings.SettingBuilder;
import me.anxuiz.settings.types.BooleanType;

public class AutoJoinSetting {
    private static final Setting INSTANCE = new SettingBuilder()
        .name("AutoJoin").alias("aj")
        .summary("Toggles the AutoJoin feature")
        .type(new BooleanType())
        .defaultValue(false)
        .get();

    public static Setting get() {
        return INSTANCE;
    }
}
