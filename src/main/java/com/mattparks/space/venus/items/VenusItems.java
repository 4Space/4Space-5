package com.mattparks.space.venus.items;

import com.mattparks.space.core.builder.ICoreItems;
import net.minecraftforge.common.util.EnumHelper;
import com.mattparks.space.core.items.ItemBasics;
import com.mattparks.space.core.items.ItemBattery;
import com.mattparks.space.core.items.ItemFullArmour;
import com.mattparks.space.venus.VenusCore;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class VenusItems implements ICoreItems {
	public static ItemBasics venusItem;
	public static ItemBattery batteryUranium;
	public static ItemBattery batterySulfur;
	public static ItemFullArmour armourRuby;
	public static ItemFullArmour armourSulfur;

	public static final ToolMaterial TOOL_RUBY = EnumHelper.addToolMaterial("ruby", 3, 768, 5.0f, 2, 8);
	public static final ToolMaterial TOOL_SULFUR = EnumHelper.addToolMaterial("sulfur", 3, 416, 6.0f, 1, 2);

	@Override
	public void initItems() {
		venusItem = new ItemBasics(VenusCore.instance.prefixTexture, new String[] {
			"sulfurDust", 
			"ingotSulfur",
			"ruby",
			"ingotUranium",
			"venusCrystal",
			"sulfurPlate",
			"rubyPlate",
			"uraniumPlate",
			"tier4HeavyDutyPlate",
			"venusRod",
			"venusRodDust",
			"venusPowerCrystal",
		});
		batteryUranium = new ItemBattery(VenusCore.instance.prefixTexture, "batteryUranium", 75000.0f);
		batterySulfur = new ItemBattery(VenusCore.instance.prefixTexture, "batterySulfur", 17500.0f);
		armourRuby = new ItemFullArmour(VenusCore.instance.prefixTexture, EnumHelper.addArmorMaterial("ruby", 30, new int[] { 3, 8, 6, 3 }, 12));
		armourSulfur = new ItemFullArmour(VenusCore.instance.prefixTexture, EnumHelper.addArmorMaterial("sulfur", 15, new int[] { 1, 4, 2, 1 }, 7));
	}

	@Override
	public void registerItems() {
		venusItem.registerItem();
		batteryUranium.registerItem();
		batterySulfur.registerItem();
		armourRuby.registerItem();
		armourSulfur.registerItem();
	}

	@Override
	public void registerHarvestLevels() {
	}
}
