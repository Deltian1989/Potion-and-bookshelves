package net.deltian.potionandbookshelves.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.deltian.potionandbookshelves.block.entity.PotionShelfBlockEntity;
import net.deltian.potionandbookshelves.utils.RayTraceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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

        renderItemHitbox(blockEntity, poseStack, source);
    }

    private void renderItemHitbox(PotionShelfBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource) {

        Minecraft mc = Minecraft.getInstance();

        Player player = mc.player;
        if (player == null) return;

        BlockHitResult hitResult = (BlockHitResult)RayTraceUtils.rayTrace(mc.level, player, 5.0);
        if (hitResult.getType() == HitResult.Type.MISS) return;

        if (hitResult.getBlockPos() !=blockEntity.getBlockPos()) return;

        Vec3 hitVec = hitResult.getLocation();

        AABB[] hitboxes = PotionShelfBlockEntity.getItemHitboxes();
        Vec3 blockPos = Vec3.atLowerCornerOf(blockEntity.getBlockPos());
        for (AABB hitbox : hitboxes) {
            //if (hitbox.move(blockPos).contains(hitVec)) {
                renderHitbox(poseStack,bufferSource,hitbox);
            //}
        }
    }

    private void renderHitbox(PoseStack poseStack, MultiBufferSource bufferSource, AABB hitbox) {

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, hitbox, 1.0f, 1.0f, 1.0f, 0.5F);
    }
}
