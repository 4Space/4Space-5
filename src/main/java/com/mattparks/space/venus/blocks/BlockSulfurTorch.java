package com.mattparks.space.venus.blocks;

import java.util.Random;

import com.mattparks.space.core.SpaceCore;
import com.mattparks.space.core.blocks.ItemBlockUtil;
import com.mattparks.space.venus.VenusCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSulfurTorch extends BlockTorch {
	private String name;
	
	public BlockSulfurTorch(String name) {
		super();
		this.setTickRandomly(true);
		this.setLightLevel(1.0f);
		this.setStepSound(Block.soundTypeWood);
		this.setBlockName(this.name = name);
	}
	
	public void registerBlock() {
		SpaceCore.registerBlock(this, ItemBlockUtil.class, 0, true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon(VenusCore.instance.prefixTexture + name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTabToDisplayOn() {
		return SpaceCore.spaceBlocksTab;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	private boolean canPlaceTorchOn(World par1World, int par2, int par3, int par4) {
		if (World.doesBlockHaveSolidTopSurface(par1World, par2, par3, par4)) {
			return true;
		} else {
			final Block block = par1World.getBlock(par2, par3, par4);
			return block != null && block.canPlaceTorchOnTop(par1World, par2, par3, par4);
		}
	}

	@Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
		return par1World.isSideSolid(par2 - 1, par3, par4, ForgeDirection.EAST, true) || par1World.isSideSolid(par2 + 1, par3, par4, ForgeDirection.WEST, true) || par1World.isSideSolid(par2, par3, par4 - 1, ForgeDirection.SOUTH, true) || par1World.isSideSolid(par2, par3, par4 + 1, ForgeDirection.NORTH, true) || this.canPlaceTorchOn(par1World, par2, par3 - 1, par4);
	}

	@Override
	public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9) {
		int j1 = par9;

		if (par5 == 1 && this.canPlaceTorchOn(par1World, par2, par3 - 1, par4)) {
			j1 = 5;
		}
		
		if (par5 == 2 && par1World.isSideSolid(par2, par3, par4 + 1, ForgeDirection.NORTH, true)) {
			j1 = 4;
		}
		
		if (par5 == 3 && par1World.isSideSolid(par2, par3, par4 - 1, ForgeDirection.SOUTH, true)) {
			j1 = 3;
		}
		
		if (par5 == 4 && par1World.isSideSolid(par2 + 1, par3, par4, ForgeDirection.WEST, true)) {
			j1 = 2;
		}
		
		if (par5 == 5 && par1World.isSideSolid(par2 - 1, par3, par4, ForgeDirection.EAST, true)) {
			j1 = 1;
		}
		
		return j1;
	}

	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
		super.updateTick(par1World, par2, par3, par4, par5Random);

		if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
			this.onBlockAdded(par1World, par2, par3, par4);
		}
	}

	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
			if (par1World.isSideSolid(par2 - 1, par3, par4, ForgeDirection.EAST, true)) {
				par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);
			} else if (par1World.isSideSolid(par2 + 1, par3, par4, ForgeDirection.WEST, true)) {
				par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
			} else if (par1World.isSideSolid(par2, par3, par4 - 1, ForgeDirection.SOUTH, true)) {
				par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
			} else if (par1World.isSideSolid(par2, par3, par4 + 1, ForgeDirection.NORTH, true)) {
				par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
			} else if (this.canPlaceTorchOn(par1World, par2, par3 - 1, par4)) {
				par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
			}
		}
		
		this.dropTorchIfCantStay(par1World, par2, par3, par4);
	}

	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
		if (this.dropTorchIfCantStay(par1World, par2, par3, par4)) {
			final int i1 = par1World.getBlockMetadata(par2, par3, par4);
			boolean flag = false;

			if (!par1World.isSideSolid(par2 - 1, par3, par4, ForgeDirection.EAST, true) && i1 == 1) {
				flag = true;
			}
			
			if (!par1World.isSideSolid(par2 + 1, par3, par4, ForgeDirection.WEST, true) && i1 == 2) {
				flag = true;
			}
			
			if (!par1World.isSideSolid(par2, par3, par4 - 1, ForgeDirection.SOUTH, true) && i1 == 3) {
				flag = true;
			}
			
			if (!par1World.isSideSolid(par2, par3, par4 + 1, ForgeDirection.NORTH, true) && i1 == 4) {
				flag = true;
			}
			
			if (!this.canPlaceTorchOn(par1World, par2, par3 - 1, par4) && i1 == 5) {
				flag = true;
			}
			
			if (flag) {
				this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
				par1World.setBlockToAir(par2, par3, par4);
			}
		}
	}

	protected boolean dropTorchIfCantStay(World par1World, int par2, int par3, int par4) {
		if (!this.canPlaceBlockAt(par1World, par2, par3, par4)) {
			if (par1World.getBlock(par2, par3, par4) == this) {
				this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
				par1World.setBlockToAir(par2, par3, par4);
			}
			
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
		final int l = par1World.getBlockMetadata(par2, par3, par4);
		final double d0 = par2 + 0.5f;
		final double d1 = par3 + 0.7f;
		final double d2 = par4 + 0.5f;
		final double d3 = 0.2199999988079071D;
		final double d4 = 0.27000001072883606D;

		if (l == 1) {
			par1World.spawnParticle("smoke", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			par1World.spawnParticle("flame", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
		} else if (l == 2) {
			par1World.spawnParticle("smoke", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			par1World.spawnParticle("flame", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
		} else if (l == 3) {
			par1World.spawnParticle("smoke", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
			par1World.spawnParticle("flame", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
		} else if (l == 4) {
			par1World.spawnParticle("smoke", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
			par1World.spawnParticle("flame", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
		} else {
			par1World.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
			par1World.spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3) {
		final int l = par1World.getBlockMetadata(par2, par3, par4) & 7;
		float f = 0.15f;

		if (l == 1) {
			this.setBlockBounds(0.0f, 0.2f, 0.5f - f, f * 2.0f, 0.8f, 0.5f + f);
		} else if (l == 2) {
			this.setBlockBounds(1.0f - f * 2.0f, 0.2f, 0.5f - f, 1.0f, 0.8f, 0.5f + f);
		} else if (l == 3) {
			this.setBlockBounds(0.5f - f, 0.2f, 0.0f, 0.5f + f, 0.8f, f * 2.0f);
		} else if (l == 4) {
			this.setBlockBounds(0.5f - f, 0.2f, 1.0f - f * 2.0f, 0.5f + f, 0.8f, 1.0f);
		} else {
			f = 0.1f;
			this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 0.6f, 0.5f + f);
		}
		
		return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
	}
}
