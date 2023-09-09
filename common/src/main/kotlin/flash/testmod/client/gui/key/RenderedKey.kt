package flash.testmod.client.gui.key

import com.mojang.blaze3d.platform.InputConstants
import flash.testmod.util.resource
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import kotlin.math.max

class RenderedKey(key: Int) {
    companion object {
        val TEXTURE = resource("textures/gui/keys.png")
        private val CACHE : HashMap<Int, KeyRenderer> = hashMapOf()
        private val REPLACEMENTS : Map<Int, KeyRenderer> = hashMapOf(
            InputConstants.KEY_ESCAPE to KeyRenderer.Key("Esc"),
            InputConstants.KEY_CAPSLOCK to KeyRenderer.Key("CAPS"),
            InputConstants.KEY_PRINTSCREEN to KeyRenderer.Key("PrtSc"),
            InputConstants.KEY_SCROLLLOCK to KeyRenderer.Key("ScrLk"),
            InputConstants.KEY_PAUSE to KeyRenderer.Key("Pause"),
            InputConstants.KEY_PAGEUP to KeyRenderer.Key("PgUp"),
            InputConstants.KEY_PAGEDOWN to KeyRenderer.Key("PgDn"),
            InputConstants.KEY_INSERT to KeyRenderer.Key("Ins"),
            InputConstants.KEY_HOME to KeyRenderer.Key("Home"),
            InputConstants.KEY_DELETE to KeyRenderer.Key("Del"),
            InputConstants.KEY_END to KeyRenderer.Key("End"),
            InputConstants.KEY_LCONTROL to KeyRenderer.Key("Ctrl", KeyRenderPosition.LEFT),
            InputConstants.KEY_RCONTROL to KeyRenderer.Key("Ctrl", KeyRenderPosition.RIGHT),
            InputConstants.KEY_LALT to KeyRenderer.Key("Alt", KeyRenderPosition.LEFT),
            InputConstants.KEY_RALT to KeyRenderer.Key("Alt", KeyRenderPosition.RIGHT),
            InputConstants.KEY_LSHIFT to KeyRenderer.Shift(KeyRenderPosition.LEFT),
            InputConstants.KEY_RSHIFT to KeyRenderer.Shift(KeyRenderPosition.RIGHT),
            InputConstants.KEY_BACKSPACE to KeyRenderer.Icon(71, 8, 12, 4, 36, KeyRenderPosition.RIGHT),
            InputConstants.KEY_TAB to KeyRenderer.Icon(77, 0, 9, 8, 32),
            InputConstants.KEY_LEFT to KeyRenderer.Icon(86, 8, 8, 4),
            InputConstants.KEY_RIGHT to KeyRenderer.Icon(86, 4, 8, 4),
            InputConstants.KEY_UP to KeyRenderer.Icon(98, 4, 4, 8),
            InputConstants.KEY_DOWN to KeyRenderer.Icon(94, 4, 4, 8),
            InputConstants.KEY_SPACE to KeyRenderer.Space()
        )

        private fun keyRenderer(key: Int) : KeyRenderer {
            return REPLACEMENTS[key] ?: CACHE[key] ?: run {
                val renderer = KeyRenderer.Key(InputConstants.getKey(key, 0).displayName.string.uppercase())
                CACHE[key] = renderer
                renderer
            }
        }
    }

    private var key: Int
    private var renderer: KeyRenderer

    init {
        this.key = key
        renderer = keyRenderer(key)
    }

    fun setKey(key: Int) {
        renderer = keyRenderer(key)
    }

    fun render(graphics: GuiGraphics, x: Int, y: Int, isPressed: Boolean) {
        renderer.render(graphics, x, y, isPressed)
    }

    class Mapping(private val mapping: KeyMapping) {
        fun render(graphics: GuiGraphics, x: Int, y: Int) {
            keyRenderer(mapping.key.value).render(graphics, x, y, mapping.isDown)
        }
    }

    private abstract class KeyRenderer(protected val w: Int = 24, private val position: KeyRenderPosition = KeyRenderPosition.CENTER) {
        abstract fun render(graphics: GuiGraphics, x: Int, y: Int, isPressed: Boolean)

        fun drawBackground(graphics: GuiGraphics, x: Int, y: Int, isHovered: Boolean = false) {
            val hoveredOffset = if (isHovered) 48 else 0
            if (w == 24) {
                graphics.blit(TEXTURE, x, y, 0, hoveredOffset, w, 24)
            } else {
                graphics.blitNineSliced(TEXTURE, x, y, w, 24, 11, 1, 11, 9, 28, 24, 0, 24 + hoveredOffset)
            }
        }

        fun xPos(x: Int, w: Int) : Int = when(position) {
            KeyRenderPosition.CENTER -> x + this.w / 2 - w / 2
            KeyRenderPosition.LEFT -> x + 6
            KeyRenderPosition.RIGHT -> x + this.w - 6 - w
        }

        class Key(private val keyName: String, position: KeyRenderPosition = KeyRenderPosition.CENTER, w: Int = run {
            val font = Minecraft.getInstance().font
            val keyNameWidth = font.width(keyName)
            if (keyNameWidth <= 12) 24 else max(keyNameWidth + 16, 24)
        }) : KeyRenderer(w, position) {
            override fun render(graphics: GuiGraphics, x: Int, y: Int, isPressed: Boolean) {
                drawBackground(graphics, x, y, isPressed)
                val font = Minecraft.getInstance().font
                val keyNameWidth = font.width(keyName)
                graphics.drawString(font, keyName, xPos(x, keyNameWidth), y + 4 + if (isPressed) 2 else 0, 0xFFFFFF)
            }
        }

        open class Icon(val u: Int, val v: Int, val uW: Int, val vH: Int, w: Int = 24, position: KeyRenderPosition = KeyRenderPosition.CENTER) : KeyRenderer(w, position) {
            override fun render(graphics: GuiGraphics, x: Int, y: Int, isPressed: Boolean) {
                drawBackground(graphics, x, y, isPressed)
                graphics.blit(TEXTURE, xPos(x, uW), y + 8 - vH / 2 + if (isPressed) 2 else 0, u, v, uW, vH)
            }
        }

        class Shift(position: KeyRenderPosition) : Icon(71, 0, 6, 6, 28, position)

        class Space : KeyRenderer(47) {
            override fun render(graphics: GuiGraphics, x: Int, y: Int, isPressed: Boolean) {
                graphics.blit(TEXTURE, x, y, 24, if (isPressed) 48 else 0, w, 24)
                graphics.blit(TEXTURE, x + w / 2 - 9 / 2, y + if (isPressed) 8 else 6, 86, 0, 9, 4)
            }
        }
    }

}
