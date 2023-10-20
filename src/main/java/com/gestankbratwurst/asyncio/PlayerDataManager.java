package com.gestankbratwurst.asyncio;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

  private final Map<UUID, PlayerData> livePlayerData = new ConcurrentHashMap<>();

  public void addPlayerData(PlayerData playerData) {
    this.livePlayerData.put(playerData.getOwnerId(), playerData);
  }

  public PlayerData getPlayerData(UUID playerId) {
    return this.livePlayerData.get(playerId);
  }

  public PlayerData removePlayerData(UUID playerId) {
    return this.livePlayerData.remove(playerId);
  }

  public Collection<PlayerData> getAllLoadedData() {
    return List.copyOf(this.livePlayerData.values());
  }

}
