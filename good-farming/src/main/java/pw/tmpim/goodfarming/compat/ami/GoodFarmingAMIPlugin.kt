package pw.tmpim.goodfarming.compat.ami

import net.glasslauncher.mods.alwaysmoreitems.api.*
import net.minecraft.nbt.NbtCompound
import pw.tmpim.goodfarming.GoodFarming.MOD_ID
import pw.tmpim.goodfarming.GoodFarming.MOD_NAME
import pw.tmpim.goodfarming.GoodFarming.namespace

object GoodFarmingAMIPlugin : ModPluginProvider {
  lateinit var amiHelpers: AMIHelpers
  lateinit var itemRegistry: ItemRegistry

  override fun getName() = MOD_NAME
  override fun getId() = namespace.id(MOD_ID)

  override fun onAMIHelpersAvailable(amiHelpers: AMIHelpers) {
    GoodFarmingAMIPlugin.amiHelpers = amiHelpers
  }

  override fun onItemRegistryAvailable(itemRegistry: ItemRegistry) {
    GoodFarmingAMIPlugin.itemRegistry = itemRegistry
  }

  override fun register(registry: ModRegistry) {}
  override fun onRecipeRegistryAvailable(recipeRegistry: RecipeRegistry) {}
  override fun deserializeRecipe(recipe: NbtCompound): SyncableRecipe? = null
  override fun updateBlacklist(amiHelpers: AMIHelpers) {}
}
