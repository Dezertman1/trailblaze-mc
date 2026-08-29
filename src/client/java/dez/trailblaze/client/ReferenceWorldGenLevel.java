package dez.trailblaze.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.attribute.EnvironmentAttributeReader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ReferenceWorldGenLevel implements WorldGenLevel {
    private final ServerLevel serverLevel;
    private final ProtoChunk targetChunk;
    private final Map<ChunkPos, ProtoChunk> chunkCache = new HashMap<>();

    public ReferenceWorldGenLevel(ServerLevel serverLevel, ProtoChunk targetChunk) {
        this.serverLevel = serverLevel;
        this.targetChunk = targetChunk;
        this.chunkCache.put(targetChunk.getPos(), targetChunk); // pre-seed cache too, avoids regenerating it
    }


    private ProtoChunk getOrCreateChunk(int chunkX, int chunkZ) {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        return chunkCache.computeIfAbsent(pos, p ->
                (ProtoChunk) ReferenceChunkGenerator.generateAndFillReference(serverLevel, p, false).join()
        );
    }

    @Override
    public long getSeed() {
        return serverLevel.getSeed();
    }

    @Override
    public ServerLevel getLevel() {
        return serverLevel;
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos pos) {
        return null;
    }

    @Override
    public long nextSubTickCount() {
        return 0;
    }

    @Override
    public LevelData getLevelData() {
        return serverLevel.getLevelData();
    }

    @Override
    public @Nullable MinecraftServer getServer() {
        return null;
    }

    @Override
    public ChunkSource getChunkSource() {
        return null;
    }

    @Override
    public RandomSource getRandom() {
        return null;
    }

    @Override
    public void playSound(@Nullable Entity except, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {

    }

    @Override
    public void addParticle(ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {

    }

    @Override
    public void levelEvent(@Nullable Entity source, int type, BlockPos pos, int data) {

    }

    @Override
    public void gameEvent(Holder<GameEvent> gameEvent, Vec3 position, GameEvent.Context context) {

    }

    @Override
    public List<Entity> getEntities(@Nullable Entity except, AABB bb, Predicate<? super Entity> selector) {
        return List.of();
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> type, AABB bb, Predicate<? super T> selector) {
        return List.of();
    }

    @Override
    public List<? extends Player> players() {
        return List.of();
    }

    @Override
    public @Nullable ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus targetStatus, boolean loadOrGenerate) {
        return getOrCreateChunk(chunkX, chunkZ);
    }

    @Override
    public int getHeight(Heightmap.Types type, int x, int z) {
        return 0;
    }

    @Override
    public int getSkyDarken() {
        return 0;
    }

    @Override
    public BiomeManager getBiomeManager() {
        return serverLevel.getBiomeManager();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int quartX, int quartY, int quartZ) {
        return null;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        return serverLevel.getSeaLevel();
    }

    @Override
    public DimensionType dimensionType() {
        return serverLevel.dimensionType();
    }

    @Override
    public RegistryAccess registryAccess() {
        return serverLevel.registryAccess();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return null;
    }

    @Override
    public EnvironmentAttributeReader environmentAttributes() {
        return null;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public WorldBorder getWorldBorder() {
        return null;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        ChunkPos chunkPos = ChunkPos.containing(pos);
        ProtoChunk chunk = getOrCreateChunk(chunkPos.x(), chunkPos.z());
        return chunk.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        ChunkPos chunkPos = ChunkPos.containing(pos);
        ProtoChunk chunk = getOrCreateChunk(chunkPos.x(), chunkPos.z());
        return chunk.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        ChunkPos chunkPos = ChunkPos.containing(pos);
        ProtoChunk chunk = getOrCreateChunk(chunkPos.x(), chunkPos.z());
        return chunk.getFluidState(pos);
    }

    @Override
    public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> predicate) {
        return false;
    }

    @Override
    public boolean isFluidAtPosition(BlockPos pos, Predicate<FluidState> predicate) {
        return false;
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int updateFlags, int updateLimit) {
        ChunkPos chunkPos = ChunkPos.containing(pos);
        ProtoChunk chunk = getOrCreateChunk(chunkPos.x(), chunkPos.z());
        chunk.setBlockState(pos, state, updateFlags);
        return true;
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean movedByPiston) {
        return false;
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropResources, @Nullable Entity breaker, int updateLimit) {
        return false;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return new NoopLevelTickAccess<>();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return new NoopLevelTickAccess<>();
    }
}