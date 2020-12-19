package dev.alofi11.minecraft.servers.plugins.chatengine.components.permissions;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.DisableSupport;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

public final class PermissionsManager implements DisableSupport {

  private static final String PLUGIN_PERMISSIONS_PREFIX = "chatengine";
  private final List<Permission> registeredPermissions = new ArrayList<>();

  @NotNull
  public Permission register(@NotNull String permissionName) {
    Permission permission = new Permission(permissionName);
    registeredPermissions.add(permission);
    Bukkit.getPluginManager().addPermission(permission);

    return permission;
  }

  public void unregisterAll() {
    registeredPermissions.forEach(Bukkit.getPluginManager()::removePermission);
    registeredPermissions.clear();
  }

  public void unregister(@NotNull Permission permission) {
    registeredPermissions.remove(permission);
    Bukkit.getPluginManager().removePermission(permission);
  }

  @NotNull
  public Permission registerPluginPermission(@NotNull String permissionName) {
    return register(PLUGIN_PERMISSIONS_PREFIX + "." + permissionName);
  }

  @Override
  public void onDisable() {
    unregisterAll();
  }

}
