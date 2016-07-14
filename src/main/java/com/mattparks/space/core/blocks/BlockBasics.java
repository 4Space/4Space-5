package com.mattparks.space.core.blocks;

import java.util.List;

import com.mattparks.space.core.SpaceCore;
import com.mattparks.space.core.proxy.ClientProxy;
import com.mattparks.space.venus.VenusCore;
import com.mattparks.space.venus.blocks.VenusBlocks;

import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock;
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * 
 */
public class BlockBasics extends Block implements IDetectableResource, IPartialSealableBlock, IPlantableBlock, ITerraformableBlock {
	private IIcon[] blockIcon;
	
	private BasicBlock[] blocks;

	public BlockBasics(String name, BasicBlock[] blocks) {
		super(Material.rock);
		this.setBlockName(name);
		this.blocks = blocks;
	}

	public void registerBasicBlocks() {
		SpaceCore.registerBlock(VenusBlocks.venusBasicBlock, ItemBlockUtil.class, blocks.length, true);
	}

	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		blockIcon = new IIcon[blocks.length];
		
		for (int i = 0; i < blocks.length; i++) {
			blockIcon[i] = par1IconRegister.registerIcon(VenusCore.TEXTURE_PREFIX + blocks[i].getType());
		}
	}

	@Override
	public CreativeTabs getCreativeTabToDisplayOn() {
		return SpaceCore.spaceBlocksTab;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return this.blockIcon[meta];
	}

	@Override
	public void getSubBlocks(Item block, CreativeTabs creativeTabs, List list) {
		for (int i = 0; i < blocks.length; ++i) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public float getBlockHardness(World par1World, int par2, int par3, int par4) {
		int meta = par1World.getBlockMetadata(par2, par3, par4);
		return blocks[meta].getHardness();
	}

	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		int meta = world.getBlockMetadata(x, y, z);
		float resistance = blocks[meta].getResistance();
		
		if (resistance != -1) {
			return resistance;
		} else {
			return super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
		}
	}

	@Override
	public MapColor getMapColor(int meta) {
		return MapColor.redColor;
	}

	@Override
	public int damageDropped(int meta) {
		String damagedDrop = blocks[meta].getDamagedDrop();
		
		if (damagedDrop != null) {
			int index = getIndex(damagedDrop);
			return index == -1 ? meta : index;
		}

		return meta;
	}
	
	public int getIndex(String blockName) {
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i].getType().equals(blockName)) {
				return i;
			}
		} 
		
		return -1;
	}

	@Override
	public boolean isTerraformable(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int requiredLiquidBlocksNearby() {
		return 4;
	}

	@Override
	public boolean isPlantable(int metadata) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSealed(World world, int x, int y, int z, ForgeDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValueable(int metadata) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 */
	public interface BasicBlock {
		public String getType();
		public float getHardness();
		public float getResistance();
		public String getDamagedDrop();
	}
}
