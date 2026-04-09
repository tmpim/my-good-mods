package pw.tmpim.goodflags.mixin;

import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.CraftingRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodflags.recipe.FlagCraftingRecipe;

import java.util.List;

@Mixin(CraftingRecipeManager.class)
public class CraftingRecipeManagerMixin {
  @Shadow
  private List<CraftingRecipe> recipes;

  @Inject(
    method = "<init>",
    at = @At(
      value = "INVOKE",
      target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"
    )
  )
  public void addFlagRecipes(CallbackInfo ci) {
    recipes.add(FlagCraftingRecipe.INSTANCE);
  }
}
