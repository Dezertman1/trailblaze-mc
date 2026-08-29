package dez.trailblaze.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;

public class NoopLevelTickAccess<T> implements LevelTickAccess<T> {
    @Override
    public void schedule(ScheduledTick<T> tick) {
        // no-op — we don't care about ticks actually firing in the reference sandbox
    }

    @Override
    public boolean hasScheduledTick(BlockPos pos, T type) {
        return false;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public boolean willTickThisTick(BlockPos pos, T type) {
        return false;
    }
}