package tc.oc.pgm.commands;

import tc.oc.commons.core.commands.CommandBinder;
import tc.oc.commons.core.inject.HybridManifest;

public class CommandManifest extends HybridManifest {
    @Override
    protected void configure() {
        CommandBinder binder = new CommandBinder(binder());
        binder.register(AdminCommands.class);
        binder.register(MapCommands.class);
        binder.register(MatchCommands.class);
    }
}
