package com.github.thefrieber.ueu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
Integration with Elytra BHop
Reflections is required due to the decision to make player list package-private
 */
public final class BHopIntegration {

    private static boolean initPluginClass = false;
    private static Class<? extends JavaPlugin> _pluginClass = null;
    public static Class<? extends JavaPlugin> getPluginClass() {
        if (!initPluginClass) {
            try {
                _pluginClass = Class.forName("dev.orf1.plugins.Main").asSubclass(JavaPlugin.class);
            } catch (ClassNotFoundException | ClassCastException ignored) {
            } catch (LinkageError e) {
                Main.getPlugin(Main.class).getLogger().warning("Linkage error while trying to hook into Elytra BHop");
                e.printStackTrace();
            }
            initPluginClass = true;
        }
        return _pluginClass;
    }

    private static boolean initPlugin = false;
    private static JavaPlugin _plugin = null;
    public static JavaPlugin getPlugin() {
        if (!initPlugin) {
            Class<? extends JavaPlugin> clazz = getPluginClass();
            if (clazz != null) {
                for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                    if (clazz.isInstance(p)) {
                        _plugin = clazz.cast(p);
                        if (p.getName().equalsIgnoreCase("elytrabhop")) break;
                    }
                }
            }
            initPlugin = true;
        }
        return _plugin;
    }

    public static List<Player> getPlayerList() {
        JavaPlugin plugin = getPlugin();
        if (plugin == null) return Collections.emptyList();
        Class<? extends JavaPlugin> clazz = plugin.getClass();
        try {
            Field f = clazz.getDeclaredField("list");
            f.setAccessible(true);
            Object ob = f.get(plugin);
            List<?> l = (List<?>) ob;
            List<Player> ret = new ArrayList<>();
            for (Object o : l) ret.add((Player) o);
            return ret;
        } catch (ReflectiveOperationException | ClassCastException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
