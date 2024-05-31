package net.deltian.potionandbookshelves.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.deltian.potionandbookshelves.PotionAndBookshelves;
import net.deltian.potionandbookshelves.block.PotionShelfBlock;
import net.deltian.potionandbookshelves.block.entity.PotionShelfBlockEntity;
import net.deltian.potionandbookshelves.utils.RayTraceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class PotionShelfBlockEntityRenderer implements BlockEntityRenderer<PotionShelfBlockEntity> {

    public PotionShelfBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PotionShelfBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay) {
        if (blockEntity.getItems().isEmpty()) return;

        var blockState = blockEntity.getBlockState();

        var direction = blockState.getValue(PotionShelfBlock.FACING);

        // Calculate the position and render each item
        for (int i = 0; i < blockEntity.getItems().size(); i++) {

            ItemStack itemStack = blockEntity.getItem(i);

            if (!itemStack.isEmpty()) {
                poseStack.pushPose();

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

                float xOffset =0;
                float zOffset = 0;

                float rotationY = 0;

                switch (direction){
                    case NORTH:{
                        xOffset = 0.2f + (i % 4) * 0.2f;
                        zOffset = 0.25f;
                        break;
                    }
                    case SOUTH:{
                        xOffset = 1 - (0.2f + (i % 4) * 0.2f);
                        zOffset = 0.75f;
                        break;
                    }
                    case WEST:{
                        xOffset = 0.25f;
                        zOffset = 1 - (0.2f + (i % 4) * 0.2f);
                        rotationY=-90;
                        break;
                    }
                    case EAST:{
                        xOffset = 0.75f;
                        zOffset = 0.2f + (i % 4) * 0.2f;
                        rotationY=-90;
                        break;
                    }
                }


                poseStack.translate(xOffset, yOffset, zOffset);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationY));;
                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.GROUND, light, overlay, poseStack, source,0);

                poseStack.popPose();
            }
        }

        renderItemHitbox(blockEntity, partialTicks, poseStack, source,light, overlay, direction);
    }

    private void renderItemHitbox(PotionShelfBlockEntity blockEntity,float partialTicks,  PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, Direction direction) {

        Minecraft mc = Minecraft.getInstance();

        Player player = mc.player;
        if (player == null) return;

        BlockPos blockPos = blockEntity.getBlockPos();

        Vec3 start = player.getEyePosition(partialTicks);
        Vec3 end = start.add(player.getViewVector(partialTicks).scale(20.0D));

        Optional<AABB> itemHitbox = getItemHitbox(start, end,blockPos, direction);

        if (itemHitbox.isPresent()) {
            renderHitbox(poseStack, bufferSource, itemHitbox.get());

            renderBlock(blockEntity,poseStack,bufferSource,light, overlay);
        }
    }

    private Optional<AABB> getItemHitbox(Vec3 start, Vec3 end, BlockPos blockPos, Direction direction) {
        AABB[] itemHitboxes = PotionShelfBlockEntity.getItemHitboxes(direction);
        for (AABB itemHitbox : itemHitboxes) {

            var currentHitBoxPos = itemHitbox.move(blockPos);

            if (currentHitBoxPos.clip(start, end).isPresent()) {
                return Optional.of(itemHitbox);
            }
        }
        return Optional.empty();
    }

    private void renderHitbox(PoseStack poseStack, MultiBufferSource bufferSource, AABB hitbox) {

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, hitbox, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    private void renderBlock(PotionShelfBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BlockPos blockPos = blockEntity.getBlockPos();
        BlockState blockState = blockEntity.getBlockState();

        BlockRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
        poseStack.pushPose();

        // Apply translation and rotation based on block position and state
        poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        // Render the block model
        blockRenderDispatcher.renderSingleBlock(blockState, poseStack, bufferSource, combinedLight, combinedOverlay);

        poseStack.popPose();
    }
}
