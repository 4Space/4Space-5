package com.mattparks.space.core.tick;

import com.mattparks.space.core.SpaceCore;
import com.mattparks.space.core.builder.ICoreModule;
import com.mattparks.space.core.builder.celestials.ICoreCelestial;
import com.mattparks.space.core.utils.SpaceVersionCheck;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.client.CloudRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;

/**
 * A tick handler run from the client side.
 */
public class TickHandlerClient {
	public static boolean checkedVersion = true;

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		Minecraft minecraft = FMLClientHandler.instance().getClient();
		WorldClient world = minecraft.theWorld;
		EntityClientPlayerMP player = minecraft.thePlayer;

		// Starts a version check.
		if (event.phase == Phase.START) {
			if (world != null && TickHandlerClient.checkedVersion) {
				SpaceVersionCheck.startCheck();
				TickHandlerClient.checkedVersion = false;
			}
		}

		// Sets up the atmosphere for the world.
		if (world != null) {
			for (ICoreModule module : SpaceCore.modulesList) {
				if (module instanceof ICoreCelestial) {
					ICoreCelestial celestial = (ICoreCelestial) module;
					
					if (celestial.instanceOfProvider(world.provider)) {
						if (world.provider.getSkyRenderer() == null) {
							world.provider.setSkyRenderer(celestial.createSkyProvider((IGalacticraftWorldProvider) world.provider));
						}

						if (world.provider.getCloudRenderer() == null) {
							world.provider.setCloudRenderer(new CloudRenderer());
						}	
						
						break;
					}
				}
			}
		}
	}
}
