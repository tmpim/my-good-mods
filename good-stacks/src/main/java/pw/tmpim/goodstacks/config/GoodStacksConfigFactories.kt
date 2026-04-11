package pw.tmpim.goodstacks.config

import com.google.common.collect.ImmutableMap
import net.glasslauncher.mods.gcapi3.api.ConfigFactoryProvider
import pw.tmpim.goodutils.gcapi.GcapiBuilder
import pw.tmpim.goodutils.gcapi.putEnum
import java.lang.reflect.Type
import java.util.function.Function

object GoodStacksConfigFactories : ConfigFactoryProvider {
  override fun provideLoadFactories(builder: GcapiBuilder) {
    builder.putEnum(ItemCountTooltip::class.java)
  }

  override fun provideSaveFactories(builder: ImmutableMap.Builder<Type, Function<in Any, in Any>>) {
    builder.put(ItemCountTooltip::class.java) { it }
  }
}
