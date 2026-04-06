package pw.tmpim.goodflags.compat.ami

import net.glasslauncher.mods.alwaysmoreitems.api.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import pw.tmpim.goodflags.GoodFlags
import pw.tmpim.goodflags.GoodFlags.MOD_ID
import pw.tmpim.goodflags.GoodFlags.MOD_NAME
import pw.tmpim.goodflags.GoodFlags.namespace

object GoodFlagsAMIPlugin : ModPluginProvider {
  lateinit var amiHelpers: AMIHelpers
  lateinit var itemRegistry: ItemRegistry

  override fun getName() = MOD_NAME
  override fun getId() = namespace.id(MOD_ID)

  override fun onAMIHelpersAvailable(amiHelpers: AMIHelpers) {
    GoodFlagsAMIPlugin.amiHelpers = amiHelpers
  }

  override fun onItemRegistryAvailable(itemRegistry: ItemRegistry) {
    GoodFlagsAMIPlugin.itemRegistry = itemRegistry
  }

  override fun register(registry: ModRegistry) {}
  override fun onRecipeRegistryAvailable(recipeRegistry: RecipeRegistry) {}
  override fun deserializeRecipe(recipe: NbtCompound): SyncableRecipe? = null

  override fun updateBlacklist(amiHelpers: AMIHelpers) {
    amiHelpers.itemBlacklist.addItemToBlacklist(ItemStack(GoodFlags.flagPoleBlock))
  }
}
