package net.deltian.potionandbookshelves.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

import javax.annotation.Nullable;

public class ModMenu extends AbstractContainerMenu {

    private final Container container;

    private ModMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory) {
        this(menuType, containerId, playerInventory, new SimpleContainer(12));
    }

    protected ModMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, Container container){
        super(menuType, containerId);

        checkContainerSize(container, 12);

        this.container = container;

        this.container.startOpen(playerInventory.player);

        for(int j = 0; j < 3; ++j) {
            for(int k = 0; k < 4; ++k) {
                this.addSlot(new Slot(container, k + j*4, 53 + k * 18, 15 + j * 19){
                    @Override
                    public boolean mayPlace(ItemStack pStack) {
                        return pStack.getItem() instanceof PotionItem;
                    }

                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }
                });
            }
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 84+ l * 18));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
        }
    }

    public static ModMenu createPotionShelfContainer(int containerId, Inventory playerInventory) {
        return new ModMenu(ModContainerTypes.POTION_SHELF.get(), containerId, playerInventory);
    }

    public static ModMenu createPotionShelfContainer(int containerId, Inventory playerInventory, Container inventory) {
        return new ModMenu(ModContainerTypes.POTION_SHELF.get(), containerId, playerInventory, inventory);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < 12) {
                if (!this.moveItemStackTo(itemstack1, 12, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 12, false)) {
                if (pIndex >= 12 && pIndex < this.slots.size()-9){
                    if (!this.moveItemStackTo(itemstack1, this.slots.size()-9, this.slots.size(), false)){
                        return ItemStack.EMPTY;
                    }
                }
                else if (pIndex >= this.slots.size()-9 && pIndex < this.slots.size()){
                    if (!this.moveItemStackTo(itemstack1, 12, this.slots.size()-9, false)){
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.container.stopOpen(pPlayer);
    }
}
