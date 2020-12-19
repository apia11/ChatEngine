package dev.alofi11.minecraft.servers.plugins.chatengine.components.commands;

import static java.util.Arrays.copyOfRange;
import static org.bukkit.util.StringUtil.copyPartialMatches;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.ComponentsList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseExpandableCommand extends BaseCommand {

  protected final ComponentsList componentsList;
  private final List<Subcommand> registeredSubcommands = new ArrayList<>();

  protected BaseExpandableCommand(@NotNull final ComponentsList componentsList,
      @NotNull String name,
      @NotNull String description, @NotNull String permissionName, @NotNull String... aliases) {
    super(componentsList, name, description, permissionName, aliases);
    this.componentsList = componentsList;
    initializeSubcommands();
  }

  @Override
  protected final void execute(@NotNull CommandSender sender, @NotNull String[] args) {
    if (args.length > 0) {
      Subcommand subcommand = getSubcommandByName(args[0]);
      if (subcommand != null) {
        if (hasPermission(sender, subcommand.getPermission())) {
          subcommand.execute(sender, copyOfRange(args, 1, args.length));
        }

        return;
      }
    }

    help(sender);
  }

  @Override
  public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
      @NotNull String[] args) {
    List<String> result = new ArrayList<>();

    if (sender.hasPermission(permission)) {
      if (args.length == 1) {
        copyPartialMatches(args[0], getRegisteredSubcommandsStrList(), result);
      } else {
        Subcommand subcommand = getSubcommandByName(args[0]);
        if (subcommand != null) {
          Map<Integer, List<String>> promptsMap = subcommand
              .tabComplete(sender, copyOfRange(args, 1, args.length));
          List<String> indexPrompts = promptsMap.getOrDefault((args.length - 2), new ArrayList<>());
          setPlaceholders(indexPrompts);
          copyPartialMatches(args[args.length - 1], indexPrompts, result);
        }
      }
    }

    return result;
  }

  @NotNull
  private List<String> setPlaceholders(@NotNull List<String> input) {
    if (input.size() == 1 && input.get(0).equalsIgnoreCase("%players%")) {
      input = getPlayersStrList();
    }

    return input;
  }

  @NotNull
  private List<String> getPlayersStrList() {
    return Bukkit.getOnlinePlayers().stream()
        .collect(ArrayList::new, (list, player) -> list.add(player.getName()), ArrayList::addAll);
  }

  @Nullable
  private Subcommand getSubcommandByName(@NotNull String name) {
    for (Subcommand subcommand : registeredSubcommands) {
      if (subcommand.getName().equalsIgnoreCase(name)) {
        return subcommand;
      }
    }

    return null;
  }

  @NotNull
  private List<String> getRegisteredSubcommandsStrList() {
    return registeredSubcommands.stream()
        .collect(ArrayList::new, (list, subcommand) -> list.add(subcommand.getName()),
            ArrayList::addAll);
  }

  protected abstract void help(@NotNull CommandSender sender);

  protected final void registerSubcommand(@NotNull Subcommand subcommand) {
    registeredSubcommands.add(subcommand);
  }

  protected abstract void initializeSubcommands();

}