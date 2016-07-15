package com.mattparks.space.core.items;

import com.mattparks.space.core.SpaceCore;
import com.mattparks.space.core.proxy.ClientProxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

/**
 * A class for creating a basic set of tools.
 */
public class ItemSetTools {
	private String texturePrefix;
	private ToolMaterial material;

	public ItemSwordBasic sword;
	public ItemPickaxeBasic pickaxe;
	public ItemSpadeBasic spade;
	public ItemAxeBasic axe;
	public ItemHoeBasic hoe;
	
	public ItemSetTools(String texturePrefix, ToolMaterial material) {
		this.texturePrefix = texturePrefix;
		this.material = material;
		
		this.sword = new ItemSwordBasic(material.name() + "Sword", material);
		this.pickaxe = new ItemPickaxeBasic(material.name() + "Pickaxe", material);
		this.spade = new ItemSpadeBasic(material.name() + "Spade", material);
		this.axe = new ItemAxeBasic(material.name() + "Axe", material);
		this.hoe = new ItemHoeBasic(material.name() + "Hoe", material);
	}

	public void registerItem() {
		SpaceCore.registerItem(sword, true);
		SpaceCore.registerItem(pickaxe, true);
		SpaceCore.registerItem(spade, true);
		SpaceCore.registerItem(axe, true);
		SpaceCore.registerItem(hoe, true);
	}

	public class ItemAxeBasic extends ItemAxe {
		public ItemAxeBasic(String name, ToolMaterial material) {
			super(material);
			this.setUnlocalizedName(name);
		}
	
		@Override
		public CreativeTabs getCreativeTab() {
			return SpaceCore.spaceItemsTab;
		}
	
		@Override
		public EnumRarity getRarity(ItemStack par1ItemStack) {
			return ClientProxy.RARITY_SPACE_ITEM;
		}
	
		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister par1IconRegister) {
			this.itemIcon = par1IconRegister.registerIcon(this.getUnlocalizedName().replace("item.", texturePrefix));
		}
	}
	
	public class ItemHoeBasic extends ItemHoe {
		public ItemHoeBasic(String name, ToolMaterial material) {
			super(material);
			this.setUnlocalizedName(name);
		}
	
		@Override
		public CreativeTabs getCreativeTab() {
			return SpaceCore.spaceItemsTab;
		}
	
		@Override
		public EnumRarity getRarity(ItemStack par1ItemStack) {
			return ClientProxy.RARITY_SPACE_ITEM;
		}
	
		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister par1IconRegister) {
			this.itemIcon = par1IconRegister.registerIcon(this.getUnlocalizedName().replace("item.", texturePrefix));
		}
	}
	
	public class ItemPickaxeBasic extends ItemPickaxe {
		public ItemPickaxeBasic(String name, ToolMaterial material) {
			super(material);
			this.setUnlocalizedName(name);
		}
	
		@Override
		public CreativeTabs getCreativeTab() {
			return SpaceCore.spaceItemsTab;
		}
	
		@Override
		public EnumRarity getRarity(ItemStack par1ItemStack) {
			return ClientProxy.RARITY_SPACE_ITEM;
		}
	
		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister par1IconRegister) {
			this.itemIcon = par1IconRegister.registerIcon(this.getUnlocalizedName().replace("item.", texturePrefix));
		}
	}
	
	public class ItemSpadeBasic extends ItemSpade {
		public ItemSpadeBasic(String name, ToolMaterial material) {
			super(material);
			this.setUnlocalizedName(name);
		}
	
		@Override
		public CreativeTabs getCreativeTab() {
			return SpaceCore.spaceItemsTab;
		}
	
		@Override
		public EnumRarity getRarity(ItemStack par1ItemStack) {
			return ClientProxy.RARITY_SPACE_ITEM;
		}
	
		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister par1IconRegister) {
			this.itemIcon = par1IconRegister.registerIcon(this.getUnlocalizedName().replace("item.", texturePrefix));
		}
	}
	public class ItemSwordBasic extends ItemSword {
		public ItemSwordBasic(String name, ToolMaterial material) {
			super(material);
			this.setUnlocalizedName(name);
		}
	
		@Override
		public CreativeTabs getCreativeTab() {
			return SpaceCore.spaceItemsTab;
		}
	
		@Override
		public EnumRarity getRarity(ItemStack par1ItemStack) {
			return ClientProxy.RARITY_SPACE_ITEM;
		}
	
		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister par1IconRegister) {
			this.itemIcon = par1IconRegister.registerIcon(this.getUnlocalizedName().replace("item.", texturePrefix));
		}
	}
}
