package net.deltian.potionandbookshelves.inventory;

import net.deltian.potionandbookshelves.PotionAndBookshelves;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainerTypes {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, PotionAndBookshelves.MOD_ID);

    public static final RegistryObject<MenuType<ModMenu>> POTION_SHELF = CONTAINERS.register("potion_shelf", () -> new MenuType<>(ModMenu::createPotionShelfContainer));
}
