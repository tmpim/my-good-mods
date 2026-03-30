package pw.tmpim.mygoodmod;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class MyGoodModConfig {
  @ConfigEntry(
    name = "Boats drop boat item",
    description = "Boats drop the boat item instead of planks and sticks"
  )
  public Boolean boatsDropBoatItem = true;
}
