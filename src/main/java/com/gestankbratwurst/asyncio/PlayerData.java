package com.gestankbratwurst.asyncio;

import java.util.UUID;

public class PlayerData {

  private final UUID ownerId;
  private int kills;
  private int deaths;

  public PlayerData(UUID ownerId) {
    this.ownerId = ownerId;
  }

  public int getKills() {
    return kills;
  }

  public void setKills(int kills) {
    this.kills = kills;
  }

  public int getDeaths() {
    return deaths;
  }

  public void setDeaths(int deaths) {
    this.deaths = deaths;
  }

  public UUID getOwnerId() {
    return ownerId;
  }
}
