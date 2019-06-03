package fi.dy.masa.tweakeroo.mixin;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

@Mixin(RenderLiving.class)
public abstract class MixinRenderLiving<T extends EntityLiving> extends RenderLivingBase<T>
{
    public MixinRenderLiving(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void ignoreDeadEntities(T livingEntity, ICamera camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_NO_DEAD_MOB_TARGETING.getBooleanValue() &&
            livingEntity instanceof EntityLivingBase &&
            (livingEntity).getHealth() <= 0f)
        {
            cir.setReturnValue(false);
        }
    }
}
