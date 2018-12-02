package tc.oc.pgm.polls;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import tc.oc.commons.core.logging.Loggers;
import tc.oc.commons.core.plugin.PluginFacet;
import tc.oc.pgm.map.MapLibrary;
import tc.oc.pgm.map.PGMMap;

@Singleton
public class PollBlacklist implements PluginFacet {

    private List<PGMMap> blacklistedMaps = new ArrayList<>();

    private final MapLibrary mapLibrary;
    private final PollConfig pollConfig;
    private final Logger logger;

    @Inject PollBlacklist(MapLibrary mapLibrary, PollConfig pollConfig, Loggers loggers) {
        this.mapLibrary = mapLibrary;
        this.pollConfig = pollConfig;
        this.logger = loggers.get(getClass());
    }

    @Override
    public void enable() {
        loadPollBlacklist();
    }

    public void loadPollBlacklist() {
        Path filepath = pollConfig.getPollBlacklistPath();
        if (filepath == null) return;
        List<String> lines = null;
        try {
            lines = Files.readAllLines(filepath, Charsets.UTF_8);
        } catch (IOException e) {
            logger.severe("Error in reading poll blacklist from file!");
        }
        if (lines == null) return;
        ImmutableList.Builder<PGMMap> maps = ImmutableList.builder();
        for(String line : lines) {
            if (line.contains("#")) {
                line = line.substring(0, line.indexOf("#"));
            }

            line = line.trim();
            if(line.isEmpty()) {
                continue;
            }

            Optional<PGMMap> map = mapLibrary.getMapByNameOrId(line);
            if(map.isPresent()) {
                maps.add(map.get());
            } else {
                logger.warning("Unknown map '" + line + "' when parsing " + filepath.toString());
            }
        }
        this.blacklistedMaps = maps.build();
    }

    public boolean isBlacklisted(PGMMap map) {
        return blacklistedMaps.contains(map);
    }
}
