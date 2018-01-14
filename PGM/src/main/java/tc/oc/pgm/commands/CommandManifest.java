package tc.oc.pgm.commands;

import tc.oc.commons.core.commands.CommandBinder;
import tc.oc.commons.core.inject.HybridManifest;

public class CommandManifest extends HybridManifest {
    @Override
    protected void configure() {
        new CommandBinder(binder()).register(AdminCommands.class);
        new CommandBinder(binder()).register(MapCommands.class);
        new CommandBinder(binder()).register(MatchCommands.class);
    }
}
