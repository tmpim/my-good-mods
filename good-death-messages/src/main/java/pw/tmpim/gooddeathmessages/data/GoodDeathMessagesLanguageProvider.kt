package pw.tmpim.gooddeathmessages.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.gooddeathmessages.data.GoodDeathMessagesData.namespace

class GoodDeathMessagesLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  private val d = "$namespace.death"

  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$d.attack.cactus", "%s was pricked to death")
      .add("$d.attack.drown", "%s drowned")
      .add("$d.attack.explosion", "%s blew up")
      .add("$d.attack.explosion.player", "%s was blown up by %s")
      .add("$d.attack.badRespawnPoint.message", "%s was killed by [Intentional Game Design]")
      .add("$d.attack.fall", "%s hit the ground too hard") // Less than 5 blocks
      .add("$d.fell.accident.generic", "%s fell from a high place") // More than 5 blocks
      .add("$d.fell.accident.ladder", "%s fell off a ladder")
      .add("$d.fell.accident.water", "%s belly flopped into a puddle")
      .add("$d.attack.inFire", "%s went up in flames")
      .add("$d.attack.onFire", "%s burned to death")
      .add("$d.attack.lava", "%s tried to swim in lava")
      .add("$d.attack.lightningBolt", "%s was struck by lightning")
      .add("$d.attack.player", "%s was slain by %s")
      .add("$d.attack.mob", "%s was slain by %s")
      .add("$d.attack.arrow", "%s was shot by %s")
      .add("$d.attack.thrown", "%s was pummeled by %s")
      .add("$d.attack.fireball", "%s was fireballed by %s")
      .add("$d.attack.inWall", "%s suffocated in a wall")
      .add("$d.attack.outOfWorld", "%s fell out of the world")
      .add("$d.attack.generic", "%s died")
      .add("$d.attack.genericKill", "%s was killed")
      // Custom
      .add("$d.attack.wolf", "%s was mauled to bits by %s")
      .add("$d.attack.wolf.player", "%s was mauled to bits by %s's %s")
      .add("$d.attack.drownBurn", "%s drowned to a crisp")
      .save("en_US", this, ctx)
  }
}
