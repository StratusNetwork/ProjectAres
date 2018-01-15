package tc.oc.pgm.rotation;

import tc.oc.commons.core.commands.CommandBinder;
import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.minecraft.api.event.ListenerBinder;

public class RotationManifest extends HybridManifest {
    @Override
    protected void configure() {
        bind(DynamicRotationListener.class);
        new ListenerBinder(binder()).bindListener().to(DynamicRotationListener.class);
        CommandBinder binder = new CommandBinder(binder());
        binder.register(RotationControlCommands.RotationControlParent.class);
        binder.register(RotationEditCommands.RotationEditParent.class);
    }
}
