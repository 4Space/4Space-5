package com.mattparks.space.core.blocks;

import java.util.List;
import java.util.Random;

import com.mattparks.space.core.SpaceCore;

import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock;
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A class used to construct many blocks under one name.
 */
public class BlockBasics extends Block implements IDetectableResource, IPartialSealableBlock, IPlantableBlock, ITerraformableBlock {
	private IIcon[] blockIcon;
	
	private BlockBuilder[] blocks;
	private String texturePrefix;

	public BlockBasics(String name, String texturePrefix, BlockBuilder[] blocks) {
		super(Material.rock);
		this.setBlockName(name);
		this.texturePrefix = texturePrefix;
		this.blocks = blocks;
	}

	public void registerBlock() {
		BlockBasicsItem.BLOCK_TYPES.put(this, new String[blocks.length]);
		
		for (int i = 0; i < blocks.length; i++) {
			BlockBasicsItem.BLOCK_TYPES.get(this)[i] = blocks[i].type;
		}
		
		SpaceCore.registerBlock(this, BlockBasicsItem.class, blocks.length, true);
	}

	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		blockIcon = new IIcon[blocks.length];
		
		for (int i = 0; i < blocks.length; i++) {
			blockIcon[i] = par1IconRegister.registerIcon(texturePrefix + blocks[i].type);
		}
	}

	@Override
	public CreativeTabs getCreativeTabToDisplayOn() {
		return SpaceCore.spaceBlocksTab;
	}

    @Override
    public Item getItemDropped(int meta, Random random, int par3) {
    	return blocks[meta].dropItem == null ? Item.getItemFromBlock(this) : blocks[meta].dropItem;
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
	public float getBlockHardness(World par1World, int x, int y, int z) {
		int meta = par1World.getBlockMetadata(x, y, z);
		return blocks[meta].hardness;
	}

	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		int meta = world.getBlockMetadata(x, y, z);
		float resistance = blocks[meta].resistance;
		
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
		String damagedDrop = blocks[meta].damagedDrop;
		
		if (damagedDrop != null) {
			int index = getIndex(damagedDrop);
			return index == -1 ? meta : index;
		}

		return meta;
	}
	
	@Override
	public boolean isTerraformable(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
		return blocks[meta].isTerraformable;
	}

	@Override
	public int requiredLiquidBlocksNearby() {
		return 4;
	}

	@Override
	public boolean isPlantable(int meta) {
		return blocks[meta].isPlantable;
	}

	@Override
	public boolean isSealed(World world, int x, int y, int z, ForgeDirection direction) {
        int meta = world.getBlockMetadata(x, y, z);
		return blocks[meta].sealable;
	}

	@Override
	public boolean isValueable(int meta) {
		return blocks[meta].isValueable;
	}

	public int getIndex(String blockName) {
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i].type.equals(blockName)) {
				return i;
			}
		} 
		
		return -1;
	}
}
