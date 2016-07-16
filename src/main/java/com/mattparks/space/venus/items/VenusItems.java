package com.mattparks.space.venus.items;

import com.mattparks.space.core.builder.ICoreItems;
import com.mattparks.space.core.items.ItemBasics;
import com.mattparks.space.core.items.ItemBattery;
import com.mattparks.space.core.items.ItemSetArmour;
import com.mattparks.space.core.items.ItemSetTools;
import com.mattparks.space.venus.VenusCore;

import net.minecraftforge.common.util.EnumHelper;

public class VenusItems implements ICoreItems {
	public static ItemBasics venusItem;
	public static ItemBattery batteryUranium;
	public static ItemBattery batterySulfur;
	public static ItemSetArmour armourRuby;
	public static ItemSetArmour armourSulfur;
	public static ItemSetTools toolsRuby;
	public static ItemSetTools toolsSulfur;

	@Override
	public void initItems() {
		venusItem = new ItemBasics(VenusCore.instance.prefixTexture, new String[] {
			"sulfurDust", 
			"ingotSulfur",
			"ruby",
			"ingotUranium",
			"pellucidum",
			"sulfurPlate",
			"rubyPlate",
			"uraniumPlate",
			"tier4HeavyDutyPlate",
			"venusRod",
			"venusRodDust",
			"powerPellucidum",
			"ventBerry",
		});
		batteryUranium = new ItemBattery(VenusCore.instance.prefixTexture, "batteryUranium", 75000.0f);
		batterySulfur = new ItemBattery(VenusCore.instance.prefixTexture, "batterySulfur", 17500.0f);
		armourRuby = new ItemSetArmour(VenusCore.instance.prefixTexture, EnumHelper.addArmorMaterial("ruby", 30, new int[] { 3, 8, 6, 3 }, 12));
		armourSulfur = new ItemSetArmour(VenusCore.instance.prefixTexture, EnumHelper.addArmorMaterial("sulfur", 15, new int[] { 1, 4, 2, 1 }, 7));
		toolsRuby = new ItemSetTools(VenusCore.instance.prefixTexture, EnumHelper.addToolMaterial("ruby", 3, 768, 5.0f, 2, 8));
		toolsSulfur = new ItemSetTools(VenusCore.instance.prefixTexture, EnumHelper.addToolMaterial("sulfur", 3, 416, 6.0f, 1, 2));
	}

	@Override
	public void registerItems() {
		venusItem.registerItem();
		batteryUranium.registerItem();
		batterySulfur.registerItem();
		armourRuby.registerItem();
		armourSulfur.registerItem();
		toolsRuby.registerItem();
		toolsSulfur.registerItem();
	}

	@Override
	public void registerHarvestLevels() {
	}
}
