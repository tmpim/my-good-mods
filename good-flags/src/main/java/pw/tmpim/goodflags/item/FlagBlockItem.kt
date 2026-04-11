@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION", "UnstableApiUsage")

package pw.tmpim.goodflags.item

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Block
import net.minecraft.block.WoolBlock
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.texture.TextureManager
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.api.client.StationRenderAPI
import net.modificationstation.stationapi.api.client.model.item.ItemWithRenderer
import net.modificationstation.stationapi.api.client.render.RendererAccess
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import net.modificationstation.stationapi.api.item.StationItemNbt
import org.lwjgl.opengl.GL11
import pw.tmpim.goodflags.GoodFlags
import pw.tmpim.goodflags.block.FlagSpec

/**
 * Custom BlockItem for the flag block that renders a downsampled preview of
 * the flag's painted design on top of the item icon in inventory slots.
 *
 * Uses [net.modificationstation.stationapi.api.client.model.item.ItemWithRenderer] to first render the base baked model (the flag item
 * texture with pole and blank flag area), then composites a downsampled version
 * of the flag's pixel data on top.
 *
 * The flag area on the item texture is at pixel (4, 1) with size 9x6 within
 * the 16x16 item icon. The 48x32 flag canvas is downsampled to fit this space
 * using mode sampling (most common color per region).
 */
class FlagBlockItem(id: Int) : BlockItem(id), ItemWithRenderer {
  @Environment(EnvType.CLIENT)
  override fun renderItemOnGui(
      itemRenderer: ItemRenderer,
      textRenderer: TextRenderer,
      textureManager: TextureManager,
      stack: ItemStack,
      x: Int,
      y: Int
  ) {
    // Sample wool texture from Station's Arsenic sprites
    val atlas = Atlases.getTerrain()
      val woolTextures = (0..<16).map { dyeIndex ->
      val woolBlockMeta = WoolBlock.getBlockMeta(dyeIndex)
      val woolTexture = Block.WOOL.getTexture(0, woolBlockMeta)
      atlas.getTexture(woolTexture).sprite.contents
    }

    // Render the base baked model (flag item texture)
    val renderer = RendererAccess.INSTANCE.renderer.bakedModelRenderer()
    StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture()
    renderer.renderInGuiWithOverrides(stack, x, y)

    // Only render flag preview overlay for painted flags
    val cfg = GoodFlags.config
    if (cfg.itemRendererEnabled != true) return

    val nbt = (stack as? StationItemNbt)?.stationNbt ?: return
    if (!nbt.contains("Pixels")) return

    val pixels = nbt.getByteArray("Pixels")
    if (pixels.size != FlagSpec.FLAG_WIDTH * FlagSpec.FLAG_HEIGHT) return

    // Flag preview area within the item icon — driven by config
    val baseRes     = cfg.itemTextureResolution ?: 16
    val scale       = 16.0 / baseRes // pixel scale factor relative to standard 16×16
    val flagOffsetX = cfg.flagPreviewX ?: 3
    val flagOffsetY = cfg.flagPreviewY ?: 0
    val previewW    = cfg.flagPreviewWidth ?: 12
    val previewH    = cfg.flagPreviewHeight ?: 8

    // Downsample the 48x32 canvas into preview size using mode sampling.
    // Each preview pixel covers a rectangular region of the source canvas.
    // We pick the most common color in each region.
    val previewColors = IntArray(previewW * previewH)

    for (py in 0 until previewH) {
      for (px in 0 until previewW) {
        // Source region bounds (integer division maps preview pixels to source regions)
        val srcX0 = px * FlagSpec.FLAG_WIDTH / previewW
        val srcX1 = (px + 1) * FlagSpec.FLAG_WIDTH / previewW
        val srcY0 = py * FlagSpec.FLAG_HEIGHT / previewH
        val srcY1 = (py + 1) * FlagSpec.FLAG_HEIGHT / previewH

        // Count occurrences of each color index in this region
        val counts = IntArray(16)
        for (sy in srcY0 until srcY1) {
          for (sx in srcX0 until srcX1) {
            val colorIndex = pixels[sy * FlagSpec.FLAG_WIDTH + sx].toInt() and 0xF
            counts[colorIndex]++
          }
        }

        // Find the mode (most common color)
        var bestIndex = 0
        var bestCount = counts[0]
        for (i in 0..<16) {
          if (counts[i] > bestCount) {
            bestCount = counts[i]
            bestIndex = i
          }
        }

        previewColors[py * previewW + px] = bestIndex
      }
    }

    // Draw the preview pixels as colored quads on top of the base texture
    GL11.glDisable(GL11.GL_LIGHTING)
    GL11.glDisable(GL11.GL_DEPTH_TEST)
    GL11.glDisable(GL11.GL_TEXTURE_2D)

    val t = Tessellator.INSTANCE

    for (py in 0 until previewH) {
      for (px in 0 until previewW) {
        val dyeIndex = previewColors[py * previewW + px] and 0xF
        val woolTex = woolTextures[dyeIndex] ?: woolTextures[0] // fallback to white
        val color = woolTex.baseFrame.getColor(px % woolTex.width, py % woolTex.height)

        val b = ((color shr 16) and 0xFF) / 255.0f
        val g = ((color shr 8) and 0xFF) / 255.0f
        val r = (color and 0xFF) / 255.0f

        val x1 = x + (flagOffsetX + px) * scale
        val y1 = y + (flagOffsetY + py) * scale
        val x2 = x1 + scale
        val y2 = y1 + scale

        GL11.glColor4f(r, g, b, 1.0f)
        t.startQuads()
        t.vertex(x1, y2, 0.0)
        t.vertex(x2, y2, 0.0)
        t.vertex(x2, y1, 0.0)
        t.vertex(x1, y1, 0.0)
        t.draw()
      }
    }

    GL11.glEnable(GL11.GL_TEXTURE_2D)
    GL11.glEnable(GL11.GL_DEPTH_TEST)
    GL11.glEnable(GL11.GL_LIGHTING)
    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
  }

  @Environment(EnvType.CLIENT)
  override fun renderItemOnGui(
      itemRenderer: ItemRenderer,
      textRenderer: TextRenderer,
      textureManager: TextureManager,
      itemId: Int,
      damage: Int,
      texture: Int,
      x: Int,
      y: Int
  ) {
    // Fallback for int-based rendering (no ItemStack available, so no NBT).
    // Just render the base baked model.
    val renderer = RendererAccess.INSTANCE.renderer.bakedModelRenderer()
    StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture()
    renderer.renderInGuiWithOverrides(ItemStack(itemId, 1, damage), x, y)
  }
}
