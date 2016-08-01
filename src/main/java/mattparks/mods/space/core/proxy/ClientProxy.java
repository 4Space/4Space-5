package mattparks.mods.space.core.proxy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mattparks.mods.space.core.Constants;
import mattparks.mods.space.core.music.MusicHandlerClient;
import mattparks.mods.space.core.tick.TickHandlerClient;
import mattparks.mods.space.core.utils.SpaceLog;
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import micdoodle8.mods.galacticraft.core.client.render.ThreadDownloadImageDataGC;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.VersionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.EnumHelper;

/**
 * 4Spaces main client proxy.
 */
public class ClientProxy extends CommonProxy {
	public static final EnumRarity RARITY_SPACE_ITEM = EnumHelper.addRarity("SpaceRarity", EnumChatFormatting.RED, Constants.MOD_NAME);
	public static final Minecraft MC_INSTANCE = FMLClientHandler.instance().getClient();
	public static final MusicHandlerClient MUSIC_HANDLER_SPACE = new MusicHandlerClient(Minecraft.getMinecraft());
	
	private static Map<String, ResourceLocation> capesMap = Maps.newHashMap();
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new TickHandlerClient());
		setupCapes();
		super.init(event);
	}

	private static void setupCapes() {
		try {
			int timeout = 10000;
			URL capeListUrl = null;

			try {
				capeListUrl = new URL(Constants.CAPE_INFO_TXT);
			} catch (MalformedURLException e) {
				SpaceLog.severe("Error getting " + Constants.MOD_NAME + " capes list URL.");
				e.printStackTrace();
				return;
			}

			URLConnection connection = null;

			try {
				connection = capeListUrl.openConnection();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			InputStream stream = null;

			try {
				stream = connection.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			InputStreamReader streamReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(streamReader);

			String line;

			try {
				while ((line = reader.readLine()) != null) {
					if (line.contains(":")) {
						int splitLocation = line.indexOf(":");
						String username = line.substring(0, splitLocation);
						String capeUrl = Constants.CAPE_RAW_IMAGES + line.substring(splitLocation + 1) + ".png";
						ClientProxyCore.capeMap.put(username, capeUrl);
					}
				}
				
				reader.close();
				streamReader.close();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			FMLLog.severe("Error while setting up " + Constants.MOD_NAME + " capes");
			e.printStackTrace();
		}

		/*
		 * if (Loader.isModLoaded("CoFHCore")) { for (Entry<String, String> e :
		 * ClientProxy.capeMap.entrySet()) { try { Object capeRegistry =
		 * Class.forName
		 * ("cofh.api.core.RegistryAccess").getField("capeRegistry").get(null);
		 * Class.forName("cofh.api.core.ISimpleRegistry").getMethod("register",
		 * String.class, String.class).invoke(capeRegistry, e.getKey(),
		 * e.getValue()); } catch (Exception e1) { e1.printStackTrace(); break;
		 * } } }
		 */
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		try {
			Field ftc = Minecraft.getMinecraft().getClass().getDeclaredField(VersionUtil.getNameDynamic(VersionUtil.KEY_FIELD_MUSICTICKER));
			ftc.setAccessible(true);
			ftc.set(Minecraft.getMinecraft(), MUSIC_HANDLER_SPACE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.postInit(event);
	}

	@Override
	public void registerRenderInfo() {
	}

	@SubscribeEvent
	public void onPostRender(RenderPlayerEvent.Specials.Post event) {
		renderCapes(event);
	}
	
	private void renderCapes(RenderPlayerEvent.Specials.Post event) {
		AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;
		boolean flag = ClientProxyCore.capeMap.containsKey(event.entityPlayer.getCommandSenderName());
		float f4;

		if (flag && !player.isInvisible() && !player.getHideCape()) {
			String url = ClientProxyCore.capeMap.get(player.getCommandSenderName());
			ResourceLocation capeLoc = capesMap.get(url);
			
			if (!capesMap.containsKey(url)) {
				try {
					String dirName = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
					File directory = new File(dirName, "assets");
					boolean success = true;

					if (!directory.exists()) {
						success = directory.mkdir();
					}

					if (success) {
						directory = new File(directory, Constants.CAPES_DIR);
						if (!directory.exists()) {
							success = directory.mkdir();
						}

						if (success) {
							String hash = String.valueOf(player.getCommandSenderName().hashCode());
							File file1 = new File(directory, hash.substring(0, 2));
							File file2 = new File(file1, hash);
							final ResourceLocation resourcelocation = new ResourceLocation(Constants.CAPES_DIR + "/" + hash);
							
							ThreadDownloadImageDataGC threaddownloadimagedata = new ThreadDownloadImageDataGC(file2, url, null, new IImageBuffer() {
								public BufferedImage parseUserSkin(BufferedImage p_78432_1_) {
									if (p_78432_1_ == null) {
										return null;
									} else {
										BufferedImage bufferedimage1 = new BufferedImage(512, 256, 2);
										Graphics graphics = bufferedimage1.getGraphics();
										graphics.drawImage(p_78432_1_, 0, 0, null);
										graphics.dispose();
										p_78432_1_ = bufferedimage1;
									}
									
									return p_78432_1_;
								}

								public void func_152634_a() {
								}
							});

							if (MC_INSTANCE.getTextureManager().loadTexture(resourcelocation, threaddownloadimagedata)) {
								capeLoc = resourcelocation;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				capesMap.put(url, capeLoc);
			}

			if (capeLoc != null) {
				MC_INSTANCE.getTextureManager().bindTexture(capeLoc);
				GL11.glPushMatrix();
				GL11.glTranslatef(0.0f, 0.0f, 0.125f);
				double d3 = player.field_71091_bM + (player.field_71094_bP - player.field_71091_bM) * event.partialRenderTick - (player.prevPosX + (player.posX - player.prevPosX) * event.partialRenderTick);
				double d4 = player.field_71096_bN + (player.field_71095_bQ - player.field_71096_bN) * event.partialRenderTick - (player.prevPosY + (player.posY - player.prevPosY) * event.partialRenderTick);
				double d0 = player.field_71097_bO + (player.field_71085_bR - player.field_71097_bO) * event.partialRenderTick - (player.prevPosZ + (player.posZ - player.prevPosZ) * event.partialRenderTick);
				f4 = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * event.partialRenderTick) / 57.29578F;
				double d1 = MathHelper.sin(f4);
				double d2 = -MathHelper.cos(f4);
				float f5 = (float) d4 * 10.0f;

				if (f5 < -6.0f) {
					f5 = -6.0f;
				}

				if (f5 > 32.0f) {
					f5 = 32.0f;
				}

				float f6 = (float) (d3 * d1 + d0 * d2) * 100.0f;
				float f7 = (float) (d3 * d2 - d0 * d1) * 100.0f;

				if (f6 < 0.0f) {
					f6 = 0.0f;
				}

				float f8 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * event.partialRenderTick;
				f5 += MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * event.partialRenderTick) * 6.0f) * 32.0f * f8;

				if (player.isSneaking()) {
					f5 += 25.0f;
				}

				GL11.glRotatef(6.0f + f6 / 2.0f + f5, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(f7 / 2.0f, 0.0f, 0.0F, 1.0f);
				GL11.glRotatef(-f7 / 2.0f, 0.0f, 1.0F, 0.0f);
				GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
				event.renderer.modelBipedMain.renderCloak(0.0625f);
				GL11.glPopMatrix();
			}
		}
	}

	@SubscribeEvent
	public void onRenderPlanetPost(CelestialBodyRenderEvent.Post event) {
	}
}
