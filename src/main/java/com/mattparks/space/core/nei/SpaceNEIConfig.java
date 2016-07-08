package com.mattparks.space.core.nei;

import com.mattparks.space.core.Constants;
import com.mattparks.space.core.SpaceCore;

import codechicken.nei.api.IConfigureNEI;

/**
 * A NEI config loader for 4Space.
 */
public class SpaceNEIConfig implements IConfigureNEI {
	@Override
	public void loadConfig() {
		SpaceCore.registerHideNEI();
	}

	@Override
	public String getName() {
		return Constants.MOD_NAME + " NEI-Plugin";
	}

	@Override
	public String getVersion() {
		return Constants.VERSION;
	}
}
