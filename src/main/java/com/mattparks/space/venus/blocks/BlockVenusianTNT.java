package com.mattparks.space.venus.blocks;

import java.util.Random;

import com.mattparks.space.core.SpaceCore;
import com.mattparks.space.core.blocks.ItemBlockUtil;
import com.mattparks.space.venus.VenusCore;
import com.mattparks.space.venus.entities.EntityVenusianTNT;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockVenusianTNT extends Block {
	private String name;
	private IIcon TNTTop;
	private IIcon TNTBottom;
	private IIcon TNTSide;

	public BlockVenusianTNT(String name) {
		super(Material.tnt);
		this.setStepSound(Block.soundTypeGrass);
		this.setHardness(0.3f);
		this.setBlockName(this.name = name);
	}

	public void registerBlock() {
		SpaceCore.registerBlock(this, ItemBlockUtil.class, 0, true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int par1, int par2) {
		return par1 == 0 ? this.TNTBottom : par1 == 1 ? this.TNTTop : this.TNTSide;
	}

	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		super.onBlockAdded(par1World, par2, par3, par4);

		if (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
			this.onBlockDestroyedByPlayer(par1World, par2, par3, par4, 1);
			par1World.setBlockToAir(par2, par3, par4);
		}
	}

	@Override
	public CreativeTabs getCreativeTabToDisplayOn() {
		return SpaceCore.spaceBlocksTab;
	}

	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
		if (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
			this.onBlockDestroyedByPlayer(par1World, par2, par3, par4, 1);
			par1World.setBlockToAir(par2, par3, par4);
		}
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}

	@Override
	public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion) {
		if (!par1World.isRemote) {
			final EntityVenusianTNT entitytntprimed = new EntityVenusianTNT(par1World, par2 + 0.5f, par3 + 0.5f, par4 + 0.5f, par5Explosion.getExplosivePlacedBy());
			entitytntprimed.fuse = par1World.rand.nextInt(entitytntprimed.fuse / 4) + entitytntprimed.fuse / 8;
			par1World.spawnEntityInWorld(entitytntprimed);
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {
		this.primeTnt(par1World, par2, par3, par4, par5, (EntityLivingBase) null);
	}

	public void primeTnt(World par1World, int par2, int par3, int par4, int par5, EntityLivingBase par6EntityLivingBase) {
		if (!par1World.isRemote) {
			if ((par5 & 1) == 1) {
				final EntityVenusianTNT entitytntprimed = new EntityVenusianTNT(par1World, par2 + 0.5f, par3 + 0.5f, par4 + 0.5f, par6EntityLivingBase);
				par1World.spawnEntityInWorld(entitytntprimed);
				par1World.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0f, 1.0f);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		if (par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel) {
			this.primeTnt(par1World, par2, par3, par4, 1, par5EntityPlayer);
			par1World.setBlockToAir(par2, par3, par4);
			par5EntityPlayer.getCurrentEquippedItem().damageItem(1, par5EntityPlayer);
			return true;
		}
		
		return super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
	}

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
		if (par5Entity instanceof EntityArrow && !par1World.isRemote) {
			final EntityArrow entityarrow = (EntityArrow) par5Entity;

			if (entityarrow.isBurning()) {
				this.primeTnt(par1World, par2, par3, par4, 1, entityarrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) entityarrow.shootingEntity : null);
				par1World.setBlockToAir(par2, par3, par4);
			}
		}
	}

	@Override
	public boolean canDropFromExplosion(Explosion par1Explosion) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.TNTSide = par1IconRegister.registerIcon(VenusCore.instance.prefixTexture + name + "Side");
		this.TNTTop = par1IconRegister.registerIcon(VenusCore.instance.prefixTexture + name + "Top");
		this.TNTBottom = par1IconRegister.registerIcon(VenusCore.instance.prefixTexture + name + "Bottom");
	}
}
