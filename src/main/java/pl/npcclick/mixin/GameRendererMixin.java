package pl.npcclick.mixin;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.npcclick.NpcClickMod;
import java.util.Optional;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "findCrosshairTarget", at = @At("RETURN"), cancellable = true)
    private void npcclick$findCrosshairTarget(Entity camera, double maxDistance, float tickDelta,
                                               CallbackInfoReturnable<HitResult> cir) {
        if (!NpcClickMod.isEnabled()) return;
        HitResult current = cir.getReturnValue();
        if (!(current instanceof EntityHitResult entityHit)) return;
        if (!(entityHit.getEntity() instanceof PlayerEntity)) return;
        if (entityHit.getEntity() == camera) return;
        World world = camera.getWorld();
        Vec3d start = camera.getCameraPosVec(tickDelta);
        Vec3d look  = camera.getRotationVec(tickDelta);
        Vec3d end   = start.add(look.multiply(maxDistance));
        Box searchBox = camera.getBoundingBox().stretch(look.multiply(maxDistance)).expand(1.5, 1.5, 1.5);
        Entity bestEntity = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity candidate : world.getOtherEntities(camera, searchBox)) {
            if (candidate instanceof PlayerEntity) continue;
            if (!(candidate instanceof LivingEntity)) continue;
            Box hitBox = candidate.getBoundingBox().expand(candidate.getTargetingMargin());
            Optional<Vec3d> hit = hitBox.raycast(start, end);
            if (hit.isPresent()) {
                double dist = start.squaredDistanceTo(hit.get());
                if (dist < bestDist) {
                    bestDist = dist;
                    bestEntity = candidate;
                }
            }
        }
        if (bestEntity != null) {
            cir.setReturnValue(new EntityHitResult(bestEntity, bestEntity.getBoundingBox().getCenter()));
        }
    }
}
