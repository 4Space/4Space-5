package com.mattparks.space.core.items;

import com.mattparks.space.core.SpaceCore;
import com.mattparks.space.core.proxy.ClientProxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

/**
 * A class used for creating battery's.
 */
public class ItemBattery extends ItemElectricBase {
	private float maxEnergy;
	
	public ItemBattery(String texturePrefix, String assetName, float maxEnergy) {
		super();
		this.setUnlocalizedName(assetName);
		this.setTextureName(texturePrefix + assetName);
		this.maxEnergy = maxEnergy;
	}
	
	public void registerItem() {
		SpaceCore.registerItem(this, true);
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return SpaceCore.spaceItemsTab;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return ClientProxy.RARITY_SPACE_ITEM;
	}

	@Override
	public float getMaxElectricityStored(ItemStack itemStack) {
		return maxEnergy;
	}
}
