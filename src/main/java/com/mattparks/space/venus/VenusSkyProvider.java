package com.mattparks.space.venus;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;

public class VenusSkyProvider extends IRenderHandler {
	private static final ResourceLocation sunTexture = new ResourceLocation("textures/environment/sun.png");

	public int starList;
	public int glSkyList;
	public int glSkyList2;

	public VenusSkyProvider(IGalacticraftWorldProvider provider) {
		int displayLists = GLAllocation.generateDisplayLists(3);
		this.starList = displayLists;
		this.glSkyList = displayLists + 1;
		this.glSkyList2 = displayLists + 2;

		// Bind stars to display list
		GL11.glPushMatrix();
		GL11.glNewList(this.starList, GL11.GL_COMPILE);
		this.renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();

		final Tessellator tessellator = Tessellator.instance;
		GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
		final byte byte2 = 64;
		final int i = 256 / byte2 + 2;
		float f = 16.0f;

		for (int j = -byte2 * i; j <= byte2 * i; j += byte2) {
			for (int l = -byte2 * i; l <= byte2 * i; l += byte2) {
				tessellator.startDrawingQuads();
				tessellator.addVertex(j + 0, f, l + 0);
				tessellator.addVertex(j + byte2, f, l + 0);
				tessellator.addVertex(j + byte2, f, l + byte2);
				tessellator.addVertex(j + 0, f, l + byte2);
				tessellator.draw();
			}
		}

		GL11.glEndList();
		GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
		f = -16.0f;
		tessellator.startDrawingQuads();

		for (int k = -byte2 * i; k <= byte2 * i; k += byte2) {
			for (int i1 = -byte2 * i; i1 <= byte2 * i; i1 += byte2) {
				tessellator.addVertex(k + byte2, f, i1 + 0);
				tessellator.addVertex(k + 0, f, i1 + 0);
				tessellator.addVertex(k + 0, f, i1 + byte2);
				tessellator.addVertex(k + byte2, f, i1 + byte2);
			}
		}

		tessellator.draw();
		GL11.glEndList();
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Vec3 skyColour = world.getSkyColor(mc.renderViewEntity, partialTicks);
		float skyCR = (float) skyColour.xCoord;
		float skyCG = (float) skyColour.yCoord;
		float skyCB = (float) skyColour.zCoord;
		float anagB;

		if (mc.gameSettings.anaglyph) {
			float anagR = (skyCR * 30.0f + skyCG * 59.0f + skyCB * 11.0f) / 100.0f;
			float anagG = (skyCR * 30.0f + skyCG * 70.0f) / 100.0f;
			anagB = (skyCR * 30.0f + skyCB * 70.0f) / 100.0f;
			skyCR = anagR;
			skyCG = anagG;
			skyCB = anagB;
		}

		GL11.glColor3f(skyCR, skyCG, skyCB);
		Tessellator tessellator1 = Tessellator.instance;
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glColor3f(skyCR, skyCG, skyCB);
		GL11.glCallList(this.glSkyList);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		RenderHelper.disableStandardItemLighting();
		float f7;
		float f8;
		float f9;
		float f10;

		float f18 = world.getStarBrightness(partialTicks);

		if (f18 > 0.0f) {
			GL11.glColor4f(f18, f18, f18, f18);
			GL11.glCallList(this.starList);
		}

		float[] afloat = new float[4];
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glPushMatrix();
		GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f);
		afloat[0] = 255 / 255.0f;
		afloat[1] = 194 / 255.0f;
		afloat[2] = 180 / 255.0f;
		afloat[3] = 0.3f;
		anagB = afloat[0];
		f7 = afloat[1];
		f8 = afloat[2];
		float f11;

		if (mc.gameSettings.anaglyph) {
			f9 = (anagB * 30.0f + f7 * 59.0f + f8 * 11.0f) / 100.0f;
			f10 = (anagB * 30.0f + f7 * 70.0f) / 100.0f;
			f11 = (anagB * 30.0f + f8 * 70.0f) / 100.0f;
			anagB = f9;
			f7 = f10;
			f8 = f11;
		}

		f18 = 1.0f - f18;

		tessellator1.startDrawing(GL11.GL_TRIANGLE_FAN);
		tessellator1.setColorRGBA_F(anagB * f18, f7 * f18, f8 * f18, afloat[3] * 2 / f18);
		tessellator1.addVertex(0.0, 100.0, 0.0);
		tessellator1.setColorRGBA_F(afloat[0] * f18, afloat[1] * f18, afloat[2] * f18, 0.0f);

		// Render sun aura
		f10 = 25.0f;
		tessellator1.addVertex(-f10, 100.0, -f10);
		tessellator1.addVertex(0, 100.0, (double) -f10 * 1.5f);
		tessellator1.addVertex(f10, 100.0, -f10);
		tessellator1.addVertex((double) f10 * 1.5f, 100.0, 0);
		tessellator1.addVertex(f10, 100.0, f10);
		tessellator1.addVertex(0, 100.0, (double) f10 * 1.5f);
		tessellator1.addVertex(-f10, 100.0, f10);
		tessellator1.addVertex((double) -f10 * 1.5f, 100.0, 0);
		tessellator1.addVertex(-f10, 100.0, -f10);

		tessellator1.draw();
		tessellator1.startDrawing(GL11.GL_TRIANGLE_FAN);
		tessellator1.setColorRGBA_F(anagB * f18, f7 * f18, f8 * f18, afloat[3] * f18);
		tessellator1.addVertex(0.0, 100.0, 0.0);
		tessellator1.setColorRGBA_F(afloat[0] * f18, afloat[1] * f18, afloat[2] * f18, 0.0f);

		// Render larger sun aura
		f10 = 45.0f;
		tessellator1.addVertex(-f10, 100.0, -f10);
		tessellator1.addVertex(0, 100.0, (double) -f10 * 1.5f);
		tessellator1.addVertex(f10, 100.0, -f10);
		tessellator1.addVertex((double) f10 * 1.5f, 100.0, 0);
		tessellator1.addVertex(f10, 100.0, f10);
		tessellator1.addVertex(0, 100.0, (double) f10 * 1.5f);
		tessellator1.addVertex(-f10, 100.0, f10);
		tessellator1.addVertex((double) -f10 * 1.5f, 100.0, 0);
		tessellator1.addVertex(-f10, 100.0, -f10);

		tessellator1.draw();
		GL11.glPopMatrix();
		GL11.glShadeModel(GL11.GL_FLAT);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glPushMatrix();
		f7 = 0.0f;
		f8 = 0.0f;
		f9 = 0.0f;
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glTranslatef(f7, f8, f9);
		GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f);
		
		// Render sun
		f10 = 19.0f;
		mc.renderEngine.bindTexture(VenusSkyProvider.sunTexture);
		tessellator1.startDrawingQuads();
		tessellator1.addVertexWithUV(-f10, 100.0, -f10, 0.0, 0.0);
		tessellator1.addVertexWithUV(f10, 100.0, -f10, 1.0, 0.0);
		tessellator1.addVertexWithUV(f10, 100.0, f10, 1.0, 1.0);
		tessellator1.addVertexWithUV(-f10, 100.0, f10, 0.0, 1.0);
		tessellator1.draw();

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(0.0f, 0.0f, 0.0f);
		double d0 = mc.thePlayer.getPosition(partialTicks).yCoord - world.getHorizon();

		if (d0 < 0.0) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0f, 12.0f, 0.0f);
			GL11.glCallList(this.glSkyList2);
			GL11.glPopMatrix();
			f8 = 1.0f;
			f9 = -((float) (d0 + 65.0));
			f10 = -f8;
			tessellator1.startDrawingQuads();
			tessellator1.setColorRGBA_I(0, 255);
			tessellator1.addVertex(-f8, f9, f8);
			tessellator1.addVertex(f8, f9, f8);
			tessellator1.addVertex(f8, f10, f8);
			tessellator1.addVertex(-f8, f10, f8);
			tessellator1.addVertex(-f8, f10, -f8);
			tessellator1.addVertex(f8, f10, -f8);
			tessellator1.addVertex(f8, f9, -f8);
			tessellator1.addVertex(-f8, f9, -f8);
			tessellator1.addVertex(f8, f10, -f8);
			tessellator1.addVertex(f8, f10, f8);
			tessellator1.addVertex(f8, f9, f8);
			tessellator1.addVertex(f8, f9, -f8);
			tessellator1.addVertex(-f8, f9, -f8);
			tessellator1.addVertex(-f8, f9, f8);
			tessellator1.addVertex(-f8, f10, f8);
			tessellator1.addVertex(-f8, f10, -f8);
			tessellator1.addVertex(-f8, f10, -f8);
			tessellator1.addVertex(-f8, f10, f8);
			tessellator1.addVertex(f8, f10, f8);
			tessellator1.addVertex(f8, f10, -f8);
			tessellator1.draw();
		}

		if (world.provider.isSkyColored()) {
			GL11.glColor3f(skyCR * 0.2f + 0.04f, skyCG * 0.2f + 0.04f, skyCB * 0.6f + 0.1f);
		} else {
			GL11.glColor3f(skyCR, skyCG, skyCB);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(0.0f, -((float) (d0 - 16.0)), 0.0f);
		GL11.glCallList(this.glSkyList2);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
	}

	private void renderStars() {
		final Random rand = new Random(10842);
		final Tessellator var2 = Tessellator.instance;
		var2.startDrawingQuads();

		for (int starIndex = 0; starIndex < (ConfigManagerCore.moreStars ? 40000 : 6000); ++starIndex) {
			double var4 = rand.nextFloat() * 2.0f - 1.0f;
			double var6 = rand.nextFloat() * 2.0f - 1.0f;
			double var8 = rand.nextFloat() * 2.0f - 1.0f;
			final double var10 = 0.15f + rand.nextFloat() * 0.1f;
			double var12 = var4 * var4 + var6 * var6 + var8 * var8;

			if (var12 < 1.0 && var12 > 0.01) {
				var12 = 1.0 / Math.sqrt(var12);
				var4 *= var12;
				var6 *= var12;
				var8 *= var12;
				final double var14 = var4 * (ConfigManagerCore.moreStars ? rand.nextDouble() * 150 + 130 : 100.0);
				final double var16 = var6 * (ConfigManagerCore.moreStars ? rand.nextDouble() * 150 + 130 : 100.0);
				final double var18 = var8 * (ConfigManagerCore.moreStars ? rand.nextDouble() * 150 + 130 : 100.0);
				final double var20 = Math.atan2(var4, var8);
				final double var22 = Math.sin(var20);
				final double var24 = Math.cos(var20);
				final double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
				final double var28 = Math.sin(var26);
				final double var30 = Math.cos(var26);
				final double var32 = rand.nextDouble() * Math.PI * 2.0;
				final double var34 = Math.sin(var32);
				final double var36 = Math.cos(var32);

				for (int var38 = 0; var38 < 4; ++var38) {
					final double var39 = 0.0;
					final double var41 = ((var38 & 2) - 1) * var10;
					final double var43 = ((var38 + 1 & 2) - 1) * var10;
					final double var47 = var41 * var36 - var43 * var34;
					final double var49 = var43 * var36 + var41 * var34;
					final double var53 = var47 * var28 + var39 * var30;
					final double var55 = var39 * var28 - var47 * var30;
					final double var57 = var55 * var22 - var49 * var24;
					final double var61 = var49 * var22 + var55 * var24;
					var2.addVertex(var14 + var57, var16 + var53, var18 + var61);
				}
			}
		}

		var2.draw();
	}

	public float getSkyBrightness(float par1) {
		final float var2 = FMLClientHandler.instance().getClient().theWorld.getCelestialAngle(par1);
		float var3 = 1.0f - (MathHelper.sin(var2 * (float) Math.PI * 2.0f) * 2.0f + 0.25f);

		if (var3 < 0.0f) {
			var3 = 0.0f;
		}

		if (var3 > 1.0f) {
			var3 = 1.0f;
		}
		
		return var3 * var3 * 1.0f;
	}
}
