package pw.tmpim.gooddeathmessages.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.gooddeathmessages.CONFIG_KEY
import pw.tmpim.gooddeathmessages.GoodDeathMessages.MOD_ID
import pw.tmpim.gooddeathmessages.GoodDeathMessages.MOD_NAME

private const val C = CONFIG_KEY
private const val D = "$MOD_ID.death"

class GoodDeathMessagesLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      // Config
      .add("$C.name", MOD_NAME)
      .add("$C.death_messages_enabled", "Enable death messages")
      .add("$C.custom_death_messages_enabled", "Enable non-vanilla death messages")
      .add("$C.custom_death_messages_enabled.desc", "Enables Good Death Messages' own additional death messages. " +
        "Does not affect any other mods adding their own death messages.")

      // Vanilla death messages
      .add("$D.attack.cactus", "%s was pricked to death")
      .add("$D.attack.drown", "%s drowned")
      .add("$D.attack.explosion", "%s blew up")
      .add("$D.attack.explosion.player", "%s was blown up by %s")
      .add("$D.attack.badRespawnPoint.message", "%s was killed by [Intentional Game Design]")
      .add("$D.attack.fall", "%s hit the ground too hard") // Less than 5 blocks
      .add("$D.fell.accident.generic", "%s fell from a high place") // More than 5 blocks
      .add("$D.fell.accident.ladder", "%s fell off a ladder")
      .add("$D.fell.accident.water", "%s belly flopped into a puddle")
      .add("$D.attack.inFire", "%s went up in flames")
      .add("$D.attack.onFire", "%s burned to death")
      .add("$D.attack.lava", "%s tried to swim in lava")
      .add("$D.attack.lightningBolt", "%s was struck by lightning")
      .add("$D.attack.player", "%s was slain by %s")
      .add("$D.attack.mob", "%s was slain by %s")
      .add("$D.attack.arrow", "%s was shot by %s")
      .add("$D.attack.thrown", "%s was pummeled by %s")
      .add("$D.attack.fireball", "%s was fireballed by %s")
      .add("$D.attack.inWall", "%s suffocated in a wall")
      .add("$D.attack.outOfWorld", "%s fell out of the world")
      .add("$D.attack.generic", "%s died")
      .add("$D.attack.genericKill", "%s was killed")

      // Custom death messages
      .add("$D.attack.wolf", "%s was mauled to bits by %s")
      .add("$D.attack.wolf.player", "%s was mauled to bits by %s's %s")
      .add("$D.attack.drownBurn", "%s drowned to a crisp")

      .save("en_US", this, ctx)
  }
}
