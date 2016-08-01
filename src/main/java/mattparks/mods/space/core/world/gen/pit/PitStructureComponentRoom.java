package mattparks.mods.space.core.world.gen.pit;

import java.util.List;
import java.util.Random;

import mattparks.mods.space.core.world.gen.GenerationSettings;
import micdoodle8.mods.galacticraft.core.world.gen.StructureComponentGC;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class PitStructureComponentRoom extends StructureComponentGC {
	private GenerationSettings settings;
	
	public int corridorCount;
	public int originalFourCorridorLength;
	public int bossEntryCorridor;
	public int bossEntryCount;
	private int averageGroundLevel = -1;
	private final int height;
	private final int width;

	public PitStructureComponentRoom(GenerationSettings settings, int type, World world, Random par2Random, int x, int y, int z, int height, int width, int cbm) {
		super(type);
		this.setCoordBaseMode(cbm);
		this.settings = settings;
		this.height = height;
		this.width = width;
		this.boundingBox = StructureComponentGC.getComponentToAddBoundingBox(x, 78 - this.height, z, 0, 0, 0, 7, this.height, 7, cbm);
	}

	@Override
	public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random) {
		int var4;

		for (var4 = 0; var4 < 4; ++var4) {
			final int[] var5 = this.getValidOpening(par3Random, var4);

			this.makeCorridor(par2List, par3Random, 1, var5[0], var5[1], var5[2], this.width, 7, var4);
		}
	}

	public int[] getValidOpening(Random var1, int var2) {
		if (var2 == 0) {
			return new int[] { this.width - 1, 0, 1 };
		} else if (var2 == 1) {
			return new int[] { 1, 0, this.width - 1 };
		} else if (var2 == 2) {
			return new int[] { 0, 0, 1 };
		} else if (var2 == 3) {
			return new int[] { 1, 0, 0 };
		}

		return new int[] { 0, 0, 0 };
	}

	public boolean makeCorridor(List list, Random random, int type, int x, int y, int z, int width, int height, int cbm) {
		final int var10 = (this.getCoordBaseMode() + cbm) % 4;
		this.offsetCorridorCoords(x, y, z, width, var10);

		{
		}

		return true;
	}

	protected int[] offsetCorridorCoords(int x, int y, int z, int width, int cbm) {
		final int var6 = this.getXWithOffset(x, z);
		final int var7 = this.getYWithOffset(y);
		final int var8 = this.getZWithOffset(x, z);
		return cbm == 0 ? new int[] { var6 + 1, var7 - 1, var8 - width / 2 } : cbm == 1 ? new int[] { var6 + width / 2, var7 - 1, var8 + 1 } : cbm == 2 ? new int[] { var6 - 1, var7 - 1, var8 + width / 2 } : cbm == 3 ? new int[] { var6 - width / 2, var7 - 1, var8 - 1 } : new int[] { x, y, z };
	}

	@Override
	public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox) {
		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

			if (this.averageGroundLevel < 0) {
				return true;
			}

			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 3, 0);
		}

		this.makeWallsDown(par1World);
		this.makePlatforms(par1World, par2Random);

		return true;
	}

	public void makeWallsDown(World world) {
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < 7; x++) {
				for (int z = 0; z < 7; z++) {
					if ((x == 0 || x == 6 || z == 0 || z == 6) && (y == 0 || y > 7)) {
						this.placeBlockAtCurrentPosition(world, settings.blockBrick.getFirst(), settings.blockBrick.getSecond(), x, y, z, this.getBoundingBox());
					} else {
						this.placeBlockAtCurrentPosition(world, Blocks.air, 0, x, y, z, this.getBoundingBox());
					}
				}
			}
		}
	}

	public void makePlatforms(World world, Random rand) {
		for (int y = this.height - 1; y > 10; y--) {
			for (int x = 0; x < this.width; x++) {
				for (int z = 0; z < this.width; z++) {
					if (y % 4 == 0 && rand.nextInt(20) == 0) {
						if (world.getBlock(this.getBoundingBox().minX + x, this.getBoundingBox().minY + y, this.getBoundingBox().minZ + z) == settings.blockBrick.getFirst()) {
						}

						{
							for (int i = -2; i < 2; i++) {
								for (int j = -2; j < 2; j++) {
									if (world.getBlock(this.getBoundingBox().minX + x + i, this.getBoundingBox().minY + y, this.getBoundingBox().minZ + z + j) == Blocks.air) {
										this.placeBlockAtCurrentPosition(world, settings.blockBrick.getFirst(), settings.blockBrick.getSecond(), x + i, y, z + j, this.getBoundingBox());
									}

									if (y > 10) {
										this.placeBlockAtCurrentPosition(world, Blocks.air, 0, x - 2, y, z - 2, this.getBoundingBox());
										this.placeBlockAtCurrentPosition(world, Blocks.air, 0, x + 1, y, z - 2, this.getBoundingBox());
										this.placeBlockAtCurrentPosition(world, Blocks.air, 0, x - 2, y, z + 1, this.getBoundingBox());
										this.placeBlockAtCurrentPosition(world, Blocks.air, 0, x + 1, y, z + 1, this.getBoundingBox());
									}

									if (rand.nextInt(5) == 0 && world.getBlock(this.getBoundingBox().minX + x + i, this.getBoundingBox().minY + y + 1, this.getBoundingBox().minZ + z + j) == Blocks.air && world.getBlock(this.getBoundingBox().minX + x + i, this.getBoundingBox().minY + y, this.getBoundingBox().minZ + z + j) == settings.blockBrick.getFirst()) {
										this.placeBlockAtCurrentPosition(world, settings.blockEgg, 0, x + i, y + 1, z + j, this.getBoundingBox());
									}
								}
							}

							if (rand.nextInt(7) == 0) {
								if (x > 0 && x < 7 && z > 0 && z < 7) {
									if (world.getBlock(this.getBoundingBox().minX + x, this.getBoundingBox().minY + y + 1, this.getBoundingBox().minZ + z) == Blocks.air) {
										this.placeBlockAtCurrentPosition(world, settings.blockEgg, 0, x, y + 2, z, this.getBoundingBox());
									}

									final TileEntityMobSpawner var7 = (TileEntityMobSpawner) world.getTileEntity(this.getBoundingBox().minX + x, this.getBoundingBox().minY + y + 1, this.getBoundingBox().minZ + z);

									if (var7 != null) {
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected int getAverageGroundLevel(World par1World, StructureBoundingBox par2StructureBoundingBox) {
		int var3 = 0;
		int var4 = 0;

		for (int var5 = this.boundingBox.minZ; var5 <= this.boundingBox.maxZ; ++var5) {
			for (int var6 = this.boundingBox.minX; var6 <= this.boundingBox.maxX; ++var6) {
				if (par2StructureBoundingBox.isVecInside(var6, 64, var5)) {
					var3 += Math.max(par1World.getTopSolidOrLiquidBlock(var6, var5), par1World.provider.getAverageGroundLevel());
					++var4;
				}
			}
		}

		if (var4 == 0) {
			return -1;
		} else {
			return var3 / var4;
		}
	}

	@Override
	protected void func_143012_a(NBTTagCompound nbttagcompound) {
	}

	@Override
	protected void func_143011_b(NBTTagCompound nbttagcompound) {
	}
}
