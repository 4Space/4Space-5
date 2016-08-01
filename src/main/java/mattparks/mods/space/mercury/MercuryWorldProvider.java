package mattparks.mods.space.mercury;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mattparks.mods.space.core.world.gen.GenChunkManager;
import mattparks.mods.space.core.world.gen.GenChunkProvider;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class MercuryWorldProvider extends WorldProviderSpace implements IGalacticraftWorldProvider {
	@Override
	public float getGravity() {
		return (float) (0.08 * (1 - 0.378));
	}

	@Override
	public double getMeteorFrequency() {
		return 6.0;
	}

	@Override
	public double getFuelUsageMultiplier() {
		return 0.6;
	}

	@Override
	public boolean canSpaceshipTierPass(int tier) {
		return tier >= 2;
	}

	@Override
	public float getFallDamageModifier() {
		return 0.378f;
	}

	@Override
	public float getSoundVolReductionAmount() {
		return 10.0f;
	}

	@Override
	public float getThermalLevelModifier() {
		if (this.isDaytime()) {
			return 12.0F;
		} else {
			return -9.0F;
		}
	}

	@Override
	public float getWindLevel() {
		return 0.2f;
	}

	@Override
	public CelestialBody getCelestialBody() {
		return MercuryCore.instance.planet;
	}

	@Override
	public Vector3 getFogColor() {
		return new Vector3(0, 0, 0);
	}

	@Override
	public Vector3 getSkyColor() {
		return new Vector3(0, 0, 0);
	}

	@Override
	public boolean canRainOrSnow() {
		return false;
	}

	@Override
	public boolean hasSunset() {
		return false;
	}

	@Override
	public long getDayLength() {
		return 5832000;
	}

	@Override
	public boolean shouldForceRespawn() {
		return !ConfigManagerCore.forceOverworldRespawn;
	}

	@Override
	public Class<? extends IChunkProvider> getChunkProviderClass() {
		return GenChunkProvider.class;
	}

	@Override
	public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
		return null; // So this should not be null, but normally this would return `GenChunkManager.class`. That does not work with our new generator so we skip this method.
	}

	@Override
	public void setDimension(int var1) {
		this.dimensionId = var1;
		super.setDimension(var1);
	}

	@Override
	protected void generateLightBrightnessTable() {
		final float var1 = 0.0f;

		for (int var2 = 0; var2 <= 15; ++var2) {
			final float var3 = 1.0f - var2 / 15.0f;
			this.lightBrightnessTable[var2] = (1.0f - var3) / (var3 * 3.0f + 1.0f) * (1.0f - var1) + var1;
		}
	}

	@Override
	public float[] calcSunriseSunsetColors(float var1, float var2) {
		return null;
	}

	@Override
	public void registerWorldChunkManager() {
		this.worldChunkMgr = new GenChunkManager(getDimensionName(), MercuryCore.instance.getBiomeID());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 getFogColor(float var1, float var2) {
		return Vec3.createVectorHelper(getFogColor().x / 255.0f, getFogColor().y / 255.0f, getFogColor().z / 255.0f);
	}

	@Override
	public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
		return Vec3.createVectorHelper(getSkyColor().x / 255.0f, getSkyColor().y / 255.0f, getSkyColor().z / 255.0f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getStarBrightness(float par1) {
		final float var2 = this.worldObj.getCelestialAngle(par1);
		float var3 = 1.0f - (MathHelper.cos(var2 * (float) Math.PI * 2.0f) * 2.0f + 0.25f);

		if (var3 < 0.0f) {
			var3 = 0.0f;
		}

		if (var3 > 1.0f) {
			var3 = 1.0f;
		}

		return var3 * var3 * 0.5f + 0.3f;
	}

	@Override
	public float calculateCelestialAngle(long par1, float par3) {
		return super.calculateCelestialAngle(par1, par3);
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		return new GenChunkProvider(worldObj, worldObj.getSeed(), worldObj.getWorldInfo().isMapFeaturesEnabled(), MercuryCore.instance.getGenerationSettings(), MercuryCore.instance.getGenBiomeDecorator());
	}

	@Override
	public boolean isSkyColored() {
		return false;
	}

	@Override
	public double getHorizon() {
		return 44.0;
	}

	@Override
	public int getAverageGroundLevel() {
		return 44;
	}

	@Override
	public boolean isSurfaceWorld() {
		return true;
	}

	@Override
	public boolean canCoordinateBeSpawn(int var1, int var2) {
		return true;
	}

	@Override
	public boolean canRespawnHere() {
		return !ConfigManagerCore.forceOverworldRespawn;
	}

	@Override
	public String getSaveFolder() {
		return "DIM" + MercuryCore.instance.getDimensionID();
	}

	@Override
	public String getWelcomeMessage() {
		return "Entering Mercury";
	}

	@Override
	public String getDepartMessage() {
		return "Leaving Mercury";
	}

	@Override
	public String getDimensionName() {
		return "Mercury";
	}

	@Override
	public boolean canBlockFreeze(int x, int y, int z, boolean byWater) {
		return false;
	}

	@Override
	public boolean canDoLightning(Chunk chunk) {
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return false;
	}


	@Override
	public int getHeight() {
		return 800;
	}

	@Override
	public boolean hasBreathableAtmosphere() {
		return false;
	}
}
