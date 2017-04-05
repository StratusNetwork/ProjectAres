package tc.oc.commons.bukkit.raindrops;

import org.bukkit.configuration.ConfigurationSection;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class RaindropConfiguration {

    public ConfigurationSection configurationSection;

    @Inject
    public RaindropConfiguration(ConfigurationSection configurationSection) {
        this.configurationSection = checkNotNull(configurationSection);
    }

    public String singular() {
        return configurationSection.getString("singular");
    }
    public String plural() {
        return configurationSection.getString("plural");
    }
}
