package com.mattparks.space.venus.entities.render;

import org.lwjgl.opengl.GL11;

import com.mattparks.space.venus.blocks.VenusBlocks;
import com.mattparks.space.venus.entities.EntityVenusianTNT;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderVenusianTNT extends Render {
	private final RenderBlocks blockRenderer = new RenderBlocks();

	public RenderVenusianTNT() {
		this.shadowSize = 0.5F;
	}

	public void doRender(EntityVenusianTNT tnt, double x, double y, double z, float par5, float par6) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		float var10;

		if (tnt.fuse - par6 + 1.0F < 10.0F) {
			var10 = 1.0F - (tnt.fuse - par6 + 1.0F) / 10.0F;

			if (var10 < 0.0F) {
				var10 = 0.0F;
			}

			if (var10 > 1.0F) {
				var10 = 1.0F;
			}
			var10 *= var10;
			var10 *= var10;
			final float var11 = 1.0F + var10 * 0.3F;
			GL11.glScalef(var11, var11, var11);
		}

		var10 = (1.0F - (tnt.fuse - par6 + 1.0F) / 100.0F) * 0.8F;
		this.bindEntityTexture(tnt);
		this.blockRenderer.renderBlockAsItem(VenusBlocks.venusianTNT, 0, tnt.getBrightness(par6));

		if (tnt.fuse / 5 % 2 == 0) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, var10);
			this.blockRenderer.renderBlockAsItem(VenusBlocks.venusianTNT, 0, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		GL11.glPopMatrix();
	}

	protected ResourceLocation getEntityTexture(EntityVenusianTNT tnt) {
		return TextureMap.locationBlocksTexture;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity tnt) {
		return this.getEntityTexture((EntityVenusianTNT) tnt);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float par5, float par6) {
		this.doRender((EntityVenusianTNT) entity, x, y, z, par5, par6);
	}
}