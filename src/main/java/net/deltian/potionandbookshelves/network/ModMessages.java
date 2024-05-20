package net.deltian.potionandbookshelves.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.deltian.potionandbookshelves.PotionAndBookshelves;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PotionAndBookshelves.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.messageBuilder(SyncItemsPacket.class, id++)
                .encoder(SyncItemsPacket::toBytes)
                .decoder(SyncItemsPacket::new)
                .consumer(SyncItemsPacket::handle)
                .add();
    }
}
