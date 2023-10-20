package com.gestankbratwurst.asyncio;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class AsyncIO extends JavaPlugin {

  private PlayerDataManager playerDataManager;
  private PlayerDataAccess playerDataAccess;

  @Override
  public void onEnable() {
    this.playerDataManager = new PlayerDataManager();
    this.playerDataAccess = new PlayerDataAccess();
    PlayerDataListener listener = new PlayerDataListener(this.playerDataManager, this.playerDataAccess);
    Bukkit.getPluginManager().registerEvents(listener, this);
  }

  @Override
  public void onDisable() {
    Collection<PlayerData> leftData = playerDataManager.getAllLoadedData();
    leftData.forEach(this.playerDataAccess::update);
  }

  /**
   * This method reads the kills of a player, and gives every online
   * player as many diamond as the player has kills.
   */
  public void addDiamondsToAllPlayerDependingOnKills(UUID playerId) {
    PlayerData playerData = this.playerDataManager.getPlayerData(playerId);
    if (playerData != null) {
      // Player is online so we can just do this
      giveAllPlayersDiamonds(playerData.getKills());
    } else {
      // Player is NOT online, so we need to load the data async and give
      // the diamonds 1 or 2 ticks later, depending on how long the DB
      // call takes.
      BukkitScheduler scheduler = Bukkit.getScheduler();
      CompletableFuture.runAsync(() -> {
        PlayerData loadedData = this.playerDataAccess.read(playerId);
        int kills = loadedData.getKills();
        // Very important: You can not do anything on the server
        // while in an async context. This is why we need to start
        // a new task, which will run code on the main thread when the next
        // tick starts!
        scheduler.runTask(this, () -> giveAllPlayersDiamonds(kills));
      });
    }
  }

  private void giveAllPlayersDiamonds(int amount) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      ItemStack diamonds = new ItemStack(Material.DIAMOND, amount);
      player.getInventory().addItem(diamonds);
    }
  }

}
