package pw.tmpim.gooddeathmessages.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.gooddeathmessages.data.GoodDeathMessagesData.namespace

class GoodDeathMessagesLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("${namespace}.death.attack.cactus", "%s was pricked to death")
      .add("${namespace}.death.attack.drown", "%s drowned")
      .add("${namespace}.death.attack.explosion", "%s blew up")
      .add("${namespace}.death.attack.explosion.player", "%s was blown up by %s")
      .add("${namespace}.death.attack.badRespawnPoint.message", "%s was killed by [Intentional Game Design]")
      .add("${namespace}.death.attack.fall", "%s hit the ground too hard") // Less than 5 blocks
      .add("${namespace}.death.fell.accident.generic", "%s fell from a high place") // More than 5 blocks
      .add("${namespace}.death.fell.accident.ladder", "%s fell off a ladder")
      .add("${namespace}.death.fell.accident.water", "%s belly flopped into a puddle")
      .add("${namespace}.death.attack.inFire", "%s went up in flames")
      .add("${namespace}.death.attack.onFire", "%s burned to death")
      .add("${namespace}.death.attack.lava", "%s tried to swim in lava")
      .add("${namespace}.death.attack.lightningBolt", "%s was struck by lightning")
      .add("${namespace}.death.attack.player", "%s was slain by %s")
      .add("${namespace}.death.attack.mob", "%s was slain by %s")
      .add("${namespace}.death.attack.arrow", "%s was shot by %s")
      .add("${namespace}.death.attack.thrown", "%s was pummeled by %s")
      .add("${namespace}.death.attack.fireball", "%s was fireballed by %s")
      .add("${namespace}.death.attack.inWall", "%s suffocated in a wall")
      .add("${namespace}.death.attack.outOfWorld", "%s fell out of the world")
      .add("${namespace}.death.attack.generic", "%s died")
      .add("${namespace}.death.attack.genericKill", "%s was killed")
    // Custom
      .add("${namespace}.death.attack.wolf", "%s was mauled to bits by %s")
      .add("${namespace}.death.attack.wolf.player", "%s was mauled to bits by %s's %s")
      .add("${namespace}.death.attack.drownBurn", "%s drowned to a crisp")
      .save("en_US", this, ctx)
  }
}
