package net.deltian.potionandbookshelves.block;

import net.deltian.potionandbookshelves.PotionAndBookshelves;
import net.deltian.potionandbookshelves.block.entity.ModBlockEntityTypes;
import net.deltian.potionandbookshelves.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PotionAndBookshelves.MOD_ID);

    public static final RegistryObject<Block> POTION_SHELF = registerBlock("potion_shelf", () ->
            new PotionShelfBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion().isViewBlocking(ModBlocks::never), ModBlockEntityTypes.IRON_CHEST::get), CreativeModeTab.TAB_DECORATIONS);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab creativeModeTab){
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn,creativeModeTab);

        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab creativeModeTab){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(creativeModeTab)));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

    private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
        return false;
    }
}
