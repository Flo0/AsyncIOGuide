package com.gestankbratwurst.asyncio;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ExampleCommand implements CommandExecutor {

  private final PlayerDataManager playerDataManager;
  private final PlayerDataAccess playerDataAccess;

  public ExampleCommand(PlayerDataManager playerDataManager, PlayerDataAccess playerDataAccess) {
    this.playerDataManager = playerDataManager;
    this.playerDataAccess = playerDataAccess;
  }

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {

    if (args.length != 2) {
      return true;
    }

    String playerIdStr = args[0]; // Never do that. Just as an example
    UUID playerId = UUID.fromString(playerIdStr);

    int killsToSet = Integer.parseInt(args[1]);

    PlayerData playerData = this.playerDataManager.getPlayerData(playerId);
    if (playerData != null) {
      playerData.setKills(killsToSet);
      commandSender.sendMessage("Successfully changed kills.");
    } else {
      commandSender.sendMessage("Loading data...");
      CompletableFuture
          .supplyAsync(() -> this.playerDataAccess.read(playerId))
          .thenAccept(data -> {
            data.setKills(killsToSet);
            this.playerDataAccess.update(data);
          }).thenRun(() -> {
            JavaPlugin plugin = JavaPlugin.getPlugin(AsyncIO.class);
            Bukkit.getScheduler().runTask(plugin, () -> {
              commandSender.sendMessage("Successfully changed kills.");
            });
          });
    }

    return true;
  }
}
