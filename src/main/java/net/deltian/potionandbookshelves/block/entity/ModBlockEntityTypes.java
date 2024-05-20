package net.deltian.potionandbookshelves.block.entity;

import net.deltian.potionandbookshelves.PotionAndBookshelves;
import net.deltian.potionandbookshelves.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, PotionAndBookshelves.MOD_ID);

    public static final RegistryObject<BlockEntityType<PotionShelfBlockEntity>> POTION_SHELF = BLOCK_ENTITIES.register(
            "iron_chest", () -> BlockEntityType.Builder.of(PotionShelfBlockEntity::new, ModBlocks.POTION_SHELF.get()).build(null));

}
