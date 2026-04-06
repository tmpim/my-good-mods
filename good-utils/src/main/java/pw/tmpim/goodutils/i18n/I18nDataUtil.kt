package pw.tmpim.goodutils.i18n

import emmathemartian.datagen.builder.LangBuilder
import net.minecraft.item.Item

// kept in a separate file as datagen may not be in the classpath

fun LangBuilder.sub(item: Item, key: String, value: String) =
  add(getItemSubKey(item, key), value)
