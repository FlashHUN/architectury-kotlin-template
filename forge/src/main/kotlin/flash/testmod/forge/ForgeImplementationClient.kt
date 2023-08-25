package flash.testmod.forge

import flash.testmod.ModClient
import flash.testmod.PlatformImplementationClient
import flash.testmod.registries.ModCreativeTabs
import flash.testmod.registries.ModEntities
import flash.testmod.registries.ModKeybinds
import flash.testmod.registries.ModParticles
import net.minecraft.client.Minecraft
import net.minecraft.client.color.block.BlockColor
import net.minecraft.client.color.item.ItemColor
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ReloadableResourceManager
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.util.function.Supplier

object ForgeImplementationClient : PlatformImplementationClient {

    fun init() {
        with(MOD_BUS) {
            addListener(::onClientSetup)
            addListener(::onKeyMappingRegister)
            addListener(::onRegisterParticleProviders)
            addListener(::onBuildCreativeTabs)
        }
        MinecraftForge.EVENT_BUS.addListener(this::onRenderGuiOverlayEvent)
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        (Minecraft.getInstance().resourceManager as ReloadableResourceManager)
            .registerReloadListener(object : ResourceManagerReloadListener {
                override fun onResourceManagerReload(resourceManager: ResourceManager) {
                    ModClient.reloadCodedAssets(resourceManager)
                }
            })
        ModClient.reloadCodedAssets(Minecraft.getInstance().resourceManager)
        event.enqueueWork {
            ModClient.init(this)
//            EntityRenderers.register(ModEntities.CUSTOM_ENTITY) { ModClient.registerCustomEntityRenderer(it) }
        }
        ForgeClientEventHandler.register()
    }

    @Suppress("UnstableApiUsage")
    override fun registerLayer(modelLayer: ModelLayerLocation, supplier: Supplier<LayerDefinition>) {
        ForgeHooksClient.registerLayerDefinition(modelLayer, supplier)
    }

    override fun <T : ParticleOptions> registerParticleFactory(
        type: ParticleType<T>,
        factory: (SpriteSet) -> ParticleProvider<T>
    ) {
        throw UnsupportedOperationException("Forge can't store these early, use ForgeImplementationClient#onRegisterParticleProviders")
    }

    @Suppress("DEPRECATION")
    override fun registerBlockRenderType(layer: RenderType, vararg blocks: Block) {
        blocks.forEach { block ->
            ItemBlockRenderTypes.setRenderLayer(block, layer)
        }
    }

    @Suppress("DEPRECATION")
    override fun registerItemColors(provider: ItemColor, vararg items: Item) {
        Minecraft.getInstance().itemColors.register(provider, *items)
    }

    @Suppress("DEPRECATION")
    override fun registerBlockColors(provider: BlockColor, vararg blocks: Block) {
        Minecraft.getInstance().blockColors.register(provider, *blocks)
    }

    override fun <T : BlockEntity> registerBlockEntityRenderer(
        type: BlockEntityType<T>,
        factory: BlockEntityRendererProvider<T>
    ) {
        BlockEntityRenderers.register(type, factory)
    }

    private fun onKeyMappingRegister(event: RegisterKeyMappingsEvent) {
        ModKeybinds.register(event::register)
    }

    private fun onRegisterParticleProviders(event: RegisterParticleProvidersEvent) {
//        event.registerSpriteSet(ModParticles.CUSTOM_PARTICLE_TYPE, CustomParticleType::Factory)
    }

    private fun onRenderGuiOverlayEvent(event: RenderGuiOverlayEvent.Pre) {
        if (event.overlay.id == VanillaGuiOverlay.CHAT_PANEL.id()) {
            ModClient.renderOverlay(event.guiGraphics, event.partialTick)
        }
    }

    internal fun registerResourceReloader(reloader: PreparableReloadListener) {
        (Minecraft.getInstance().resourceManager as ReloadableResourceManager).registerReloadListener(reloader)
    }

    private fun onBuildCreativeTabs(e: BuildCreativeModeTabContentsEvent) {
        ModCreativeTabs.inject { injector ->
            if (e.tabKey == injector.key) {
                injector.entryInjector(e.parameters).forEach(e::accept)
            }
        }
    }
}