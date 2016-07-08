package com.mattparks.space.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.mattparks.space.core.Constants;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mekanism.api.EnumColor;
import net.minecraft.util.ChatComponentText;

/**
 * A version checker for 4Space.
 */
public class SpaceVersionCheck extends Thread {
	public static SpaceVersionCheck INSTANCE = new SpaceVersionCheck();
	private int count = 0;

	public static int remoteMajVer;
	public static int remoteMinVer;
	public static int remoteBuildVer;

	/**
	 * Creates a new thread for a version checker.
	 */
	public SpaceVersionCheck() {
		super(Constants.MOD_NAME + " Version Check Thread");
	}

	/**
	 * Starts the version check thread.
	 */
	public static void startCheck() {
		Thread thread = new Thread(SpaceVersionCheck.INSTANCE);
		thread.start();
	}

	@Override
	public void run() {
		final Side sideToCheck = FMLCommonHandler.instance().getSide();

		if (sideToCheck == null) {
			return;
		}

		while (this.count <= 3 && remoteBuildVer == 0) {
			BufferedReader in = null;
			try {
				URL url = new URL(Constants.MOD_WEBSITE + "/" + Constants.VERSION_PAGE);
				HttpURLConnection http = (HttpURLConnection) url.openConnection();
				http.addRequestProperty("User-Agent", "Mozilla/4.76");
				InputStreamReader streamReader = new InputStreamReader(http.getInputStream());
				in = new BufferedReader(streamReader);
				String str;
				String str2[] = null;

				while ((str = in.readLine()) != null) {
					if (str.contains("Version")) {
						str = str.replace("Version=", "");

						str2 = str.split("#");

						if (str2.length == 3) {
							remoteMajVer = Integer.parseInt(str2[0]);
							remoteMinVer = Integer.parseInt(str2[1]);
							remoteBuildVer = Integer.parseInt(str2[2]);
						}

						if (remoteMajVer == Constants.LOCALMAJVERSION && (remoteMinVer > Constants.LOCALMINVERSION || (remoteMinVer == Constants.LOCALMINVERSION && remoteBuildVer > Constants.LOCALBUILDVERSION))) {
							Thread.sleep(5000);

							if (sideToCheck.equals(Side.CLIENT)) {
								FMLClientHandler.instance().getClient().thePlayer.addChatMessage(new ChatComponentText(EnumColor.GREY + "New " + EnumColor.DARK_AQUA + Constants.MOD_NAME + EnumColor.GREY + " version available! v" + String.valueOf(remoteMajVer) + "." + String.valueOf(remoteMinVer) + "." + String.valueOf(remoteBuildVer) + EnumColor.DARK_BLUE + " " + Constants.MOD_WEBSITE));
							} else if (sideToCheck.equals(Side.SERVER)) {
								String message = "New " + Constants.MOD_NAME + " version available! v" + String.valueOf(remoteMajVer) + "." + String.valueOf(remoteMinVer) + "." + String.valueOf(remoteBuildVer) + " " + Constants.MOD_WEBSITE;
								SpaceLog.severe(message);
							}
						}
					}
				}

				in.close();
				streamReader.close();
			} catch (Exception e) {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

			if (remoteBuildVer == 0) {
				try {
					String message = SpaceTranslate.translate("updater.space.failed.name");
					SpaceLog.info(message);
					Thread.sleep(15000);
				} catch (final InterruptedException e) {
				}
			} else {
				String message = SpaceTranslate.translate("updater.space.success.name") + " " + remoteMajVer + "." + remoteMinVer + "." + remoteBuildVer;
				SpaceLog.info(message);
			}

			this.count++;
		}
	}
}
