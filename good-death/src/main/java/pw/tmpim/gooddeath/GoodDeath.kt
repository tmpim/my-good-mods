package pw.tmpim.gooddeath

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.gooddeath.block.TombstoneBlock
import pw.tmpim.gooddeath.block.TombstoneBlockEntity

object GoodDeath : ModInitializer {
  const val MOD_ID = "good-death"
  const val MOD_NAME = "Good Death"
  val MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().metadata.version.toString()

  val namespace: Namespace = Namespace.resolve()

  lateinit var tombstoneBlock: Block

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
  val config = GoodDeathConfig()

  override fun onInitialize() {}

  @JvmStatic
  fun spawnTombstone(playerEntity: PlayerEntity) {
    val deathX = playerEntity.x.toInt()
    val deathY = (playerEntity.y - playerEntity.standingEyeHeight + 0.1f).toInt() // thats what beds do
    val deathZ = playerEntity.z.toInt()

    val world = playerEntity.world
    world.setBlock(deathX, deathY, deathZ, tombstoneBlock.id, 1)

    val blockEntity = world.getBlockEntity(deathX, deathY, deathZ)
    if (blockEntity is TombstoneBlockEntity) {
      blockEntity.storePlayerInventory(playerEntity.inventory)
    }
  }

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }

  @EventListener
  fun onRegisterBlocks(event: BlockRegistryEvent) {
    log.info("$MOD_NAME registering blocks")

    tombstoneBlock = TombstoneBlock()
  }

  @EventListener
  fun onRegisterBlockEntities(event: BlockEntityRegisterEvent) {
    event.register(TombstoneBlockEntity::class.java, namespace.id("tombstone").toString())
  }
}
