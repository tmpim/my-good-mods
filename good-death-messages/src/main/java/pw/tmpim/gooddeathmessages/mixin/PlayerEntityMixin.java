package pw.tmpim.gooddeathmessages.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import pw.tmpim.gooddeathmessages.ExplosionTracker;
import pw.tmpim.gooddeathmessages.Victim;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements Victim {
  @Unique
  private boolean shot = false;

  @Unique
  private boolean pricked = false;

  @Unique
  private boolean struck = false;

  @Unique
  private boolean lit = false;

  @Unique
  private ExplosionTracker.BlastSource blastSource;

  @Override
  public void goodmod_setShot() {
    shot = true;
  }

  @Override
  public void goodmod_resetShot() {
    shot = false;
  }

  @Override
  public boolean goodmod_isShot() {
    return shot;
  }

  @Override
  public void goodmod_setPricked() {
    pricked = true;
  }

  @Override
  public void goodmod_resetPricked() {
    pricked = false;
  }

  @Override
  public boolean goodmod_isPricked() {
    return pricked;
  }

  @Override
  public void goodmod_setBlastSource(ExplosionTracker.BlastSource source) {
    blastSource = source;
  }

  @Override
  public void goodmod_resetBlastSource() {
    blastSource = null;
  }

  @Override
  public ExplosionTracker.BlastSource goodmod_getBlastSource() {
    return blastSource;
  }

  @Override
  public void goodmod_setStruck() {
    struck = true;
  }

  @Override
  public void goodmod_resetStruck() {
    struck = false;
  }

  @Override
  public boolean goodmod_isStruck() {
    return struck;
  }

  @Override
  public void goodmod_setLit() {
    lit = true;
  }

  @Override
  public void goodmod_resetLit() {
    lit = false;
  }

  @Override
  public boolean goodmod_isLit() {
    return lit;
  }
}
