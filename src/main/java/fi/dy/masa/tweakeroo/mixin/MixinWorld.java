package fi.dy.masa.tweakeroo.mixin;

import java.util.HashSet;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MixinWorld
{
    @Shadow
    @Final
    public List<TileEntity> loadedTileEntityList;

    @Shadow
    @Final
    public List<TileEntity> tickableTileEntities;

    @Shadow
    @Final
    private List<TileEntity> tileEntitiesToBeRemoved;

    @Inject(method = "tickEntities",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;tileEntitiesToBeRemoved:Ljava/util/List;", ordinal = 0))
    private void optimizedTileEntityRemoval(CallbackInfo ci)
    {
        if (Configs.Fixes.TILE_UNLOAD_OPTIMIZATION.getBooleanValue())
        {
            if (this.tileEntitiesToBeRemoved.isEmpty() == false)
            {
                HashSet<TileEntity> remove = new HashSet<>();
                remove.addAll(this.tileEntitiesToBeRemoved);
                this.tickableTileEntities.removeAll(remove);
                this.loadedTileEntityList.removeAll(remove);
                this.tileEntitiesToBeRemoved.clear();
            }
        }
    }

    @Inject(method = "tickEntity(Lnet/minecraft/entity/Entity;Z)V", at = @At("HEAD"), cancellable = true)
    private void preventEntityTicking(Entity entityIn, boolean forceUpdate, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_ENTITY_TICKING.getBooleanValue() && (entityIn instanceof EntityPlayer) == false)
        {
            ci.cancel();
        }
    }

    @Redirect(method = "tickEntities",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/World;processingLoadedTiles:Z", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;isRemoved()Z", ordinal = 0))
    private boolean preventTileEntityTicking(TileEntity te) {
        if (FeatureToggle.TWEAK_NO_TILE_ENTITY_TICKING.getBooleanValue()) {
            return true;
        }

        return te.isRemoved();
    }
}
