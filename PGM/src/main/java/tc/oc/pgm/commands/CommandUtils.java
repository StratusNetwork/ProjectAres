package tc.oc.pgm.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import org.bukkit.command.CommandSender;
import tc.oc.commons.core.commands.TranslatableCommandException;
import tc.oc.commons.core.formatting.StringUtils;
import tc.oc.pgm.PGM;
import tc.oc.pgm.features.FeatureDefinition;
import tc.oc.pgm.map.MapLibrary;
import tc.oc.pgm.map.PGMMap;
import tc.oc.pgm.match.Competitor;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchManager;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.rotation.RotationManager;
import tc.oc.pgm.rotation.RotationState;

public class CommandUtils {
    @Inject private static MatchManager matchManager;

    public static List<String> completeMapName(String prefix) {
        return StringUtils.complete(prefix, PGM.get().getMapLibrary().getMapNames());
    }

    public static PGMMap getMap(String search) {
        final MapLibrary library = PGM.get().getMapLibrary();

        PGMMap map = library.getMapByNameOrId(search).orElse(null);
        if(map != null) return map;

        final String name = StringUtils.bestFuzzyMatch(search, library.getMapNames(), 0.9);
        return name == null ? null : library.getMapByNameOrId(name).orElse(null);
    }

    public static PGMMap getMap(String search, CommandSender sender) throws CommandException {
        PGMMap map;
        if((map = getMap(search)) == null) {
            throw new TranslatableCommandException("command.mapNotFound");
        }
        return map;
    }

    public static Match getMatch(CommandSender sender) throws CommandException {
        Match match = matchManager.getCurrentMatch(sender);
        if(match == null) {
            throw new TranslatableCommandException("command.context");
        }
        return match;
    }

    public static <T extends MatchModule> T getMatchModule(Class<T> klass, CommandSender sender) throws CommandException {
        T mm = getMatch(sender).getMatchModule(klass);
        if(mm == null) {
            throw new TranslatableCommandException("command.moduleNotFound", klass.getSimpleName());
        }
        return mm;
    }

    public static Competitor getCompetitor(String search, CommandSender sender) throws CommandException {
        Match match = getMatch(sender);

        Map<String, Competitor> byName = new HashMap<>(match.getCompetitors().size());
        for(Competitor competitor : match.getCompetitors()) {
            byName.put(competitor.getName(sender), competitor);
        }

        Competitor competitor = StringUtils.bestFuzzyMatch(search, byName, 0.9);
        if(competitor == null) {
            throw new TranslatableCommandException("command.competitorNotFound");
        }

        return competitor;
    }

    public static @Nonnull RotationState getRotation(String search, CommandSender sender) throws CommandException {
        RotationManager manager = matchManager.getRotationManager();

        RotationState rotation = search != null ? manager.getRotation(search) : manager.getRotation();
        if(rotation == null) {
            throw new TranslatableCommandException("command.rotationNotFound");
        }

        return rotation;
    }

    public static <T extends FeatureDefinition> T getFeatureDefinition(String id, CommandSender sender, Class<T> type) throws CommandException {
        Match match = getMatch(sender);
        T feature = match.getModuleContext().features().get(id, type);
        if(feature == null) {
            throw new TranslatableCommandException("command.noFeature", type.getSimpleName(), id);
        }
        return feature;
    }

    public static MatchPlayer senderToMatchPlayer(CommandSender sender) throws CommandException {
        return getMatch(sender).getPlayer(tc.oc.commons.bukkit.commands.CommandUtils.senderToPlayer(sender));
    }

    public static MatchPlayer getMatchPlayerOrSelf(CommandContext args, CommandSender sender, int index) throws CommandException {
        return getMatch(sender).getPlayer(tc.oc.commons.bukkit.commands.CommandUtils.getPlayerOrSelf(args, sender, index));
    }

    public static MatchPlayer getMatchPlayer(CommandContext args, CommandSender sender, int index) throws CommandException {
        return getMatch(sender).getPlayer(tc.oc.commons.bukkit.commands.CommandUtils.getPlayer(args, sender, index));
    }

    public static MatchPlayer findSingleMatchPlayer(CommandContext args, CommandSender sender, int index) throws CommandException {
        return getMatch(sender).getPlayer(tc.oc.commons.bukkit.commands.CommandUtils.findOnlinePlayer(args, sender, index));
    }
}
