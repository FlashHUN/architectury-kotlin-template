package flash.testmod.registries

import flash.testmod.util.resource
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object ModCreativeTabs {

    private val ALL = arrayListOf<CreativeModeTabHolder>()
    private val INJECTORS = arrayListOf<CreativeModeTabInjector>()

    @JvmStatic val GENERIC_KEY = this.create("generic", this::genericEntries) { ItemStack(ModItems.TEST_ITEM) }

    @JvmStatic val AGRICULTURE get() = BuiltInRegistries.CREATIVE_MODE_TAB.get(GENERIC_KEY)

//    @JvmStatic val BUILDING_BLOCKS_INJECTIONS = this.inject(ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), CreativeModeTabs.BUILDING_BLOCKS.location()), this::buildingBlocksInjections)

    private fun genericEntries(displayContext: ItemDisplayParameters, entries: CreativeModeTab.Output) {
        entries.accept(ModItems.TEST_ITEM)
    }

//    private fun buildingBlocksInjections(displayContext: ItemDisplayParameters): List<Item> = arrayListOf(
//        ModItems.TEST_ITEM
//    )

    fun register(consumer: (holder: CreativeModeTabHolder) -> CreativeModeTab) {
        ALL.forEach(consumer::invoke)
    }

    fun inject(consumer: (injector: CreativeModeTabInjector) -> Unit) {
        INJECTORS.forEach(consumer)
    }

    data class CreativeModeTabHolder(
        val key: ResourceKey<CreativeModeTab>,
        val displayIconProvider: () -> ItemStack,
        val entryCollector: CreativeModeTab.DisplayItemsGenerator,
        val displayName: Component = Component.translatable("itemGroup.${key.location().namespace}.${key.location().path}")
    )

    data class CreativeModeTabInjector(
        val key: ResourceKey<CreativeModeTab>,
        val entryInjector: (displayContext: CreativeModeTab.ItemDisplayParameters) -> List<Item>,
    )

    private fun create(name: String, entryCollector: CreativeModeTab.DisplayItemsGenerator, displayIconProvider: () -> ItemStack): ResourceKey<CreativeModeTab> {
        val key = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), resource(name))
        this.ALL += CreativeModeTabHolder(key, displayIconProvider, entryCollector)
        return key
    }

    private fun inject(key: ResourceKey<CreativeModeTab>, entryInjector: (displayContext: CreativeModeTab.ItemDisplayParameters) -> List<Item>): CreativeModeTabInjector {
        val injector = CreativeModeTabInjector(key, entryInjector)
        this.INJECTORS += injector
        return injector
    }
}