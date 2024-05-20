package net.deltian.potionandbookshelves.network;

import net.deltian.potionandbookshelves.block.entity.PotionShelfBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncItemsPacket {
    private final BlockPos pos;
    private final NonNullList<ItemStack> items;

    public SyncItemsPacket(BlockPos pos, NonNullList<ItemStack> items) {
        this.pos = pos;
        this.items = items;
    }

    public SyncItemsPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        items = NonNullList.withSize(buf.readInt(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, buf.readItem());
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(items.size());
        for (ItemStack item : items) {
            buf.writeItem(item);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world = Minecraft.getInstance().level;
            if (world != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof PotionShelfBlockEntity) {
                    ((PotionShelfBlockEntity) blockEntity).setItems(items);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
