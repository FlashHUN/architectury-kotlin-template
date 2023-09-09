package flash.testmod.registries

import flash.testmod.platform.PlatformRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.Block

object ModItems : PlatformRegistry<Registry<Item>, ResourceKey<Registry<Item>>, Item>() {
    override val registry: Registry<Item> = BuiltInRegistries.ITEM
    override val registryKey: ResourceKey<Registry<Item>> = Registries.ITEM

    @JvmField val TEST_ITEM = item("test_item", Properties()
        .stacksTo(1)
        .rarity(Rarity.EPIC)
    )

    private fun item(name: String, item: Item) : Item = create(name, item)
    private fun item(name: String, properties: Properties = Properties()) : Item = create(name, Item(properties))
    private fun blockItem(name: String, block: Block, properties: Properties = Properties()) : Item = create(name, BlockItem(block, properties))
}