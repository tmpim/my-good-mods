package pw.tmpim.mygoodmod

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.modificationstation.stationapi.api.event.entity.player.IsPlayerUsingEffectiveToolEvent
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint
import net.modificationstation.stationapi.api.template.block.TemplateBlock
import net.modificationstation.stationapi.api.util.Namespace
import net.modificationstation.stationapi.api.util.Null
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.mygoodmod.assets.AssetFetcher
import pw.tmpim.mygoodmod.assets.GoodResources
import pw.tmpim.mygoodmod.block.RedstoneBlock

object MyGoodMod : ModInitializer {
  const val MOD_ID: String = "mygoodmod"
  const val MOD_NAME: String = "My good mod"
  val MOD_VERSION: String = FabricLoader.getInstance().getModContainer(MOD_ID).get().metadata.version.toString()

  val namespace: Namespace = Namespace.resolve()

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "gui.$MOD_ID.config.name")
  val config = MyGoodModConfig()

  // blocks (todo: move to another class)
  lateinit var redstoneBlock: Block
  lateinit var stoneBricksBlock: Block

  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }

  @EventListener
  fun onRegisterBlocks(event: BlockRegistryEvent) {
    log.info("$MOD_NAME registering blocks")

    redstoneBlock = RedstoneBlock().setHardness(5.0F).setResistance(6.0F).setSoundGroup(Block.STONE_SOUND_GROUP)
    stoneBricksBlock = TemplateBlock(namespace.id("stone_bricks"), Material.STONE).setHardness(1.5F)
      .setResistance(6.0F).setSoundGroup(Block.STONE_SOUND_GROUP).setTranslationKey(namespace, "stone_bricks")
  }
}
