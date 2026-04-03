package pw.tmpim.goodsounds.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.tmpim.goodsounds.GoodSounds;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
  @WrapOperation(
    method = "renderRain",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/World;playSound(DDDLjava/lang/String;FF)V"
    )
  )
  private void playRainSoundQuietly(World world, double x, double y, double z, String sound, float volume, float pitch, Operation<Void> original) {
    var config = GoodSounds.getConfig();
    float amplitude = config.rainVolume == null ? GoodSounds.DEFAULT_RAIN_VOLUME : config.rainVolume;
    original.call(world, x, y, z, sound, amplitude * volume, pitch);
  }
}
