package io.github.trainb0y.simplehud.config;

import io.github.trainb0y.simplehud.elements.*;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.trainb0y.simplehud.SimpleHud.logger;

@ConfigSerializable
public class Config {
    private static final String version = "1.0";
    public static boolean hudEnabled = true;
    public static boolean colors = true;
    public static ArrayList<Element> elements = new ArrayList<Element>();

    public static void loadConfig() {
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(FabricLoader.getInstance().getConfigDir().resolve("simplehud.conf"))
                .build();

        CommentedConfigurationNode root;
        try {
            root = loader.load();
            String configVersion = root.node("version").getString();
            if (version.equals(configVersion)) {
                logger.warn("Found config version: "+ configVersion+", current version: "+version);
                logger.warn("Attempting to load anyway");
            }
            hudEnabled = root.node("enabled").getBoolean();
            colors = root.node("colors").getBoolean();
            elements = new ArrayList<Element>(Objects.requireNonNull(root.node("elements").getList(Element.class)));
            if (elements.size() == 0) throw new NullPointerException(); // configurate doesn't error when file not found
            logger.info("Loaded existing configuration");
            return;

        } catch (ConfigurateException e) {
            logger.warn("Failed to load existing configuration! Using defaults. " + e);
        } catch (NullPointerException e) {
            logger.warn("Invalid configuration file! Using defaults.");
        }
        applyDefaultConfig();
    }

    public static void applyDefaultConfig() {
        Config.elements = new ArrayList<>();
        Config.hudEnabled = true;
        Config.colors = true;
        Config.elements.addAll(List.of(
                new FPSElement(10, 5, true),
                new BiomeElement(10, 15, false),
                new LatencyElement(10, 15, false),
                new PositionElement(10, 15, false),
                new TimeElement(10, 15, false)
        ));
        logger.info("Applied default config settings");
    }

    public static void saveConfig() {
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(FabricLoader.getInstance().getConfigDir().resolve("simplehud.conf"))
                .build();

        CommentedConfigurationNode root;
        try {
            root = loader.load();
            root.node("version").set(version);
            root.node("enabled").set(hudEnabled);
            root.node("colors").set(colors);
            root.node("elements").setList(Element.class, elements);
            loader.save(root);
            logger.info("Saved configuration");

        } catch (ConfigurateException e) {
            logger.warn("Failed to save configuration! " + e);
        }
    }
}
