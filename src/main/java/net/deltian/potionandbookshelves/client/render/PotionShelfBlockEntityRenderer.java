package net.deltian.potionandbookshelves.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.deltian.potionandbookshelves.PotionAndBookshelves;
import net.deltian.potionandbookshelves.block.entity.PotionShelfBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class PotionShelfBlockEntityRenderer implements BlockEntityRenderer<PotionShelfBlockEntity> {

    public PotionShelfBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PotionShelfBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay) {
        if (blockEntity.getItems().isEmpty()) return;

        // Calculate the position and render each item
        for (int i = 0; i < blockEntity.getItems().size(); i++) {

            ItemStack itemStack = blockEntity.getItem(i);

            if (!itemStack.isEmpty()) {
                poseStack.pushPose();

                // Example position calculation: items in a grid
                float xOffset = ((i % 4)+1) * 0.2f;
                float yOffset;

                if (i < 4){
                    yOffset=0.8f;
                }
                else if (i >= 4 && i <8){
                    yOffset=0.46f;
                }
                else{
                    yOffset=0.12f;
                }

                poseStack.translate(xOffset, yOffset, 0.25f);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.GROUND, light, overlay, poseStack, source,0);

                poseStack.popPose();
            }
        }
    }
}
