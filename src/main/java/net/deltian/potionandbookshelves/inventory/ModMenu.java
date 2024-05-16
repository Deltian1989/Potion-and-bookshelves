package net.deltian.potionandbookshelves.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

public class ModMenu extends AbstractContainerMenu {

    private final Container container;

    private ModMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory) {
        super(menuType, containerId);

        this.container = new SimpleContainer(12);

        this.container.startOpen(playerInventory.player);
    }

    public static ModMenu createPotionShelfContainer(int containerId, Inventory playerInventory, Container inventory) {
        return new ModMenu(null, containerId, playerInventory);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}