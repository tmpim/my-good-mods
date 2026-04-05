package pw.tmpim.gooddeathmessages.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import pw.tmpim.gooddeathmessages.ExplosionTracker;
import pw.tmpim.gooddeathmessages.Victim;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements Victim {
  @Unique
  private ArrowEntity shot = null;

  @Unique
  private boolean pricked = false;

  @Unique
  private boolean struck = false;

  @Unique
  private boolean lit = false;

  @Unique
  private ExplosionTracker.BlastSource blastSource;

  @Override
  public @Nullable ArrowEntity getGooddms$shotBy() {
    return shot;
  }

  @Override
  public void setGooddms$shotBy(@Nullable ArrowEntity arrowEntity) {
    shot = arrowEntity;
  }

  @Override
  public boolean getGooddms$pricked() {
    return pricked;
  }

  @Override
  public void setGooddms$pricked(boolean b) {
    pricked = b;
  }

  @Override
  public ExplosionTracker.@Nullable BlastSource getGooddms$blastSource() {
    return blastSource;
  }

  @Override
  public void setGooddms$blastSource(ExplosionTracker.@Nullable BlastSource blastSource) {
    this.blastSource = blastSource;
  }

  @Override
  public boolean getGooddms$struck() {
    return struck;
  }

  @Override
  public void setGooddms$struck(boolean b) {
    struck = b;
  }

  @Override
  public boolean getGooddms$lit() {
    return lit;
  }

  @Override
  public void setGooddms$lit(boolean b) {
    lit = b;
  }
}
