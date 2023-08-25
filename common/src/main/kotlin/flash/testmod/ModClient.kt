package flash.testmod

import flash.testmod.ModMain.LOGGER
import flash.testmod.ModMain.MOD_ID
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.entity.player.Player

object ModClient {
    lateinit var implementation: PlatformImplementationClient

    fun init(implementation: PlatformImplementationClient) {
        LOGGER.info("Initializing $MOD_ID client")
        this.implementation = implementation

//        this.implementation.registerBlockEntityRenderer(ModBlockEntities.CUSTOM_BLOCK_ENTITY, ::CustomBlockEntityRenderer)

        registerBlockRenderTypes()
        registerColors()
    }

    private fun registerColors() {
//        this.implementation.registerBlockColors(BlockColor { blockState, blockAndTintGetter, blockPos, index ->
//            return@BlockColor 0xFFFFFF
//        }, ModBlocks.CUSTOM_BLOCK)
//        this.implementation.registerItemColors(ItemColor { itemStack, index ->
//            return@ItemColor 0xE0A33A
//        }, ModItems.CUSTOM_ITEM)
    }

    private fun registerBlockRenderTypes() {
//        this.implementation.registerBlockRenderType(RenderType.cutoutMipped(), ModBlocks.CUSTOM_BLOCK)

//        this.implementation.registerBlockRenderType(
//            RenderType.cutout(),
//            ModBlocks.OTHER_BLOCK,
//            ModBlocks.ANOTHER_BLOCK
//        )
    }

    fun renderOverlay(graphics: GuiGraphics, partialTicks: Float) {
        // overlay.render(graphics, partialTicks)
    }

    @Suppress("UNCHECKED_CAST")
    fun addLayer(skinMap: Map<String, EntityRenderer<out Player>>?) {
        var renderer: LivingEntityRenderer<Player, PlayerModel<Player>>? = skinMap?.get("default") as LivingEntityRenderer<Player, PlayerModel<Player>>
        //renderer?

    }

    fun reloadCodedAssets(resourceManager: ResourceManager) {
        LOGGER.info("Loading assets...")
        //
        LOGGER.info("Loaded assets")
    }
}