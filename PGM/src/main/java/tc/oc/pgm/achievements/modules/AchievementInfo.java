package tc.oc.pgm.achievements.modules;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AchievementInfo {
    String name() default "";
    String description() default "";
    boolean secret() default false;
}
