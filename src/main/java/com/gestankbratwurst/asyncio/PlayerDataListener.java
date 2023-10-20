package com.gestankbratwurst.asyncio;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlayerDataListener implements Listener {

  private final PlayerDataManager playerDataManager;
  private final PlayerDataAccess playerDataAccess;
  private final Executor ioExecutor = Executors.newSingleThreadExecutor();

  public PlayerDataListener(PlayerDataManager playerDataManager, PlayerDataAccess playerDataAccess) {
    this.playerDataManager = playerDataManager;
    this.playerDataAccess = playerDataAccess;
  }

  @EventHandler
  public void onJoin(AsyncPlayerPreLoginEvent event) {
    UUID playerId = event.getUniqueId();
    PlayerData data = playerDataAccess.read(playerId);
    if (data == null) {
      data = new PlayerData(playerId);
      playerDataAccess.create(data);
    }
    playerDataManager.addPlayerData(data);
  }

  public void onQuit(PlayerQuitEvent event) {
    UUID playerId = event.getPlayer().getUniqueId();
    PlayerData data = playerDataManager.removePlayerData(playerId);
    CompletableFuture.runAsync(() -> this.playerDataAccess.update(data), ioExecutor).whenComplete((unused, exception) -> {
      throw new RuntimeException(exception);
    });
  }

}
