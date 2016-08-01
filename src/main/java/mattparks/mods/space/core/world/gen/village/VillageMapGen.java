package mattparks.mods.space.core.world.gen.village;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import mattparks.mods.space.core.world.gen.GenerationSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;

public class VillageMapGen extends MapGenStructure {
	public List<BiomeGenBase> villageSpawnBiomes;
	public GenerationSettings settings;
	private final int terrainType;
	private static boolean initialized;

	public VillageMapGen(GenerationSettings settings, BiomeGenBase[] biomesForGeneration) {
		this.villageSpawnBiomes = Arrays.asList(biomesForGeneration);
		this.settings = settings;
		this.terrainType = 0;

		try {
			initiateStructures();
		} catch (Throwable e) {

		}
	}

	public void initiateStructures() throws Throwable {
		if (!VillageMapGen.initialized) {
			MapGenStructureIO.registerStructure(VillageStructureStart.class, settings.planetName + "Village");
			MapGenStructureIO.func_143031_a(VillageStructureComponentField.class, settings.planetName + "Field1");
			MapGenStructureIO.func_143031_a(VillageStructureComponentField2.class, settings.planetName + "Field2");
			MapGenStructureIO.func_143031_a(VillageStructureComponentHouse.class, settings.planetName + "House");
			MapGenStructureIO.func_143031_a(VillageStructureComponentRoadPiece.class, settings.planetName + "RoadPiece");
			MapGenStructureIO.func_143031_a(VillageStructureComponentPathGen.class, settings.planetName + "Path");
			MapGenStructureIO.func_143031_a(VillageStructureComponentTorch.class, settings.planetName + "Torch");
			MapGenStructureIO.func_143031_a(VillageStructureComponentStartPiece.class, settings.planetName + "Well");
			MapGenStructureIO.func_143031_a(VillageStructureComponentWoodHut.class, settings.planetName + "WoodHut");
		}

		VillageMapGen.initialized = true;
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int i, int j) {
		final byte numChunks = 32;
		final byte offsetChunks = 8;
		final int oldi = i;
		final int oldj = j;

		if (i < 0) {
			i -= numChunks - 1;
		}

		if (j < 0) {
			j -= numChunks - 1;
		}

		int randX = i / numChunks;
		int randZ = j / numChunks;
		final Random var7 = this.worldObj.setRandomSeed(i, j, 10387312);
		randX *= numChunks;
		randZ *= numChunks;
		randX += var7.nextInt(numChunks - offsetChunks);
		randZ += var7.nextInt(numChunks - offsetChunks);

		return oldi == randX && oldj == randZ;

	}

	@Override
	protected StructureStart getStructureStart(int par1, int par2) {
		FMLLog.info("Generating " + settings.planetName + " Village at x" + par1 * 16 + " z" + par2 * 16);
		return new VillageStructureStart(this.worldObj, this.rand, par1, par2, this.terrainType);
	}

	@Override
	public String func_143025_a() {
		return settings.planetName + "Village";
	}
}
