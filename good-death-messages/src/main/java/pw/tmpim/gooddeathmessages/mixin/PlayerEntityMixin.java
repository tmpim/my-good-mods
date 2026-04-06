package pw.tmpim.gooddeathmessages.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import pw.tmpim.gooddeathmessages.Victim;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements Victim {
  @Unique
  private final Data data = new Data();

  @Override
  public @NotNull Data getGooddms$victim() {
    return data;
  }
}
