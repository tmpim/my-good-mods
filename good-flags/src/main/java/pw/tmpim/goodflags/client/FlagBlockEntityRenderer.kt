package pw.tmpim.goodflags.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Block
import net.minecraft.block.WoolBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.platform.Lighting
import net.modificationstation.stationapi.api.client.StationRenderAPI
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import org.lwjgl.opengl.GL11
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.block.FlagSpec.FLAG_HEIGHT
import pw.tmpim.goodflags.block.FlagSpec.FLAG_WIDTH
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Environment(EnvType.CLIENT)
class FlagBlockEntityRenderer : BlockEntityRenderer() {
  override fun render(entity: BlockEntity, dx: Double, dy: Double, dz: Double, tickDelta: Float) {
    if (entity !is FlagBlockEntity) return

    val meta = entity.world?.getBlockMeta(entity.x, entity.y, entity.z) ?: 0
    // meta: 0=south, 1=west, 2=north, 3=east (player facing direction when placed)
    val rotation = when (meta) {
      0 -> 0.0F    // south
      1 -> 90.0F   // west
      2 -> 180.0F  // north
      3 -> 270.0F  // east
      else -> 0.0F
    }

    // Get the light level at the flag (+2 y)
    val flagLight = dispatcher.world.getNaturalBrightness(entity.x, entity.y + 2, entity.z, 0)

    Lighting.turnOff()
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glDisable(GL11.GL_CULL_FACE)
    if (Minecraft.isAmbientOcclusionEnabled()) {
      GL11.glShadeModel(GL11.GL_SMOOTH)
    } else {
      GL11.glShadeModel(GL11.GL_FLAT)
    }

    GL11.glPushMatrix()
    GL11.glTranslated(dx + 0.5, dy, dz + 0.5)
    GL11.glRotatef(-rotation, 0.0F, 1.0F, 0.0F)

    // Draw the pole
    drawPole()

    // Draw the flag
    drawFlag(entity, flagLight)

    GL11.glPopMatrix()

    Lighting.turnOn()
  }

  private fun drawPole() {
    // Pole dimensions: thin column from y=0 to y=3
    val poleRadius = 0.0625 // 1/16 of a block
    val poleHeight = 3.0

    val gameAtlas = StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE)
    gameAtlas.bindTexture()

    val t = Tessellator.INSTANCE
    t.startQuads()

    val atlas = Atlases.getTerrain()
    val woodBlockSideId = Block.LOG.getTexture(2)
    val woodBlockSideSprite = atlas.getTexture(woodBlockSideId)

    val uSize = woodBlockSideSprite.endU - woodBlockSideSprite.startU
    val vSize = woodBlockSideSprite.endV - woodBlockSideSprite.startV

    val uSideStart = woodBlockSideSprite.startU
    val uSideEnd   = woodBlockSideSprite.startU + uSize / 8
    val vSideStart = woodBlockSideSprite.startV
    val vSideEnd   = woodBlockSideSprite.endV

    val uEndsStart = woodBlockSideSprite.startU
    val uEndsEnd   = woodBlockSideSprite.startU + uSize / 8
    val vEndsStart = woodBlockSideSprite.startV
    val vEndsEnd   = woodBlockSideSprite.startV + vSize / 8

    // Side faces: 3 quads per face, each covering 1 block segment
    for (i in 0 until 3) {
      val yBottom = i.toDouble()
      val yTop    = yBottom + 1.0

      // Front face (positive Z)
      t.vertex(-poleRadius, yTop,    poleRadius, uSideStart, vSideStart)
      t.vertex(-poleRadius, yBottom, poleRadius, uSideStart, vSideEnd)
      t.vertex( poleRadius, yBottom, poleRadius, uSideEnd,   vSideEnd)
      t.vertex( poleRadius, yTop,    poleRadius, uSideEnd,   vSideStart)

      // Back face (negative Z)
      t.vertex( poleRadius, yTop,    -poleRadius, uSideStart + uSize / 8, vSideStart)
      t.vertex( poleRadius, yBottom, -poleRadius, uSideStart + uSize / 8, vSideEnd)
      t.vertex(-poleRadius, yBottom, -poleRadius, uSideEnd + uSize / 8,   vSideEnd)
      t.vertex(-poleRadius, yTop,    -poleRadius, uSideEnd + uSize / 8,   vSideStart)

      // Left face (negative X)
      t.vertex(-poleRadius, yTop,    -poleRadius, uSideStart + 2*uSize / 8, vSideStart)
      t.vertex(-poleRadius, yBottom, -poleRadius, uSideStart + 2*uSize / 8, vSideEnd)
      t.vertex(-poleRadius, yBottom,  poleRadius, uSideEnd + 2*uSize / 8,   vSideEnd)
      t.vertex(-poleRadius, yTop,     poleRadius, uSideEnd + 2*uSize / 8,   vSideStart)

      // Right face (positive X)
      t.vertex(poleRadius, yTop,     poleRadius, uSideStart + 3*uSize / 8, vSideStart)
      t.vertex(poleRadius, yBottom,  poleRadius, uSideStart + 3*uSize / 8, vSideEnd)
      t.vertex(poleRadius, yBottom, -poleRadius, uSideEnd + 3*uSize / 8,   vSideEnd)
      t.vertex(poleRadius, yTop,    -poleRadius, uSideEnd + 3*uSize / 8,   vSideStart)
    }

    // Top face
    t.vertex(-poleRadius, poleHeight, -poleRadius, uEndsStart, vEndsStart)
    t.vertex(-poleRadius, poleHeight,  poleRadius, uEndsStart, vEndsEnd)
    t.vertex( poleRadius, poleHeight,  poleRadius, uEndsEnd,   vEndsEnd)
    t.vertex( poleRadius, poleHeight, -poleRadius, uEndsEnd,   vEndsStart)

    t.draw()
  }

  private fun drawFlag(entity: FlagBlockEntity, light: Float) {
    // Flag extends from the pole to the right, at the top of the pole (3:2 ratio)
    val flagWidth  = 1.5 // 1.5 blocks wide
    val flagHeight = 1.0 // 1 block tall
    val flagTop    = 3.0 // Top of the flag (at top of pole)
    val flagBottom = flagTop - flagHeight
    val flagRight  = -0.0625 // Start just past the pole
    val flagLeft   = flagRight - flagWidth

    // Thickness: 1 pixel (1/32 block), centred on the pole's Z plane
    val flagThickness = 0.0625 / 2
    val flagZFront    = flagThickness / 2.0
    val flagZBack     = -flagThickness / 2.0

    // UV edge fractions for the edge strips
    val uMin = 0.0
    val uMax = 1.0
    val vMin = 0.0
    val vMax = 1.0
    val vTopRow    = 1.0 / FLAG_HEIGHT       // one texel row from top
    val vBottomRow = 1.0 - 1.0 / FLAG_HEIGHT // one texel row from bottom
    val uRightCol  = 1.0 - 1.0 / FLAG_WIDTH  // one texel column from right

    // Get or create the GL texture
    val textureId = getOrCreateTexture(entity)
    GL11.glEnable(GL11.GL_TEXTURE_2D)
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

    GL11.glColor4f(light, light, light, 1.0F)

    val t = Tessellator.INSTANCE
    t.startQuads()

    // Front face of the flag (positive Z)
    t.vertex(flagLeft,  flagTop,    flagZFront, uMax, vMin)
    t.vertex(flagLeft,  flagBottom, flagZFront, uMax, vMax)
    t.vertex(flagRight, flagBottom, flagZFront, uMin, vMax)
    t.vertex(flagRight, flagTop,    flagZFront, uMin, vMin)

    // Back face of the flag (negative Z, reversed winding, mirrored U)
    t.vertex(flagRight, flagTop,    flagZBack, uMin, vMin)
    t.vertex(flagRight, flagBottom, flagZBack, uMin, vMax)
    t.vertex(flagLeft,  flagBottom, flagZBack, uMax, vMax)
    t.vertex(flagLeft,  flagTop,    flagZBack, uMax, vMin)

    // Top edge – uses the top row of the texture (vMin..vTopRow)
    t.vertex(flagLeft,  flagTop, flagZBack,  uMax, vMin)
    t.vertex(flagLeft,  flagTop, flagZFront, uMax, vTopRow)
    t.vertex(flagRight, flagTop, flagZFront, uMin, vTopRow)
    t.vertex(flagRight, flagTop, flagZBack,  uMin, vMin)

    // Bottom edge – uses the bottom row of the texture (vBottomRow..vMax)
    t.vertex(flagLeft,  flagBottom, flagZFront, uMax, vBottomRow)
    t.vertex(flagLeft,  flagBottom, flagZBack,  uMax, vMax)
    t.vertex(flagRight, flagBottom, flagZBack,  uMin, vMax)
    t.vertex(flagRight, flagBottom, flagZFront, uMin, vBottomRow)

    // Right (free) edge – uses the rightmost column (uMax..uRightCol)
    t.vertex(flagLeft, flagTop,    flagZFront, uMax,      vMin)
    t.vertex(flagLeft, flagBottom, flagZFront, uMax,      vMax)
    t.vertex(flagLeft, flagBottom, flagZBack,  uRightCol, vMax)
    t.vertex(flagLeft, flagTop,    flagZBack,  uRightCol, vMin)

    t.draw()
  }

  private fun getOrCreateTexture(entity: FlagBlockEntity): Int {
    val key = System.identityHashCode(entity)

    if (!entity.dirty && textureCache.containsKey(key)) {
      return textureCache[key]!!
    }

    // Delete old texture if it exists
    textureCache[key]?.let { oldId ->
      GL11.glDeleteTextures(oldId)
    }

    // Generate a new texture from pixel data
    val texId = GL11.glGenTextures()
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId)

    // Create pixel buffer
    val buffer = ByteBuffer.allocateDirect(FLAG_WIDTH * FLAG_HEIGHT * 4)
      .order(ByteOrder.nativeOrder())

    // Sample wool texture from Station's Arsenic sprites
    val atlas = Atlases.getTerrain()
    val woolTextures = (0..<16).map { dyeIndex ->
      val woolBlockMeta = WoolBlock.getBlockMeta(dyeIndex)
      val woolTexture = Block.WOOL.getTexture(0, woolBlockMeta)
      atlas.getTexture(woolTexture).sprite.contents
    }

    @Suppress("UnstableApiUsage")
    for (y in 0 until FLAG_HEIGHT) {
      for (x in 0 until FLAG_WIDTH) {
        val colorIndex = entity.getPixel(x, y)
        val dyeIndex = colorIndex and 0xF
        val woolTex = woolTextures[dyeIndex] ?: woolTextures[0] // fallback to white

        // Map flag pixel to wool tile via modulo (tile repeats across the 48×32 flag canvas)
        // TODO: scale the canvas according to the resolution of the resource pack (based on the biggest wool tex?)
        buffer.putInt(woolTex.baseFrame.getColor(x % woolTex.width, y % woolTex.height))
      }
    }
    buffer.flip()

    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP)
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP)
    GL11.glTexImage2D(
      GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
      FLAG_WIDTH, FLAG_HEIGHT, 0,
      GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer
    )

    textureCache[key] = texId
    entity.dirty = false

    return texId
  }

  companion object {
    /** Cache of GL texture IDs keyed by block entity identity hash. */
    private val textureCache = HashMap<Int, Int>()

    fun clearTextureCache() {
      textureCache.clear()
    }
  }
}
