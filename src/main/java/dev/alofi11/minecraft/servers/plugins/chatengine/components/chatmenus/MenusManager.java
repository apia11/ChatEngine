package dev.alofi11.minecraft.servers.plugins.chatengine.components.chatmenus;

import dev.alofi11.minecraft.servers.plugins.chatengine.ChatEnginePlugin;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.ComponentsList;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.DisableSupport;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.config.MenusConfigWrapper;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.config.SimpleConfigWrapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MenusManager implements DisableSupport {

  private static final List<String> CONFIG_IGNORE_LIST = Collections.singletonList("version");
  private final ComponentsList componentsList;
  private final MenusConfigWrapper config;
  private final Map<String, Menu> menus = new HashMap<>();
  private ChatBuffer chatBuffer;

  public MenusManager(@NotNull final ComponentsList componentsList) {
    this.componentsList = componentsList;
    final ChatEnginePlugin plugin = componentsList.getNN(ChatEnginePlugin.class);
    this.config = new MenusConfigWrapper(plugin);

    if (config.isActualVersion()) {
      this.chatBuffer = new ChatBuffer(plugin);
      initializeMenus();
    }
  }

  @Nullable
  Menu getMenu(@NotNull String name) {
    return menus.get(name);
  }

  public void onDisable() {
    menus.forEach((name, menu) -> menu.onDisable());
    chatBuffer.onDisable();
  }

  private void initializeMenus() {
    config.getKeys(false).forEach((key) -> {
      if (!CONFIG_IGNORE_LIST.contains(key)) {
        initializeMenu(key);
      }
    });
  }

  private void initializeMenu(@NotNull String key) {
    ConfigurationSection menuSection = config.getNNSection(key);
    Menu menu = new Menu(key, new SimpleConfigWrapper(menuSection), chatBuffer, componentsList,
        this);
    if (menu.isSuccessfulInitialized()) {
      menus.put(key, menu);
    }
  }

}