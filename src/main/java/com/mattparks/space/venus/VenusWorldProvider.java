package com.mattparks.space.venus;

import com.mattparks.space.core.world.gen.GenChunkManager;
import com.mattparks.space.core.world.gen.GenChunkProvider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import net.minecraftforge.common.config.Configuration;

public class VenusWorldProvider extends WorldProviderSpace implements IGalacticraftWorldProvider {
	@Override
    public Vector3 getFogColor()
    {
        float f = 1.0f - this.getStarBrightness(1.0f);
        return new Vector3(225.0f * f, 125.0f * f, 5.0f * f);
    }

    @Override
    public Vector3 getSkyColor()
    {
        float f = 1.0f - this.getStarBrightness(1.0f);
        return new Vector3(200.0f * f, 150.0f * f, 5.0f * f);
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
		return 160000;
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
		this.worldChunkMgr = new GenChunkManager(getDimensionName(), VenusCore.instance.getBiomeID());
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
		float f1 = this.worldObj.getCelestialAngle(par1);
		float f2 = 1.0f - (MathHelper.cos(f1 * (float) Math.PI * 2.0f) * 2.0f + 0.25f);

		if (f2 < 0.0f) {
			f2 = 0.0f;
		}

		if (f2 > 1.0f) {
			f2 = 1.0f;
		}

		return f2 * f2 * 0.65f; // 65% star brightness.
	}

	@Override
	public float calculateCelestialAngle(long par1, float par3) {
		return super.calculateCelestialAngle(par1, par3);
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		return new GenChunkProvider(worldObj, worldObj.getSeed(), worldObj.getWorldInfo().isMapFeaturesEnabled(), VenusCore.instance.getGenerationSettings(), VenusCore.instance.getGenBiomeDecorator());
	}

	@Override
	public boolean isSkyColored() {
		return true;
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
		return "DIM" + VenusCore.instance.getDimensionID();
	}

	@Override
	public String getWelcomeMessage() {
		return "Entering Venus";
	}

	@Override
	public String getDepartMessage() {
		return "Leaving Venus";
	}

	@Override
	public String getDimensionName() {
		return "Venus";
	}

	@Override
	public boolean canBlockFreeze(int x, int y, int z, boolean byWater) {
		return false;
	}

	@Override
	public boolean canDoLightning(Chunk chunk) {
		return true;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return false;
	}

	@Override
	public float getGravity() {
		return (float) (0.08 * (1 - 0.907));
	}

	@Override
	public int getHeight() {
		return 800;
	}

	@Override
	public double getMeteorFrequency() {
		return 5.0;
	}

	@Override
	public double getFuelUsageMultiplier() {
		return 0.907;
	}

	@Override
	public boolean canSpaceshipTierPass(int tier) {
		return tier >= 2;
	}

	@Override
	public float getFallDamageModifier() {
		return 0.38f;
	}

	@Override
	public float getSoundVolReductionAmount() {
		return 10.0f;
	}

	@Override
	public CelestialBody getCelestialBody() {
		return VenusCore.instance.planet;
	}

	@Override
	public boolean hasBreathableAtmosphere() {
		return false;
	}

	@Override
	public float getThermalLevelModifier() {
		return 9.0f;
	}

	@Override
	public float getWindLevel() {
		return 7.0f;
	}
}
