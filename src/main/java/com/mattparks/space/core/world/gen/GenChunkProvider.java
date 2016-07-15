package com.mattparks.space.core.world.gen;

import java.util.List;
import java.util.Random;

import com.mattparks.space.core.utils.SpaceLog;
import com.mattparks.space.core.utils.SpacePair;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldChunkManagerSpace;
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient;
import micdoodle8.mods.galacticraft.core.world.gen.EnumCraterSize;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

public class GenChunkProvider extends ChunkProviderGenerate {
	// DO NOT CHANGE, MINECRAFT CONSTANTS
	private static final int MID_HEIGHT = 93;
	private static final int CHUNK_SIZE_X = 16;
	private static final int CHUNK_SIZE_Y = 256;
	private static final int CHUNK_SIZE_Z = 16;
	private static final double MAIN_FEATURE_FILTER_MOD = 4;
	private static final double LARGE_FEATURE_FILTER_MOD = 8;
	private static final double SMALL_FEATURE_FILTER_MOD = 8;
	
	private World worldObject;
	private Random random;
	private Gradient noiseGen1;
	private Gradient noiseGen2;
	private Gradient noiseGen3;
	private Gradient noiseGen4;
	private Gradient noiseGen5;
	private Gradient noiseGen6;
	private Gradient noiseGen7;

	public String biomeName;
	
	public GenerationSettings generationSetup;
	public GenBiomeDecorator biomeDecorator;
	public GenMapGenCave caveGenerator;
	public BiomeGenBase[] biomesForGeneration;

	public GenChunkProvider(World par1World, long par2, boolean par4, GenerationSettings generationSetup, GenBiomeDecorator biomeDecorator) {
		super(par1World, par2, par4);
		SpaceLog.severe("INIT GenChunkProvider");
		
		this.worldObject = par1World;
		
		this.random = new Random(par2);
		this.noiseGen1 = new Gradient(this.random.nextLong(), 4, 0.25f);
		this.noiseGen2 = new Gradient(this.random.nextLong(), 4, 0.25f);
		this.noiseGen3 = new Gradient(this.random.nextLong(), 4, 0.25f);
		this.noiseGen4 = new Gradient(this.random.nextLong(), 2, 0.25f);
		this.noiseGen5 = new Gradient(this.random.nextLong(), 1, 0.25f);
		this.noiseGen6 = new Gradient(this.random.nextLong(), 1, 0.25f);
		this.noiseGen7 = new Gradient(this.random.nextLong(), 1, 0.25f);
		
		WorldChunkManagerSpace chunkManager = (WorldChunkManagerSpace) worldObject.getWorldChunkManager();
		
		this.biomeName = chunkManager.getBiome().biomeName;
		
		this.generationSetup = generationSetup;
		this.biomeDecorator = biomeDecorator;
		this.caveGenerator = new GenMapGenCave(generationSetup);
		
		this.biomesForGeneration = new BiomeGenBase[]{ chunkManager.getBiome() };
	}

	@Override
	public Chunk provideChunk(int par1, int par2) {
		random.setSeed(par1 * 341873128712L + par2 * 132897987541L);
		Block[] ids = new Block[32768 * 2];
		byte[] meta = new byte[32768 * 2];
		generateTerrain(par1, par2, ids, meta);
		createCraters(par1, par2, ids, meta);
		biomesForGeneration = worldObject.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration, par1 * 16, par2 * 16, 16, 16);
		replaceBlocksForBiome(par1, par2, ids, meta, this.biomesForGeneration);
		caveGenerator.generate(this, this.worldObject, par1, par2, ids, meta);

		Chunk var4 = new Chunk(this.worldObject, ids, meta, par1, par2);
		byte[] var5 = var4.getBiomeArray();

		for (int var6 = 0; var6 < var5.length; ++var6) {
			var5[var6] = (byte) biomesForGeneration[var6].biomeID;
		}

		var4.generateSkylightMap();
		return var4;
	}

	public void generateTerrain(int chunkX, int chunkZ, Block[] idArray, byte[] metaArray) {
		this.noiseGen1.setFrequency(0.0125f);
		this.noiseGen2.setFrequency(0.015f);
		this.noiseGen3.setFrequency(0.01f);
		this.noiseGen4.setFrequency(0.02f);
		this.noiseGen5.setFrequency(0.01f);
		this.noiseGen6.setFrequency(0.001f);
		this.noiseGen7.setFrequency(0.005f);

		for (int x = 0; x < CHUNK_SIZE_X; x++) {
			for (int z = 0; z < CHUNK_SIZE_Z; z++) {
				double baseHeight = noiseGen1.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * generationSetup.terrainHeightMod;
				double smallHillHeight = noiseGen2.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * generationSetup.smallFeatureHeightMod;
				double mountainHeight = Math.abs(noiseGen3.getNoise(chunkX * 16 + x, chunkZ * 16 + z));
				double valleyHeight = Math.abs(noiseGen4.getNoise(chunkX * 16 + x, chunkZ * 16 + z));
				double featureFilter = noiseGen5.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * MAIN_FEATURE_FILTER_MOD;
				double largeFilter = noiseGen6.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * LARGE_FEATURE_FILTER_MOD;
				double smallFilter = noiseGen7.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * SMALL_FEATURE_FILTER_MOD - 0.5;
				mountainHeight = lerp(smallHillHeight, mountainHeight * generationSetup.mountainHeightMod, fade(clamp(mountainHeight * 2, 0, 1)));
				valleyHeight = lerp(smallHillHeight, valleyHeight * generationSetup.valleyHeightMod - generationSetup.valleyHeightMod + 9, fade(clamp((valleyHeight + 2) * 4, 0, 1)));

				double yDev = lerp(valleyHeight, mountainHeight, fade(largeFilter));
				yDev = lerp(smallHillHeight, yDev, smallFilter);
				yDev = lerp(baseHeight, yDev, featureFilter);

				for (int y = 0; y < CHUNK_SIZE_Y; y++) {
					if (y < MID_HEIGHT + yDev) {
						idArray[this.getIndex(x, y, z)] = generationSetup.blockLower.getFirst();
						metaArray[this.getIndex(x, y, z)] = generationSetup.blockLower.getSecond();
					}
				}
			}
		}
	}

	public void createCraters(int chunkX, int chunkZ, Block[] chunkArray, byte[] metaArray) {
		for (int cx = chunkX - 2; cx <= chunkX + 2; cx++) {
			for (int cz = chunkZ - 2; cz <= chunkZ + 2; cz++) {
				for (int x = 0; x < GenChunkProvider.CHUNK_SIZE_X; x++) {
					for (int z = 0; z < GenChunkProvider.CHUNK_SIZE_Z; z++) {
						if (Math.abs(this.randFromPoint(cx * 16 + x, (cz * 16 + z) * 1000)) < this.noiseGen4.getNoise(x * CHUNK_SIZE_X + x, cz * CHUNK_SIZE_Z + z) / generationSetup.craterProbibility) {
							Random random = new Random(cx * 16 + x + (cz * 16 + z) * 5000);
							EnumCraterSize cSize = EnumCraterSize.sizeArray[random.nextInt(EnumCraterSize.sizeArray.length)];
							int size = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE;
							this.makeCrater(cx * 16 + x, cz * 16 + z, chunkX * 16, chunkZ * 16, size, chunkArray, metaArray);
						}
					}
				}
			}
		}
	}

	public void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, Block[] chunkArray, byte[] metaArray) {
		for (int x = 0; x < GenChunkProvider.CHUNK_SIZE_X; x++) {
			for (int z = 0; z < GenChunkProvider.CHUNK_SIZE_Z; z++) {
				double xDev = craterX - (chunkX + x);
				double zDev = craterZ - (chunkZ + z);
				
				if (xDev * xDev + zDev * zDev < size * size) {
					xDev /= size;
					zDev /= size;
					double sqrtY = xDev * xDev + zDev * zDev;
					double yDev = sqrtY * sqrtY * 6;
					yDev = 5 - yDev;
					int helper = 0;
					
					for (int y = 127; y > 0; y--) {
						if (Blocks.air != chunkArray[this.getIndex(x, y, z)] && helper <= yDev) {
							chunkArray[this.getIndex(x, y, z)] = Blocks.air;
							metaArray[this.getIndex(x, y, z)] = 0;
							helper++;
						}
						
						if (helper > yDev) {
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void replaceBlocksForBiome(int par1, int par2, Block[] arrayOfIDs, byte[] arrayOfMeta, BiomeGenBase[] par4ArrayOfBiomeGenBase) {
		int var5 = 20;
		float var6 = 0.03125F;
		this.noiseGen4.setFrequency(var6 * 2);
		
		for (int var8 = 0; var8 < 16; ++var8) {
			for (int var9 = 0; var9 < 16; ++var9) {
				int var12 = (int) (this.noiseGen4.getNoise(par1 * 16 + var8, par2 * 16 + var9) / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
				int var13 = -1;
				Block var14 = generationSetup.blockTop.getFirst();
				byte var14m = generationSetup.blockTop.getSecond();
				Block var15 = generationSetup.blockFiller.getFirst();
				byte var15m = generationSetup.blockFiller.getSecond();

				for (int var16 = CHUNK_SIZE_Y - 1; var16 >= 0; --var16) {
					int index = this.getIndex(var8, var16, var9);

					if (var16 <= 0 + this.random.nextInt(5)) {
						arrayOfIDs[index] = Blocks.bedrock;
					} else {
						Block var18 = arrayOfIDs[index];

						if (Blocks.air == var18) {
							var13 = -1;
						} else if (var18 == generationSetup.blockLower.getFirst()) {
							arrayOfMeta[index] = generationSetup.blockLower.getSecond();

							if (var13 == -1) {
								if (var12 <= 0) {
									var14 = Blocks.air;
									var14m = 0;
									var15 = generationSetup.blockLower.getFirst();
									var15m = generationSetup.blockLower.getSecond();
								} else if (var16 >= var5 - -16 && var16 <= var5 + 1) {
									var14 = generationSetup.blockTop.getFirst();
									var14m = generationSetup.blockTop.getSecond();
									var14 = generationSetup.blockFiller.getFirst();
									var14m = generationSetup.blockFiller.getSecond();
								}

								var13 = var12;

								if (var16 >= var5 - 1) {
									arrayOfIDs[index] = var14;
									arrayOfMeta[index] = var14m;
								} else {
									arrayOfIDs[index] = var15;
									arrayOfMeta[index] = var15m;
								}
							} else if (var13 > 0) {
								--var13;
								arrayOfIDs[index] = var15;
								arrayOfMeta[index] = var15m;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean chunkExists(int par1, int par2) {
		return true;
	}

	@Override
	public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {
		BlockFalling.fallInstantly = true;
		int var4 = par2 * 16;
		int var5 = par3 * 16;
		worldObject.getBiomeGenForCoords(var4 + 16, var5 + 16);
		random.setSeed(worldObject.getSeed());
		long var7 = random.nextLong() / 2l * 2l + 1l;
		long var9 = random.nextLong() / 2l * 2l + 1l;
		random.setSeed(par2 * var7 + par3 * var9 ^ worldObject.getSeed());
		decoratePlanet(this.worldObject, random, var4, var5);
		BlockFalling.fallInstantly = false;
	}

	public void decoratePlanet(World par1World, Random par2Random, int par3, int par4) {
		this.biomeDecorator.decorate(par1World, par2Random, par3, par4);
	}

	@Override
	public void recreateStructures(int par1, int par2) {
	}

	@Override
	public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
		return true;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public String makeString() {
		return biomeName + "LevelSource";
	}

	@Override
	public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int i, int j, int k) {
		if (par1EnumCreatureType == EnumCreatureType.monster) {
			return generationSetup.spawnableMonsters;
		} else {
			return null;
		}
	}
	

	private double lerp(double d1, double d2, double t) {
		if (t < 0.0) {
			return d1;
		} else if (t > 1.0) {
			return d2;
		} else {
			return d1 + (d2 - d1) * t;
		}
	}

	private double fade(double n) {
		return n * n * n * (n * (n * 6 - 15) + 10);
	}

	private double clamp(double x, double min, double max) {
		if (x < min) {
			return min;
		}
		if (x > max) {
			return max;
		}
		return x;
	}

	private double randFromPoint(int x, int z) {
		int n;
		n = x + z * 57;
		n = n << 13 ^ n;
		return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 & 0x7fffffff) / 1073741824.0;
	}

	private int getIndex(int x, int y, int z) {
		return (x * 16 + z) * 256 + y;
	}

	public static class GenerationSettings {
		protected double terrainHeightMod;
		protected double smallFeatureHeightMod;
		protected double mountainHeightMod;
		protected double valleyHeightMod;
		protected int craterProbibility;
		protected int caveChance;
		
		protected SpacePair<Block, Byte> blockTop;
		protected SpacePair<Block, Byte> blockFiller;
		protected SpacePair<Block, Byte> blockLower;
		
		protected List<SpawnListEntry> spawnableMonsters;
		
		public GenerationSettings(double terrainHeightMod, double smallFeatureHeightMod, double mountainHeightMod, double valleyHeightMod, int craterProbibility, int caveChance,
				SpacePair<Block, Integer> blockTop, SpacePair<Block, Integer> blockFiller, SpacePair<Block, Integer> blockLower,
				List<SpawnListEntry> spawnableMonsters) {
			this.terrainHeightMod = terrainHeightMod;
			this.smallFeatureHeightMod = smallFeatureHeightMod;
			this.mountainHeightMod = mountainHeightMod;
			this.valleyHeightMod = valleyHeightMod;
			this.craterProbibility = craterProbibility;
			this.caveChance = caveChance;
			
			this.blockTop = new SpacePair<Block, Byte>(blockTop.getFirst(), new Byte("" + blockTop.getSecond()));
			this.blockFiller = new SpacePair<Block, Byte>(blockFiller.getFirst(), new Byte("" + blockFiller.getSecond()));
			this.blockLower = new SpacePair<Block, Byte>(blockLower.getFirst(), new Byte("" + blockLower.getSecond()));
			
			this.spawnableMonsters = spawnableMonsters;
		}
	}
}
