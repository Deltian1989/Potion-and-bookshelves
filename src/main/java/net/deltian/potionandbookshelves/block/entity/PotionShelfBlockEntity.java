package net.deltian.potionandbookshelves.block.entity;

import net.deltian.potionandbookshelves.PotionAndBookshelves;
import net.deltian.potionandbookshelves.inventory.ModMenu;
import net.deltian.potionandbookshelves.network.ModMessages;
import net.deltian.potionandbookshelves.network.SyncItemsPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PotionShelfBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {

    private Component name;

    private NonNullList<ItemStack> items = NonNullList.withSize(12, ItemStack.EMPTY);
    private static final Map<Direction, AABB[]> ITEM_HITBOXES_MAP;

    static {

        ITEM_HITBOXES_MAP=new HashMap<>();

        createHitboxesForNorthDirection();

        createHitboxesForSouthDirection();

        createHitboxesForWestDirection();

        createHitboxesForEastDirection();
    }

    private static void createHitboxesForNorthDirection() {
        AABB[] item_hitboxes = new AABB[12];

        for (int i = 0; i < 12; i++) {

            float y;

            if (i < 4){
                y=0.8f;
            }
            else if (i >= 4 && i <8){
                y=0.46f;
            }
            else{
                y=0.12f;
            }

            y-=0.05f;

            float x = ((i % 4)+1) * 0.2f-0.0625f;
            float z = 0.25f-0.03125f;

            item_hitboxes[i] = new AABB(x, y, z, x + 0.14f, y+0.21f, z + 0.0625f);
        }

        ITEM_HITBOXES_MAP.put(Direction.NORTH,item_hitboxes);
    }

    private static void createHitboxesForSouthDirection() {
        AABB[] item_hitboxes = new AABB[12];

        for (int i = 0; i < 12; i++) {

            float y;

            if (i < 4){
                y=0.8f;
            }
            else if (i >= 4 && i <8){
                y=0.46f;
            }
            else{
                y=0.12f;
            }

            y-=0.05f;

            float x = 1-(((i % 4)+1) * 0.2f-0.0625f -0.015f);
            float z =  0.75f-0.03125f;

            item_hitboxes[i] = new AABB(x - 0.14f, y+0.21f, z + 0.0625f,x, y, z);
        }

        ITEM_HITBOXES_MAP.put(Direction.SOUTH,item_hitboxes);
    }

    private static void createHitboxesForWestDirection() {
        AABB[] item_hitboxes = new AABB[12];

        for (int i = 0; i < 12; i++) {

            float y;

            if (i < 4){
                y=0.8f;
            }
            else if (i >= 4 && i <8){
                y=0.46f;
            }
            else{
                y=0.12f;
            }

            y-=0.05f;

            float x = 1-(((i % 4)+1) * 0.2f-0.0625f -0.015f);
            float z =  0.75f-0.03125f;

            item_hitboxes[i] = new AABB(x - 0.14f, y+0.21f, z + 0.0625f,x, y, z);
        }

        ITEM_HITBOXES_MAP.put(Direction.WEST,item_hitboxes);
    }

    private static void createHitboxesForEastDirection() {
        AABB[] item_hitboxes = new AABB[12];

        for (int i = 0; i < 12; i++) {

            float y;

            if (i < 4){
                y=0.8f;
            }
            else if (i >= 4 && i <8){
                y=0.46f;
            }
            else{
                y=0.12f;
            }

            y-=0.05f;

            float x = 1-(((i % 4)+1) * 0.2f-0.0625f -0.015f);
            float z =  0.75f-0.03125f;

            item_hitboxes[i] = new AABB(x - 0.14f, y+0.21f, z + 0.0625f,x, y, z);
        }

        ITEM_HITBOXES_MAP.put(Direction.EAST,item_hitboxes);
    }

    public static AABB[] getItemHitboxes(Direction direction) {
        return ITEM_HITBOXES_MAP.get(direction);
    }

    public PotionShelfBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityTypes.POTION_SHELF.get(),pPos, pBlockState);
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
        super.saveAdditional(compoundTag);
        if (this.name != null) {
            compoundTag.putString("CustomName", Component.Serializer.toJson(this.name));
        }

        ContainerHelper.saveAllItems(compoundTag, this.items);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return ClientboundBlockEntityDataPacket.create(this, blockEntity -> tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
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

    public NonNullList<ItemStack> getItems() {
        return this.items;
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

        PotionAndBookshelves.LOGGER.info("Item set: "+stack);

        if (index >= 0 && index < this.items.size()) {
            this.items.set(index, stack);
        }

        this.setChanged();

        if (level != null && level instanceof ServerLevel && !level.isClientSide) {
            ModMessages.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)),
                    new SyncItemsPacket(worldPosition, items));
        }


    }

    public void setItems(NonNullList<ItemStack> items) {
        PotionAndBookshelves.LOGGER.info("Items set.");
        if (level != null && level instanceof ServerLevel && !level.isClientSide) {
            ModMessages.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)),
                    new SyncItemsPacket(worldPosition, items));
        }

        this.items = items;
        setChanged(); // Mark the block entity as changed to trigger a render update
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
