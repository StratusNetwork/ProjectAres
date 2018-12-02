package tc.oc.pgm.polls;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import tc.oc.commons.core.commands.CommandBinder;
import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.commons.core.plugin.PluginFacetBinder;
import tc.oc.minecraft.api.event.ListenerBinder;
import tc.oc.pgm.polls.commands.PollCommands;
import tc.oc.pgm.polls.types.PollCustom;
import tc.oc.pgm.polls.types.PollKick;
import tc.oc.pgm.polls.types.PollMutation;
import tc.oc.pgm.polls.types.PollNextMap;

public class PollManifest extends HybridManifest {

    @Override
    protected void configure() {

        final FactoryModuleBuilder fmb = new FactoryModuleBuilder();
        install(fmb.build(PollCustom.Factory.class));
        install(fmb.build(PollKick.Factory.class));
        install(fmb.build(PollNextMap.Factory.class));
        install(fmb.build(PollMutation.Factory.class));

        final PluginFacetBinder facets = new PluginFacetBinder(binder());
        facets.register(PollManager.class);
        facets.register(PollBlacklist.class);

        new CommandBinder(binder()).register(PollCommands.class);

        new ListenerBinder(binder()).bindListener().to(PollListener.class);
    }
}
