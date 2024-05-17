package net.deltian.potionandbookshelves.block.entity;

import net.deltian.potionandbookshelves.PotionAndBookshelves;
import net.deltian.potionandbookshelves.inventory.ModMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PotionShelfBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {

    private Component name;

    private NonNullList<ItemStack> items = NonNullList.withSize(12, ItemStack.EMPTY);

    public PotionShelfBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityTypes.IRON_CHEST.get(),pPos, pBlockState);
    }

    private Component getDefaultName() {
        return new TranslatableComponent(PotionAndBookshelves.MOD_ID + ".container.potion_shelf");
    }

    @javax.annotation.Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        if (compoundTag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(compoundTag.getString("CustomName"));
        }

        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        ContainerHelper.loadAllItems(compoundTag, this.items);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        if (this.name != null) {
            compoundTag.putString("CustomName", Component.Serializer.toJson(this.name));
        }

        ContainerHelper.saveAllItems(compoundTag, this.items);
    }

    @Override
    public int getContainerSize() {
        return 12;
    }

    @Override
    public Component getName()
    {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ContainerHelper.removeItem(this.items, index, count);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (stack.getItem() instanceof PotionItem){

            if (items.get(index).isEmpty()){
                this.items.set(index, stack);

                this.setChanged();
            }
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
            this.items.clear();
    }

    public void setCustomName(Component pName) {
        this.name = pName;
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return ModMenu.createPotionShelfContainer(pContainerId, pPlayerInventory, this);
    }
}
