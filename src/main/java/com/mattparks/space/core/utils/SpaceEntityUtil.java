package com.mattparks.space.core.utils;

import com.mattparks.space.core.SpaceCore;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;

/**
 * A class used to create and register entity types.
 */
public class SpaceEntityUtil {
	private static int nextID = 0;

	private static int nextInternalID() {
		nextID++;
		return nextID - 1;
	}

	/**
	 * Registers a space creature.
	 * 
	 * @param var0
	 * @param var1
	 * @param back
	 * @param fore
	 */
	public static void registerSpaceCreature(Class<? extends Entity> var0, String var1, int back, int fore) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			LanguageRegistry.instance().addStringLocalization("entity." + var1 + ".name", SpaceTranslate.translate("entity.spaceCore." + var1 + ".name"));
		}

		int newID = EntityRegistry.instance().findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(var0, var1, newID, back, fore);
		EntityRegistry.registerModEntity(var0, var1, nextInternalID(), SpaceCore.instance, 80, 3, true);
	}

	/**
	 * Registers a non-mob entity.
	 * 
	 * @param var0
	 * @param var1
	 * @param trackingDistance
	 * @param updateFreq
	 * @param sendVel
	 */
	public static void registerSpaceNonMobEntity(Class<? extends Entity> var0, String var1, int trackingDistance, int updateFreq, boolean sendVel) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			LanguageRegistry.instance().addStringLocalization("entity." + var1 + ".name", SpaceTranslate.translate("entity.spaceCore." + var1 + ".name"));
		}

		EntityRegistry.registerModEntity(var0, var1, nextInternalID(), SpaceCore.instance, trackingDistance, updateFreq, sendVel);
	}
}
