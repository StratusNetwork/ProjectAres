package tc.oc.pgm.autojoin;

import tc.oc.commons.bukkit.settings.SettingBinder;
import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.pgm.match.inject.MatchModuleFixtureManifest;

public class AutoJoinManifest extends HybridManifest {
    @Override
    protected void configure() {
        new SettingBinder(publicBinder()).addBinding().toInstance(AutoJoinSetting.get());
        install(new MatchModuleFixtureManifest<AutoJoinMatchModule>(){});
        
    }
}
