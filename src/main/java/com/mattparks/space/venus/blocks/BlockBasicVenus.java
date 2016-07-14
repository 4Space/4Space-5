package com.mattparks.space.venus.blocks;

import java.util.List;

import com.mattparks.space.core.SpaceCore;
import com.mattparks.space.venus.VenusCore;
import com.mattparks.space.venus.blocks.items.ItemBlockBasicVenus.BlocksBasic;

import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock;
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBasicVenus extends Block {
	private IIcon[] venusBlockIcon;

	public BlockBasicVenus(String name) {
		super(Material.rock);
		this.setBlockName(name);
	}

	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		venusBlockIcon = new IIcon[BlocksBasic.values().length];
		
		for (int i = 0; i < BlocksBasic.values().length; i++) {
			venusBlockIcon[i] = par1IconRegister.registerIcon(VenusCore.TEXTURE_PREFIX + BlocksBasic.values()[i].type);
		}
	}

	@Override
	public CreativeTabs getCreativeTabToDisplayOn() {
		return SpaceCore.spaceBlocksTab;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return this.venusBlockIcon[meta];
	}

	@Override
	public void getSubBlocks(Item block, CreativeTabs creativeTabs, List list) {
		for (int i = 0; i < BlocksBasic.values().length; ++i) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public float getBlockHardness(World par1World, int par2, int par3, int par4) {
		int meta = par1World.getBlockMetadata(par2, par3, par4);
		return BlocksBasic.values()[meta].hardness;
	}

	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		int meta = world.getBlockMetadata(x, y, z);
		float resistance = BlocksBasic.values()[meta].resistance;
		
		if (resistance != -1) {
			return resistance;
		} else {
			return super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
		}
	}

	public MapColor getMapColor(int meta) {
		return MapColor.redColor;
	}

	@Override
	public int damageDropped(int meta) {
		BlocksBasic damagedDrop = BlocksBasic.values()[meta].damagedDrop;
		
		if (damagedDrop != null) {
			return damagedDrop.ordinal();
		}

		return meta;
	}
}
