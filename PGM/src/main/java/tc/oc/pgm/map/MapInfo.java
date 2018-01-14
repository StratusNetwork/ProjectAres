package tc.oc.pgm.map;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import com.google.common.collect.ComparisonChain;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Difficulty;
import org.bukkit.World.Environment;
import tc.oc.api.docs.PlayerId;
import tc.oc.api.docs.SemanticVersion;
import tc.oc.api.docs.virtual.MapDoc;
import tc.oc.commons.bukkit.chat.ListComponent;
import tc.oc.commons.bukkit.chat.NameStyle;
import tc.oc.commons.core.chat.Component;
import tc.oc.commons.core.chat.Components;
import tc.oc.commons.core.formatting.StringUtils;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.md_5.bungee.api.ChatColor.*;

/** Class describing the match-independent information about a map. */
public class MapInfo implements Comparable<MapInfo> {

    public final MapId id;

    public final SemanticVersion proto;

    /** Name of the map. */
    public final String name;

    public final SemanticVersion version;

    /** Optional game name to override the default */
    public final @Nullable BaseComponent game;

    public final MapDoc.Genre genre;

    public final Set<MapDoc.Gamemode> gamemodes;

    /** Short, one-line description of the objective of this map. */
    public final BaseComponent objective;

    /** List of authors and their contributions. */
    public final List<Contributor> authors;

    /** List of contributors and their contributions. */
    public final List<Contributor> contributors;

    /** List of rules for this map. */
    public final List<String> rules;

    /** Difficulty the map should be played on. */
    public final @Nullable Difficulty difficulty;

    /** Dimension the map should be loaded in */
    public final Environment dimension;

    /** Whether friendly fire should be on or off. */
    public final boolean friendlyFire;

    public MapInfo(SemanticVersion proto,
                   @Nullable String slug,
                   String name,
                   SemanticVersion version,
                   MapDoc.Edition edition,
                   MapDoc.Phase phase,
                   @Nullable BaseComponent game,
                   MapDoc.Genre genre,
                   Set<MapDoc.Gamemode> gamemodes,
                   BaseComponent objective,
                   List<Contributor> authors,
                   List<Contributor> contributors,
                   List<String> rules,
                   @Nullable Difficulty difficulty,
                   Environment dimension,
                   boolean friendlyFire) {

        this.id = new MapId(slug != null ? slug : MapId.slugifyName(name), edition, phase);

        this.proto = checkNotNull(proto);
        this.name = checkNotNull(name);
        this.version = checkNotNull(version);
        this.game = game;
        this.genre = checkNotNull(genre);
        this.gamemodes = checkNotNull(gamemodes);
        this.objective = checkNotNull(objective);
        this.authors = checkNotNull(authors);
        this.contributors = checkNotNull(contributors);
        this.rules = checkNotNull(rules);
        this.difficulty = difficulty;
        this.dimension = checkNotNull(dimension);
        this.friendlyFire = friendlyFire;

    }

    public String slug() { return id.slug(); }
    public MapDoc.Edition edition() { return id.edition(); }
    public MapDoc.Phase phase() { return id.phase(); }

    public BaseComponent getFormattedMapTitle() {
        return new Component(StringUtils.dashedChatMessage(DARK_AQUA + " " + this.name + GRAY + " " + version, "-", RED + "" + STRIKETHROUGH));
    }

    public BaseComponent getShortDescription() {
        BaseComponent componentedName = new Component(name).color(GOLD);

        List<Contributor> authors = getNamedAuthors();
        if(!authors.isEmpty()) {
            componentedName = new Component(DARK_PURPLE).translate("misc.authorship",
                                                                    componentedName,
                                                                    new ListComponent(Lists.transform(authors,
                                                                        contributors -> contributors.getStyledName(NameStyle.MAPMAKER)
                                                                    )));
        }

        return componentedName;
    }

    /**
     * Apply standard formatting (aqua + bold) to the map name
     */
    public BaseComponent getColoredName() {
        return new Component(name).color(AQUA).bold(true);
    }

    public BaseComponent getComponentName() {
        return new Component(name, AQUA, BOLD);
    }

    /**
     * Apply standard formatting (aqua + bold) to the map version
     */
    public BaseComponent getColoredVersion() {
        return new Component(version.toString()).color(AQUA).bold(true);
    }

    public List<Contributor> getNamedAuthors() {
        return Contributor.filterNamed(this.authors);
    }

    public List<Contributor> getNamedContributors() {
        return Contributor.filterNamed(this.contributors);
    }

    public Stream<Contributor> allContributors() {
        return Stream.concat(authors.stream(), contributors.stream());
    }

    public boolean isAuthor(PlayerId player) {
        for(Contributor author : authors) {
            if(player.equals(author.getUser())) return true;
        }
        return false;
    }

    public BaseComponent getLocalizedGenre() {
        switch(genre) {
            case OBJECTIVES: return new TranslatableComponent("map.genre.objectives");
            case DEATHMATCH: return new TranslatableComponent("map.genre.deathmatch");
            default: return new TranslatableComponent("map.genre.other");
        }
    }

    public BaseComponent getLocalizedEdition() {
        switch(edition()) {
            case STANDARD: return new TranslatableComponent("map.edition.standard");
            case RANKED: return new TranslatableComponent("map.edition.ranked");
            case TOURNAMENT: return new TranslatableComponent("map.edition.tournament");
            default: return Components.blank();
        }
    }

    @Override
    public int compareTo(MapInfo o) {
        return ComparisonChain.start()
            .compare(name, o.name)
            .compare(edition(), o.edition())
            .compare(phase(), o.phase())
            .result();
    }
}
