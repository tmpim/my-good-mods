package pw.tmpim.goodutils.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pw.tmpim.goodutils.nbt.NbtCompoundExtensions;

import java.util.Map;

@Mixin(NbtCompound.class)
public class NbtCompoundMixin implements NbtCompoundExtensions {
  @Shadow
  private Map<String, NbtElement> entries;

  @Override
  public void goodutils$removeTag(@NotNull String key) {
    entries.remove(key);
  }
}
