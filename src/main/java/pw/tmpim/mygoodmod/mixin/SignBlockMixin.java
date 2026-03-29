package pw.tmpim.mygoodmod.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SignBlock.class)
public abstract class SignBlockMixin extends Block {
  public SignBlockMixin(int id, Material material) {
    super(id, material);
  }

  @Override
  public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
    player.openEditSignScreen((SignBlockEntity) world.getBlockEntity(x, y, z));
    return true;
  }
}
