package pw.tmpim.goodconfig.api

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.fabricmc.loader.api.ModContainer
import net.minecraft.nbt.NbtCompound
import net.modificationstation.stationapi.api.nbt.NbtOps
import java.nio.file.Path

open class ConfigSpec : BaseContainerSpec() {
  open val filename: String? = null

  lateinit var file: Path
  lateinit var mod: ModContainer

  var holder: ConfigHolder<*>? = null
  var role: SpecRole = SpecRole.LOCAL

  val local
    get() = holder?.local ?: this
  val remote
    get() = holder?.remote

  fun encodeToNbt(): DataResult<NbtCompound> =
    codec.encodeStart(NbtOps.INSTANCE, Unit).map { it as NbtCompound }

  fun decodeFromNbt(tag: NbtCompound): DataResult<Unit> =
    codec.parse(NbtOps.INSTANCE, tag)

  fun <O> encodeWith(ops: DynamicOps<O>): DataResult<O> =
    codec.encode(Unit, ops, ops.emptyMap())

  fun <O> decodeWith(ops: DynamicOps<O>, input: O): DataResult<Unit> =
    codec.parse(ops, input)
}

/** assigns the config a holder. it will then be loaded from disk in prelaunch (after good-config entrypoint) */
inline fun <reified S : ConfigSpec> S.register(
  noinline factory: () -> S = { S::class.java.getDeclaredConstructor().newInstance() }
) = apply {
  holder = ConfigHolder(this, factory)
}
