package net.minecraft.client.renderer;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityRenderer
implements IResourceManagerReloadListener
{
	private static final Logger logger = LogManager.getLogger();
	private static final ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
	private static final ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
	public static boolean anaglyphEnable;
	public static int anaglyphField;
	private Minecraft mc;
	private float farPlaneDistance;
	public final ItemRenderer itemRenderer;
	private final MapItemRenderer theMapItemRenderer;
	private int rendererUpdateCount;
	private Entity pointedEntity;
	private MouseFilter mouseFilterXAxis = new MouseFilter();
	private MouseFilter mouseFilterYAxis = new MouseFilter();
	private MouseFilter mouseFilterDummy1 = new MouseFilter();
	private MouseFilter mouseFilterDummy2 = new MouseFilter();
	private MouseFilter mouseFilterDummy3 = new MouseFilter();
	private MouseFilter mouseFilterDummy4 = new MouseFilter();
	public float thirdPersonDistance = 4.0F;
	public float thirdPersonDistanceTemp = 4.0F;
	private float debugCamYaw;
	private float prevDebugCamYaw;
	private float debugCamPitch;
	private float prevDebugCamPitch;
	private float smoothCamYaw;
	private float smoothCamPitch;
	private float smoothCamFilterX;
	private float smoothCamFilterY;
	private float smoothCamPartialTicks;
	private float debugCamFOV;
	private float prevDebugCamFOV;
	private float camRoll;
	private float prevCamRoll;
	private final DynamicTexture lightmapTexture;
	private final int[] lightmapColors;
	private final ResourceLocation locationLightMap;
	private float fovModifierHand;
	private float fovModifierHandPrev;
	private float fovMultiplierTemp;
	private float bossColorModifier;
	private float bossColorModifierPrev;
	private boolean cloudFog;
	private final IResourceManager resourceManager;
	public ShaderGroup theShaderGroup;
	private static final ResourceLocation[] shaderResourceLocations = { new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json") };
	public static final int shaderCount = shaderResourceLocations.length;
	private int shaderIndex;
	private double cameraZoom;
	private double cameraYaw;
	private double cameraPitch;
	private long prevFrameTime;
	private long renderEndNanoTime;
	private boolean lightmapUpdateNeeded;
	float torchFlickerX;
	float torchFlickerDX;
	float torchFlickerY;
	float torchFlickerDY;
	private Random random;
	private int rainSoundCounter;
	float[] rainXCoords;
	float[] rainYCoords;
	FloatBuffer fogColorBuffer;
	float fogColorRed;
	float fogColorGreen;
	float fogColorBlue;
	private float fogColor2;
	private float fogColor1;
	public int debugViewDirection;
	private static final String __OBFID = "CL_00000947";

	public EntityRenderer(Minecraft p_i45076_1_, IResourceManager p_i45076_2_)
	{
		this.shaderIndex = shaderCount;
		this.cameraZoom = 1.0D;
		this.prevFrameTime = Minecraft.getSystemTime();
		this.random = new Random();
		this.fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
		this.mc = p_i45076_1_;
		this.resourceManager = p_i45076_2_;
		this.theMapItemRenderer = new MapItemRenderer(p_i45076_1_.getTextureManager());
		this.itemRenderer = new ItemRenderer(p_i45076_1_);
		this.lightmapTexture = new DynamicTexture(16, 16);
		this.locationLightMap = p_i45076_1_.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
		this.lightmapColors = this.lightmapTexture.getTextureData();
		this.theShaderGroup = null;
	}

	public boolean isShaderActive()
	{
		return (OpenGlHelper.shadersSupported) && (this.theShaderGroup != null);
	}

	public void deactivateShader()
	{
		if (this.theShaderGroup != null) {
			this.theShaderGroup.deleteShaderGroup();
		}
		this.theShaderGroup = null;
		this.shaderIndex = shaderCount;
	}

	public void activateNextShader() throws IOException
	{
		if (OpenGlHelper.shadersSupported)
		{
			if (this.theShaderGroup != null) {
				this.theShaderGroup.deleteShaderGroup();
			}
			this.shaderIndex = ((this.shaderIndex + 1) % (shaderResourceLocations.length + 1));
			if (this.shaderIndex != shaderCount)
			{
				try
				{
					logger.info("Selecting effect " + shaderResourceLocations[this.shaderIndex]);
					this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), shaderResourceLocations[this.shaderIndex]);
					this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
				}
				catch (JsonSyntaxException jsonsyntaxexception)
				{
					logger.warn("Failed to load shader: " + shaderResourceLocations[this.shaderIndex], jsonsyntaxexception);
					this.shaderIndex = shaderCount;
				}
			}
			else
			{
				this.theShaderGroup = null;
				logger.info("No effect selected");
			}
		}
	}

	public void onResourceManagerReload(IResourceManager p_110549_1_)
	{

	}

	public void updateRenderer()
	{
		if ((OpenGlHelper.shadersSupported) && (ShaderLinkHelper.getStaticShaderLinkHelper() == null)) {
			ShaderLinkHelper.setNewStaticShaderLinkHelper();
		}
		updateFovModifierHand();
		updateTorchFlicker();
		this.fogColor2 = this.fogColor1;
		this.thirdPersonDistanceTemp = this.thirdPersonDistance;
		this.prevDebugCamYaw = this.debugCamYaw;
		this.prevDebugCamPitch = this.debugCamPitch;
		this.prevDebugCamFOV = this.debugCamFOV;
		this.prevCamRoll = this.camRoll;
		if (this.mc.gameSettings.smoothCamera)
		{
			float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f1 = f * f * f * 8.0F;
			this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f1);
			this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f1);
			this.smoothCamPartialTicks = 0.0F;
			this.smoothCamYaw = 0.0F;
			this.smoothCamPitch = 0.0F;
		}
		if (this.mc.renderViewEntity == null) {
			this.mc.renderViewEntity = this.mc.thePlayer;
		}
		float f = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(this.mc.renderViewEntity.posX), MathHelper.floor_double(this.mc.renderViewEntity.posY), MathHelper.floor_double(this.mc.renderViewEntity.posZ));
		float f1 = this.mc.gameSettings.renderDistanceChunks / 16.0F;
		float f2 = f * (1.0F - f1) + f1;
		this.fogColor1 += (f2 - this.fogColor1) * 0.1F;
		this.rendererUpdateCount += 1;
		this.itemRenderer.updateEquippedItem();
		addRainParticles();
		this.bossColorModifierPrev = this.bossColorModifier;
		if (BossStatus.hasColorModifier)
		{
			this.bossColorModifier += 0.05F;
			if (this.bossColorModifier > 1.0F) {
				this.bossColorModifier = 1.0F;
			}
			BossStatus.hasColorModifier = false;
		}
		else if (this.bossColorModifier > 0.0F)
		{
			this.bossColorModifier -= 0.0125F;
		}
	}

	public ShaderGroup getShaderGroup()
	{
		return this.theShaderGroup;
	}

	public void updateShaderGroupSize(int p_147704_1_, int p_147704_2_)
	{
		;
		;
		;
		if (OpenGlHelper.shadersSupported) {
			if (this.theShaderGroup != null) {
				this.theShaderGroup.createBindFramebuffers(p_147704_1_, p_147704_2_);
			}
		}
	}

	public void getMouseOver(float p_78473_1_)
	{
		;
		;
		if (this.mc.renderViewEntity != null) {
			if (this.mc.theWorld != null)
			{
				this.mc.pointedEntity = null;
				double d0 = this.mc.playerController.getBlockReachDistance();
				this.mc.objectMouseOver = this.mc.renderViewEntity.rayTrace(d0, p_78473_1_);
				double d1 = d0;
				Vec3 vec3 = this.mc.renderViewEntity.getPosition(p_78473_1_);
				if (this.mc.playerController.extendedReach())
				{
					d0 = 6.0D;
					d1 = 6.0D;
				}
				else
				{
					if (d0 > 3.0D) {
						d1 = 3.0D;
					}
					d0 = d1;
				}
				if (this.mc.objectMouseOver != null) {
					d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
				}
				Vec3 vec31 = this.mc.renderViewEntity.getLook(p_78473_1_);
				Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
				this.pointedEntity = null;
				Vec3 vec33 = null;
				float f1 = 1.0F;
				List list = this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(this.mc.renderViewEntity, this.mc.renderViewEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(f1, f1, f1));
				double d2 = d1;
				for (int i = 0; i < list.size(); i++)
				{
					Entity entity = (Entity)list.get(i);
					if (entity.canBeCollidedWith())
					{
						float f2 = entity.getCollisionBorderSize();
						AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
						MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
						if (axisalignedbb.isVecInside(vec3))
						{
							if ((0.0D < d2) || (d2 == 0.0D))
							{
								this.pointedEntity = entity;
								vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
								d2 = 0.0D;
							}
						}
						else if (movingobjectposition != null)
						{
							double d3 = vec3.distanceTo(movingobjectposition.hitVec);
							if ((d3 < d2) || (d2 == 0.0D)) {
								if ((entity == this.mc.renderViewEntity.ridingEntity) && (!entity.canRiderInteract()))
								{
									if (d2 == 0.0D)
									{
										this.pointedEntity = entity;
										vec33 = movingobjectposition.hitVec;
									}
								}
								else
								{
									this.pointedEntity = entity;
									vec33 = movingobjectposition.hitVec;
									d2 = d3;
								}
							}
						}
					}
				}
				if ((this.pointedEntity != null) && ((d2 < d1) || (this.mc.objectMouseOver == null)))
				{
					this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);
					if (((this.pointedEntity instanceof EntityLivingBase)) || ((this.pointedEntity instanceof EntityItemFrame))) {
						this.mc.pointedEntity = this.pointedEntity;
					}
				}
			}
		}
	}

    private void updateFovModifierHand()
    {
        EntityPlayerSP var1 = (EntityPlayerSP)this.mc.renderViewEntity;
        this.fovMultiplierTemp = var1.getFOVMultiplier();
        this.fovModifierHandPrev = this.fovModifierHand;
        this.fovModifierHand += (this.fovMultiplierTemp - this.fovModifierHand) * 0.5F;

        if (this.fovModifierHand > 1.5F)
        {
            this.fovModifierHand = 1.5F;
        }

        if (this.fovModifierHand < 0.1F)
        {
            this.fovModifierHand = 0.1F;
        }
    }

    private float getFOVModifier(float p_78481_1_, boolean p_78481_2_)
    {
        if (this.debugViewDirection > 0)
        {
            return 90.0F;
        }
        else
        {
            EntityPlayer var3 = (EntityPlayer)this.mc.renderViewEntity;
            float var4 = 70.0F;

            if (p_78481_2_)
            {
                var4 = this.mc.gameSettings.fovSetting;
                var4 *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * p_78481_1_;
            }

            if (var3.getHealth() <= 0.0F)
            {
                float var5 = (float)var3.deathTime + p_78481_1_;
                var4 /= (1.0F - 500.0F / (var5 + 500.0F)) * 2.0F + 1.0F;
            }

            Block var6 = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, var3, p_78481_1_);

            if (var6.getMaterial() == Material.water)
            {
                var4 = var4 * 60.0F / 70.0F;
            }

            return var4 + this.prevDebugCamFOV + (this.debugCamFOV - this.prevDebugCamFOV) * p_78481_1_;
        }
    }

	private void hurtCameraEffect(float p_78482_1_)
	{
		;
		;
		EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
		float f1 = entitylivingbase.hurtTime - p_78482_1_;
		if (entitylivingbase.getHealth() <= 0.0F)
		{
			float f2 = entitylivingbase.deathTime + p_78482_1_;
			GL11.glRotatef(40.0F - 8000.0F / (f2 + 200.0F), 0.0F, 0.0F, 1.0F);
		}
		if (f1 >= 0.0F)
		{
			f1 /= entitylivingbase.maxHurtTime;
			f1 = MathHelper.sin(f1 * f1 * f1 * f1 * 3.141593F);
			float f2 = entitylivingbase.attackedAtYaw;
			GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f1 * 14.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
		}
	}

	private void setupViewBobbing(float p_78475_1_)
	{
		;
		;
		if ((this.mc.renderViewEntity instanceof EntityPlayer))
		{
			EntityPlayer entityplayer = (EntityPlayer)this.mc.renderViewEntity;
			float f1 = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
			float f2 = -(entityplayer.distanceWalkedModified + f1 * p_78475_1_);
			float f3 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * p_78475_1_;
			float f4 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * p_78475_1_;
			GL11.glTranslatef(MathHelper.sin(f2 * 3.141593F) * f3 * 0.5F, -Math.abs(MathHelper.cos(f2 * 3.141593F) * f3), 0.0F);
			GL11.glRotatef(MathHelper.sin(f2 * 3.141593F) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(Math.abs(MathHelper.cos(f2 * 3.141593F - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
		}
	}

	private void orientCamera(float p_78467_1_)
	{
		;
		;
		EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
		float f1 = entitylivingbase.yOffset - 1.62F;
		double d0 = entitylivingbase.prevPosX + (entitylivingbase.posX - entitylivingbase.prevPosX) * p_78467_1_;
		double d1 = entitylivingbase.prevPosY + (entitylivingbase.posY - entitylivingbase.prevPosY) * p_78467_1_ - f1;
		double d2 = entitylivingbase.prevPosZ + (entitylivingbase.posZ - entitylivingbase.prevPosZ) * p_78467_1_;
		GL11.glRotatef(this.prevCamRoll + (this.camRoll - this.prevCamRoll) * p_78467_1_, 0.0F, 0.0F, 1.0F);
		if (entitylivingbase.isPlayerSleeping())
		{
			f1 = (float)(f1 + 1.0D);
			GL11.glTranslatef(0.0F, 0.3F, 0.0F);
			if (!this.mc.gameSettings.debugCamEnable)
			{
				ForgeHooksClient.orientBedCamera(this.mc, entitylivingbase);
				GL11.glRotatef(entitylivingbase.prevRotationYaw + (entitylivingbase.rotationYaw - entitylivingbase.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F, -1.0F, 0.0F);
				GL11.glRotatef(entitylivingbase.prevRotationPitch + (entitylivingbase.rotationPitch - entitylivingbase.prevRotationPitch) * p_78467_1_, -1.0F, 0.0F, 0.0F);
			}
		}
		else if (this.mc.gameSettings.thirdPersonView > 0)
		{
			double d7 = this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * p_78467_1_;
			if (this.mc.gameSettings.debugCamEnable)
			{
				float f6 = this.prevDebugCamYaw + (this.debugCamYaw - this.prevDebugCamYaw) * p_78467_1_;
				float f2 = this.prevDebugCamPitch + (this.debugCamPitch - this.prevDebugCamPitch) * p_78467_1_;
				GL11.glTranslatef(0.0F, 0.0F, (float)-d7);
				GL11.glRotatef(f2, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(f6, 0.0F, 1.0F, 0.0F);
			}
			else
			{
				float f6 = entitylivingbase.rotationYaw;
				float f2 = entitylivingbase.rotationPitch;
				if (this.mc.gameSettings.thirdPersonView == 2) {
					f2 += 180.0F;
				}
				double d3 = -MathHelper.sin(f6 / 180.0F * 3.141593F) * MathHelper.cos(f2 / 180.0F * 3.141593F) * d7;
				double d4 = MathHelper.cos(f6 / 180.0F * 3.141593F) * MathHelper.cos(f2 / 180.0F * 3.141593F) * d7;
				double d5 = -MathHelper.sin(f2 / 180.0F * 3.141593F) * d7;
				for (int k = 0; k < 8; k++)
				{
					float f3 = (k & 0x1) * 2 - 1;
					float f4 = (k >> 1 & 0x1) * 2 - 1;
					float f5 = (k >> 2 & 0x1) * 2 - 1;
					f3 *= 0.1F;
					f4 *= 0.1F;
					f5 *= 0.1F;
					MovingObjectPosition movingobjectposition = this.mc.theWorld.rayTraceBlocks(Vec3.createVectorHelper(d0 + f3, d1 + f4, d2 + f5), Vec3.createVectorHelper(d0 - d3 + f3 + f5, d1 - d5 + f4, d2 - d4 + f5));
					if (movingobjectposition != null)
					{
						double d6 = movingobjectposition.hitVec.distanceTo(Vec3.createVectorHelper(d0, d1, d2));
						if (d6 < d7) {
							d7 = d6;
						}
					}
				}
				if (this.mc.gameSettings.thirdPersonView == 2) {
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}
				GL11.glRotatef(entitylivingbase.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(entitylivingbase.rotationYaw - f6, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.0F, 0.0F, (float)-d7);
				GL11.glRotatef(f6 - entitylivingbase.rotationYaw, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(f2 - entitylivingbase.rotationPitch, 1.0F, 0.0F, 0.0F);
			}
		}
		else
		{
			GL11.glTranslatef(0.0F, 0.0F, -0.1F);
		}
		if (!this.mc.gameSettings.debugCamEnable)
		{
			GL11.glRotatef(entitylivingbase.prevRotationPitch + (entitylivingbase.rotationPitch - entitylivingbase.prevRotationPitch) * p_78467_1_, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(entitylivingbase.prevRotationYaw + (entitylivingbase.rotationYaw - entitylivingbase.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F, 1.0F, 0.0F);
		}
		GL11.glTranslatef(0.0F, f1, 0.0F);
		d0 = entitylivingbase.prevPosX + (entitylivingbase.posX - entitylivingbase.prevPosX) * p_78467_1_;
		d1 = entitylivingbase.prevPosY + (entitylivingbase.posY - entitylivingbase.prevPosY) * p_78467_1_ - f1;
		d2 = entitylivingbase.prevPosZ + (entitylivingbase.posZ - entitylivingbase.prevPosZ) * p_78467_1_;
		this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, p_78467_1_);
	}

    private void setupCameraTransform(float p_78479_1_, int p_78479_2_)
    {
        this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        float var3 = 0.07F;

        if (this.mc.gameSettings.anaglyph)
        {
            GL11.glTranslatef((float)(-(p_78479_2_ * 2 - 1)) * var3, 0.0F, 0.0F);
        }

        if (this.cameraZoom != 1.0D)
        {
            GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
            GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
        }

        Project.gluPerspective(this.getFOVModifier(p_78479_1_, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
        float var4;

        if (this.mc.playerController.enableEverythingIsScrewedUpMode())
        {
            var4 = 0.6666667F;
            GL11.glScalef(1.0F, var4, 1.0F);
        }

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        if (this.mc.gameSettings.anaglyph)
        {
            GL11.glTranslatef((float)(p_78479_2_ * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        this.hurtCameraEffect(p_78479_1_);

        if (this.mc.gameSettings.viewBobbing)
        {
            this.setupViewBobbing(p_78479_1_);
        }

        var4 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * p_78479_1_;

        if (var4 > 0.0F)
        {
            byte var5 = 20;

            if (this.mc.thePlayer.isPotionActive(Potion.confusion))
            {
                var5 = 7;
            }

            float var6 = 5.0F / (var4 * var4 + 5.0F) - var4 * 0.04F;
            var6 *= var6;
            GL11.glRotatef(((float)this.rendererUpdateCount + p_78479_1_) * (float)var5, 0.0F, 1.0F, 1.0F);
            GL11.glScalef(1.0F / var6, 1.0F, 1.0F);
            GL11.glRotatef(-((float)this.rendererUpdateCount + p_78479_1_) * (float)var5, 0.0F, 1.0F, 1.0F);
        }

        this.orientCamera(p_78479_1_);

        if (this.debugViewDirection > 0)
        {
            int var7 = this.debugViewDirection - 1;

            if (var7 == 1)
            {
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var7 == 2)
            {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var7 == 3)
            {
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var7 == 4)
            {
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var7 == 5)
            {
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }
        }
    }

    private void renderHand(float p_78476_1_, int p_78476_2_)
    {
        if (this.debugViewDirection <= 0)
        {
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            float var3 = 0.07F;

            if (this.mc.gameSettings.anaglyph)
            {
                GL11.glTranslatef((float)(-(p_78476_2_ * 2 - 1)) * var3, 0.0F, 0.0F);
            }

            if (this.cameraZoom != 1.0D)
            {
                GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
                GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
            }

            Project.gluPerspective(this.getFOVModifier(p_78476_1_, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);

            if (this.mc.playerController.enableEverythingIsScrewedUpMode())
            {
                float var4 = 0.6666667F;
                GL11.glScalef(1.0F, var4, 1.0F);
            }

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            if (this.mc.gameSettings.anaglyph)
            {
                GL11.glTranslatef((float)(p_78476_2_ * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GL11.glPushMatrix();
            this.hurtCameraEffect(p_78476_1_);

            if (this.mc.gameSettings.viewBobbing)
            {
                this.setupViewBobbing(p_78476_1_);
            }

            if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping() && !this.mc.gameSettings.hideGUI && !this.mc.playerController.enableEverythingIsScrewedUpMode())
            {
                this.enableLightmap((double)p_78476_1_);
                this.itemRenderer.renderItemInFirstPerson(p_78476_1_);
                this.disableLightmap((double)p_78476_1_);
            }

            GL11.glPopMatrix();

            if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping())
            {
                this.itemRenderer.renderOverlays(p_78476_1_);
                this.hurtCameraEffect(p_78476_1_);
            }

            if (this.mc.gameSettings.viewBobbing)
            {
                this.setupViewBobbing(p_78476_1_);
            }
        }
    }

	public void disableLightmap(double p_78483_1_)
	{
		;
		;
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(3553);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void enableLightmap(double p_78463_1_)
	{
		;
		;
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glMatrixMode(5890);
		GL11.glLoadIdentity();
		float f = 0.0039063F;
		GL11.glScalef(f, f, f);
		GL11.glTranslatef(8.0F, 8.0F, 8.0F);
		GL11.glMatrixMode(5888);
		this.mc.getTextureManager().bindTexture(this.locationLightMap);
		GL11.glTexParameteri(3553, 10241, 9729);
		GL11.glTexParameteri(3553, 10240, 9729);
		GL11.glTexParameteri(3553, 10241, 9729);
		GL11.glTexParameteri(3553, 10240, 9729);
		GL11.glTexParameteri(3553, 10242, 10496);
		GL11.glTexParameteri(3553, 10243, 10496);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(3553);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	private void updateTorchFlicker()
	{
		this.torchFlickerDX = ((float)(this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random()));
		this.torchFlickerDY = ((float)(this.torchFlickerDY + (Math.random() - Math.random()) * Math.random() * Math.random()));
		this.torchFlickerDX = ((float)(this.torchFlickerDX * 0.9D));
		this.torchFlickerDY = ((float)(this.torchFlickerDY * 0.9D));
		this.torchFlickerX += (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
		this.torchFlickerY += (this.torchFlickerDY - this.torchFlickerY) * 1.0F;
		this.lightmapUpdateNeeded = true;
	}

	private void updateLightmap(float p_78472_1_)
	{
		;
		;
		WorldClient worldclient = this.mc.theWorld;
		if (worldclient != null)
		{
			for (int i = 0; i < 256; i++)
			{
				float f1 = worldclient.getSunBrightness(1.0F) * 0.95F + 0.05F;
				float f2 = worldclient.provider.lightBrightnessTable[(i / 16)] * f1;
				float f3 = worldclient.provider.lightBrightnessTable[(i % 16)] * (this.torchFlickerX * 0.1F + 1.5F);
				if (worldclient.lastLightningBolt > 0) {
					f2 = worldclient.provider.lightBrightnessTable[(i / 16)];
				}
				float f4 = f2 * (worldclient.getSunBrightness(1.0F) * 0.65F + 0.35F);
				float f5 = f2 * (worldclient.getSunBrightness(1.0F) * 0.65F + 0.35F);
				float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
				float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
				float f8 = f4 + f3;
				float f9 = f5 + f6;
				float f10 = f2 + f7;
				f8 = f8 * 0.96F + 0.03F;
				f9 = f9 * 0.96F + 0.03F;
				f10 = f10 * 0.96F + 0.03F;
				if (this.bossColorModifier > 0.0F)
				{
					float f11 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * p_78472_1_;
					f8 = f8 * (1.0F - f11) + f8 * 0.7F * f11;
					f9 = f9 * (1.0F - f11) + f9 * 0.6F * f11;
					f10 = f10 * (1.0F - f11) + f10 * 0.6F * f11;
				}
				if (worldclient.provider.dimensionId == 1)
				{
					f8 = 0.22F + f3 * 0.75F;
					f9 = 0.28F + f6 * 0.75F;
					f10 = 0.25F + f7 * 0.75F;
				}
				if (this.mc.thePlayer.isPotionActive(Potion.nightVision))
				{
					float f11 = getNightVisionBrightness(this.mc.thePlayer, p_78472_1_);
					float f12 = 1.0F / f8;
					if (f12 > 1.0F / f9) {
						f12 = 1.0F / f9;
					}
					if (f12 > 1.0F / f10) {
						f12 = 1.0F / f10;
					}
					f8 = f8 * (1.0F - f11) + f8 * f12 * f11;
					f9 = f9 * (1.0F - f11) + f9 * f12 * f11;
					f10 = f10 * (1.0F - f11) + f10 * f12 * f11;
				}
				if (f8 > 1.0F) {
					f8 = 1.0F;
				}
				if (f9 > 1.0F) {
					f9 = 1.0F;
				}
				if (f10 > 1.0F) {
					f10 = 1.0F;
				}
				float f11 = this.mc.gameSettings.gammaSetting;
				float f12 = 1.0F - f8;
				float f13 = 1.0F - f9;
				float f14 = 1.0F - f10;
				f12 = 1.0F - f12 * f12 * f12 * f12;
				f13 = 1.0F - f13 * f13 * f13 * f13;
				f14 = 1.0F - f14 * f14 * f14 * f14;
				f8 = f8 * (1.0F - f11) + f12 * f11;
				f9 = f9 * (1.0F - f11) + f13 * f11;
				f10 = f10 * (1.0F - f11) + f14 * f11;
				f8 = f8 * 0.96F + 0.03F;
				f9 = f9 * 0.96F + 0.03F;
				f10 = f10 * 0.96F + 0.03F;
				if (f8 > 1.0F) {
					f8 = 1.0F;
				}
				if (f9 > 1.0F) {
					f9 = 1.0F;
				}
				if (f10 > 1.0F) {
					f10 = 1.0F;
				}
				if (f8 < 0.0F) {
					f8 = 0.0F;
				}
				if (f9 < 0.0F) {
					f9 = 0.0F;
				}
				if (f10 < 0.0F) {
					f10 = 0.0F;
				}
				short short1 = 255;
				int j = (int)(f8 * 255.0F);
				int k = (int)(f9 * 255.0F);
				int l = (int)(f10 * 255.0F);
				this.lightmapColors[i] = (short1 << 24 | j << 16 | k << 8 | l);
			}
			this.lightmapTexture.updateDynamicTexture();
			this.lightmapUpdateNeeded = false;
		}
	}

	private float getNightVisionBrightness(EntityPlayer p_82830_1_, float p_82830_2_)
	{
		;
		;
		;
		int i = p_82830_1_.getActivePotionEffect(Potion.nightVision).getDuration();
		return i > 200 ? 1.0F : 0.7F + MathHelper.sin((i - p_82830_2_) * 3.141593F * 0.2F) * 0.3F;
	}

	public void updateCameraAndRender(float p_78480_1_)
	{
		;
		;
		this.mc.mcProfiler.startSection("lightTex");
		if (this.lightmapUpdateNeeded) {
			updateLightmap(p_78480_1_);
		}
		this.mc.mcProfiler.endSection();
		boolean flag = Display.isActive();
		if ((!flag) && (this.mc.gameSettings.pauseOnLostFocus) && ((!this.mc.gameSettings.touchscreen) || (!Mouse.isButtonDown(1))))
		{
			if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
				this.mc.displayInGameMenu();
			}
		}
		else {
			this.prevFrameTime = Minecraft.getSystemTime();
		}
		this.mc.mcProfiler.startSection("mouse");
		if ((this.mc.inGameHasFocus) && (flag))
		{
			this.mc.mouseHelper.mouseXYChange();
			float f1 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f2 = f1 * f1 * f1 * 8.0F;
			float f3 = this.mc.mouseHelper.deltaX * f2;
			float f4 = this.mc.mouseHelper.deltaY * f2;
			byte b0 = 1;
			if (this.mc.gameSettings.invertMouse) {
				b0 = -1;
			}
			if (this.mc.gameSettings.smoothCamera)
			{
				this.smoothCamYaw += f3;
				this.smoothCamPitch += f4;
				float f5 = p_78480_1_ - this.smoothCamPartialTicks;
				this.smoothCamPartialTicks = p_78480_1_;
				f3 = this.smoothCamFilterX * f5;
				f4 = this.smoothCamFilterY * f5;
				this.mc.thePlayer.setAngles(f3, f4 * b0);
			}
			else
			{
				this.mc.thePlayer.setAngles(f3, f4 * b0);
			}
		}
		this.mc.mcProfiler.endSection();
		if (!this.mc.skipRenderWorld)
		{
			anaglyphEnable = this.mc.gameSettings.anaglyph;
			final ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
			final int k = Mouse.getX() * i / this.mc.displayWidth;
			final int l = j - Mouse.getY() * j / this.mc.displayHeight - 1;
			int i1 = this.mc.gameSettings.limitFramerate;
			if (this.mc.theWorld != null)
			{
				this.mc.mcProfiler.startSection("level");
				if (this.mc.isFramerateLimitBelowMax()) {
					renderWorld(p_78480_1_, this.renderEndNanoTime + 1000000000 / i1);
				} else {
					renderWorld(p_78480_1_, 0L);
				}
				if (OpenGlHelper.shadersSupported)
				{
					if (this.theShaderGroup != null)
					{
						GL11.glMatrixMode(5890);
						GL11.glPushMatrix();
						GL11.glLoadIdentity();
						this.theShaderGroup.loadShaderGroup(p_78480_1_);
						GL11.glPopMatrix();
					}
					this.mc.getFramebuffer().bindFramebuffer(true);
				}
				this.renderEndNanoTime = System.nanoTime();
				this.mc.mcProfiler.endStartSection("gui");
				if ((!this.mc.gameSettings.hideGUI) || (this.mc.currentScreen != null))
				{
					GL11.glAlphaFunc(516, 0.1F);
					this.mc.ingameGUI.renderGameOverlay(p_78480_1_, this.mc.currentScreen != null, k, l);
				}
				this.mc.mcProfiler.endSection();
			}
			else
			{
				GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
				GL11.glMatrixMode(5889);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(5888);
				GL11.glLoadIdentity();
				setupOverlayRendering();
				this.renderEndNanoTime = System.nanoTime();
			}
			if (this.mc.currentScreen != null)
			{
				GL11.glClear(256);
				try
				{
					if (!MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Pre(this.mc.currentScreen, k, l, p_78480_1_))) {
						this.mc.currentScreen.drawScreen(k, l, p_78480_1_);
					}
					MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Post(this.mc.currentScreen, k, l, p_78480_1_));
				}
				catch (Throwable throwable)
				{
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
					crashreportcategory.addCrashSectionCallable("Screen name", new Callable()
					{
						private static final String __OBFID = "CL_00000948";

						public String call()
						{
							return EntityRenderer.this.mc.currentScreen.getClass().getCanonicalName();
						}
					});
					crashreportcategory.addCrashSectionCallable("Mouse location", new Callable()
					{
						private static final String __OBFID = "CL_00000950";

						public String call()
						{
							return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", new Object[] { Integer.valueOf(k), Integer.valueOf(l), Integer.valueOf(Mouse.getX()), Integer.valueOf(Mouse.getY()) });
						}
					});
					crashreportcategory.addCrashSectionCallable("Screen size", new Callable()
					{
						private static final String __OBFID = "CL_00000951";

						public String call()
						{
							return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", new Object[] { Integer.valueOf(scaledresolution.getScaledWidth()), Integer.valueOf(scaledresolution.getScaledHeight()), Integer.valueOf(EntityRenderer.this.mc.displayWidth), Integer.valueOf(EntityRenderer.this.mc.displayHeight), Integer.valueOf(scaledresolution.getScaleFactor()) });
						}
					});
					throw new ReportedException(crashreport);
				}
			}
		}
	}

	public void func_152430_c(float p_152430_1_)
	{
		;
		;
		setupOverlayRendering();
		ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		int i = scaledresolution.getScaledWidth();
		int j = scaledresolution.getScaledHeight();
		this.mc.ingameGUI.func_152126_a(i, j);
	}

	public void renderWorld(float p_78471_1_, long p_78471_2_)
	{
		;
		;
		;
		this.mc.mcProfiler.startSection("lightTex");
		if (this.lightmapUpdateNeeded) {
			updateLightmap(p_78471_1_);
		}
		GL11.glEnable(2884);
		GL11.glEnable(2929);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.5F);
		if (this.mc.renderViewEntity == null) {
			this.mc.renderViewEntity = this.mc.thePlayer;
		}
		this.mc.mcProfiler.endStartSection("pick");
		getMouseOver(p_78471_1_);
		EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
		RenderGlobal renderglobal = this.mc.renderGlobal;
		EffectRenderer effectrenderer = this.mc.effectRenderer;
		double d0 = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * p_78471_1_;
		double d1 = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * p_78471_1_;
		double d2 = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * p_78471_1_;
		this.mc.mcProfiler.endStartSection("center");
		for (int j = 0; j < 2; j++)
		{
			if (this.mc.gameSettings.anaglyph)
			{
				anaglyphField = j;
				if (anaglyphField == 0) {
					GL11.glColorMask(false, true, true, false);
				} else {
					GL11.glColorMask(true, false, false, false);
				}
			}
			this.mc.mcProfiler.endStartSection("clear");
			GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
			updateFogColor(p_78471_1_);
			GL11.glClear(16640);
			GL11.glEnable(2884);
			this.mc.mcProfiler.endStartSection("camera");
			setupCameraTransform(p_78471_1_, j);
			ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
			this.mc.mcProfiler.endStartSection("frustrum");
			ClippingHelperImpl.getInstance();
			if (this.mc.gameSettings.renderDistanceChunks >= 4)
			{
				setupFog(-1, p_78471_1_);
				this.mc.mcProfiler.endStartSection("sky");
				renderglobal.renderSky(p_78471_1_);
			}
			GL11.glEnable(2912);
			setupFog(1, p_78471_1_);
			if (this.mc.gameSettings.ambientOcclusion != 0) {
				GL11.glShadeModel(7425);
			}
			this.mc.mcProfiler.endStartSection("culling");
			Frustrum frustrum = new Frustrum();
			frustrum.setPosition(d0, d1, d2);
			this.mc.renderGlobal.clipRenderersByFrustum(frustrum, p_78471_1_);
			if (j == 0)
			{
				this.mc.mcProfiler.endStartSection("updatechunks");
				while ((!this.mc.renderGlobal.updateRenderers(entitylivingbase, false)) && (p_78471_2_ != 0L))
				{
					long k = p_78471_2_ - System.nanoTime();
					if ((k < 0L) || (k > 1000000000L)) {
						break;
					}
				}
			}
			if (entitylivingbase.posY < 128.0D) {
				renderCloudsCheck(renderglobal, p_78471_1_);
			}
			this.mc.mcProfiler.endStartSection("prepareterrain");
			setupFog(0, p_78471_1_);
			GL11.glEnable(2912);
			this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			RenderHelper.disableStandardItemLighting();
			this.mc.mcProfiler.endStartSection("terrain");
			GL11.glMatrixMode(5888);
			GL11.glPushMatrix();
			renderglobal.sortAndRender(entitylivingbase, 0, p_78471_1_);
			GL11.glShadeModel(7424);
			GL11.glAlphaFunc(516, 0.1F);
			if (this.debugViewDirection == 0)
			{
				GL11.glMatrixMode(5888);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				RenderHelper.enableStandardItemLighting();
				this.mc.mcProfiler.endStartSection("entities");
				ForgeHooksClient.setRenderPass(0);
				renderglobal.renderEntities(entitylivingbase, frustrum, p_78471_1_);
				ForgeHooksClient.setRenderPass(0);

				RenderHelper.disableStandardItemLighting();
				disableLightmap(p_78471_1_);
				GL11.glMatrixMode(5888);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				if ((this.mc.objectMouseOver != null) && (entitylivingbase.isInsideOfMaterial(Material.water)) && ((entitylivingbase instanceof EntityPlayer)) && (!this.mc.gameSettings.hideGUI))
				{
					EntityPlayer entityplayer = (EntityPlayer)entitylivingbase;
					GL11.glDisable(3008);
					this.mc.mcProfiler.endStartSection("outline");
					if (!ForgeHooksClient.onDrawBlockHighlight(renderglobal, entityplayer, this.mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), p_78471_1_)) {
						renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, p_78471_1_);
					}
					GL11.glEnable(3008);
				}
			}
			GL11.glMatrixMode(5888);
			GL11.glPopMatrix();
			if ((this.cameraZoom == 1.0D) && ((entitylivingbase instanceof EntityPlayer)) && (!this.mc.gameSettings.hideGUI) && (this.mc.objectMouseOver != null) && (!entitylivingbase.isInsideOfMaterial(Material.water)))
			{
				EntityPlayer entityplayer = (EntityPlayer)entitylivingbase;
				GL11.glDisable(3008);
				this.mc.mcProfiler.endStartSection("outline");
				if (!ForgeHooksClient.onDrawBlockHighlight(renderglobal, entityplayer, this.mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), p_78471_1_)) {
					renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, p_78471_1_);
				}
				GL11.glEnable(3008);
			}
			this.mc.mcProfiler.endStartSection("destroyProgress");
			GL11.glEnable(3042);
			OpenGlHelper.glBlendFunc(770, 1, 1, 0);
			renderglobal.drawBlockDamageTexture(Tessellator.instance, entitylivingbase, p_78471_1_);
			GL11.glDisable(3042);
			if (this.debugViewDirection == 0)
			{
				enableLightmap(p_78471_1_);
				this.mc.mcProfiler.endStartSection("litParticles");
				effectrenderer.renderLitParticles(entitylivingbase, p_78471_1_);
				RenderHelper.disableStandardItemLighting();
				setupFog(0, p_78471_1_);
				this.mc.mcProfiler.endStartSection("particles");
				effectrenderer.renderParticles(entitylivingbase, p_78471_1_);
				disableLightmap(p_78471_1_);
			}
			GL11.glDepthMask(false);
			GL11.glEnable(2884);
			this.mc.mcProfiler.endStartSection("weather");
			renderRainSnow(p_78471_1_);
			GL11.glDepthMask(true);
			GL11.glDisable(3042);
			GL11.glEnable(2884);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glAlphaFunc(516, 0.1F);
			setupFog(0, p_78471_1_);
			GL11.glEnable(3042);
			GL11.glDepthMask(false);
			this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			if (this.mc.gameSettings.fancyGraphics)
			{
				this.mc.mcProfiler.endStartSection("water");
				if (this.mc.gameSettings.ambientOcclusion != 0) {
					GL11.glShadeModel(7425);
				}
				GL11.glEnable(3042);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				if (this.mc.gameSettings.anaglyph)
				{
					if (anaglyphField == 0) {
						GL11.glColorMask(false, true, true, true);
					} else {
						GL11.glColorMask(true, false, false, true);
					}
					renderglobal.sortAndRender(entitylivingbase, 1, p_78471_1_);
				}
				else
				{
					renderglobal.sortAndRender(entitylivingbase, 1, p_78471_1_);
				}
				GL11.glDisable(3042);
				GL11.glShadeModel(7424);
			}
			else
			{
				this.mc.mcProfiler.endStartSection("water");
				renderglobal.sortAndRender(entitylivingbase, 1, p_78471_1_);
			}
			if (this.debugViewDirection == 0)
			{
				RenderHelper.enableStandardItemLighting();
				this.mc.mcProfiler.endStartSection("entities");
				ForgeHooksClient.setRenderPass(1);
				renderglobal.renderEntities(entitylivingbase, frustrum, p_78471_1_);
				ForgeHooksClient.setRenderPass(-1);
				RenderHelper.disableStandardItemLighting();
			}
			GL11.glDepthMask(true);
			GL11.glEnable(2884);
			GL11.glDisable(3042);
			GL11.glDisable(2912);
			if (entitylivingbase.posY >= 128.0D)
			{
				this.mc.mcProfiler.endStartSection("aboveClouds");
				renderCloudsCheck(renderglobal, p_78471_1_);
			}
			this.mc.mcProfiler.endStartSection("FRenderLast");
			ForgeHooksClient.dispatchRenderLast(renderglobal, p_78471_1_);

			this.mc.mcProfiler.endStartSection("hand");
			if ((!ForgeHooksClient.renderFirstPersonHand(renderglobal, p_78471_1_, j)) && (this.cameraZoom == 1.0D))
			{
				GL11.glClear(256);
				renderHand(p_78471_1_, j);
			}
			if (!this.mc.gameSettings.anaglyph)
			{
				this.mc.mcProfiler.endSection();
				return;
			}
		}
		GL11.glColorMask(true, true, true, false);
		this.mc.mcProfiler.endSection();
	}

	private void renderCloudsCheck(RenderGlobal p_82829_1_, float p_82829_2_)
	{
		;
		;
		;
		if (this.mc.gameSettings.shouldRenderClouds())
		{
			this.mc.mcProfiler.endStartSection("clouds");
			GL11.glPushMatrix();
			setupFog(0, p_82829_2_);
			GL11.glEnable(2912);
			p_82829_1_.renderClouds(p_82829_2_);
			GL11.glDisable(2912);
			setupFog(1, p_82829_2_);
			GL11.glPopMatrix();
		}
	}

	private void addRainParticles()
	{
		float f = this.mc.theWorld.getRainStrength(1.0F);
		if (!this.mc.gameSettings.fancyGraphics) {
			f /= 2.0F;
		}
		if (f != 0.0F)
		{
			this.random.setSeed(this.rendererUpdateCount * 312987231L);
			EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
			WorldClient worldclient = this.mc.theWorld;
			int i = MathHelper.floor_double(entitylivingbase.posX);
			int j = MathHelper.floor_double(entitylivingbase.posY);
			int k = MathHelper.floor_double(entitylivingbase.posZ);
			byte b0 = 10;
			double d0 = 0.0D;
			double d1 = 0.0D;
			double d2 = 0.0D;
			int l = 0;
			int i1 = (int)(100.0F * f * f);
			if (this.mc.gameSettings.particleSetting == 1) {
				i1 >>= 1;
			} else if (this.mc.gameSettings.particleSetting == 2) {
				i1 = 0;
			}
			for (int j1 = 0; j1 < i1; j1++)
			{
				int k1 = i + this.random.nextInt(b0) - this.random.nextInt(b0);
				int l1 = k + this.random.nextInt(b0) - this.random.nextInt(b0);
				int i2 = worldclient.getPrecipitationHeight(k1, l1);
				Block block = worldclient.getBlock(k1, i2 - 1, l1);
				BiomeGenBase biomegenbase = worldclient.getBiomeGenForCoords(k1, l1);
				if ((i2 <= j + b0) && (i2 >= j - b0) && (biomegenbase.canSpawnLightningBolt()) && (biomegenbase.getFloatTemperature(k1, i2, l1) >= 0.15F))
				{
					float f1 = this.random.nextFloat();
					float f2 = this.random.nextFloat();
					if (block.getMaterial() == Material.lava)
					{
						this.mc.effectRenderer.addEffect(new EntitySmokeFX(worldclient, k1 + f1, i2 + 0.1F - block.getBlockBoundsMinY(), l1 + f2, 0.0D, 0.0D, 0.0D));
					}
					else if (block.getMaterial() != Material.air)
					{
						l++;
						if (this.random.nextInt(l) == 0)
						{
							d0 = k1 + f1;
							d1 = i2 + 0.1F - block.getBlockBoundsMinY();
							d2 = l1 + f2;
						}
						this.mc.effectRenderer.addEffect(new EntityRainFX(worldclient, k1 + f1, i2 + 0.1F - block.getBlockBoundsMinY(), l1 + f2));
					}
				}
			}
			if ((l > 0) && (this.random.nextInt(3) < this.rainSoundCounter++))
			{
				this.rainSoundCounter = 0;
				if ((d1 > entitylivingbase.posY + 1.0D) && (worldclient.getPrecipitationHeight(MathHelper.floor_double(entitylivingbase.posX), MathHelper.floor_double(entitylivingbase.posZ)) > MathHelper.floor_double(entitylivingbase.posY))) {
					this.mc.theWorld.playSound(d0, d1, d2, "ambient.weather.rain", 0.1F, 0.5F, false);
				} else {
					this.mc.theWorld.playSound(d0, d1, d2, "ambient.weather.rain", 0.2F, 1.0F, false);
				}
			}
		}
	}

	protected void renderRainSnow(float p_78474_1_)
	{
		;
		;
		IRenderHandler renderer = null;
		if ((renderer = this.mc.theWorld.provider.getWeatherRenderer()) != null)
		{
			renderer.render(p_78474_1_, this.mc.theWorld, this.mc);
			return;
		}
		float f1 = this.mc.theWorld.getRainStrength(p_78474_1_);
		if (f1 > 0.0F)
		{
			enableLightmap(p_78474_1_);
			if (this.rainXCoords == null)
			{
				this.rainXCoords = new float[1024];
				this.rainYCoords = new float[1024];
				for (int i = 0; i < 32; i++) {
					for (int j = 0; j < 32; j++)
					{
						float f2 = j - 16;
						float f3 = i - 16;
						float f4 = MathHelper.sqrt_float(f2 * f2 + f3 * f3);
						this.rainXCoords[(i << 5 | j)] = (-f3 / f4);
						this.rainYCoords[(i << 5 | j)] = (f2 / f4);
					}
				}
			}
			EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
			WorldClient worldclient = this.mc.theWorld;
			int k2 = MathHelper.floor_double(entitylivingbase.posX);
			int l2 = MathHelper.floor_double(entitylivingbase.posY);
			int i3 = MathHelper.floor_double(entitylivingbase.posZ);
			Tessellator tessellator = Tessellator.instance;
			GL11.glDisable(2884);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glEnable(3042);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glAlphaFunc(516, 0.1F);
			double d0 = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * p_78474_1_;
			double d1 = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * p_78474_1_;
			double d2 = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * p_78474_1_;
			int k = MathHelper.floor_double(d1);
			byte b0 = 5;
			if (this.mc.gameSettings.fancyGraphics) {
				b0 = 10;
			}
			boolean flag = false;
			byte b1 = -1;
			float f5 = this.rendererUpdateCount + p_78474_1_;
			if (this.mc.gameSettings.fancyGraphics) {
				b0 = 10;
			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			flag = false;
			for (int l = i3 - b0; l <= i3 + b0; l++) {
				for (int i1 = k2 - b0; i1 <= k2 + b0; i1++)
				{
					int j1 = (l - i3 + 16) * 32 + i1 - k2 + 16;
					float f6 = this.rainXCoords[j1] * 0.5F;
					float f7 = this.rainYCoords[j1] * 0.5F;
					BiomeGenBase biomegenbase = worldclient.getBiomeGenForCoords(i1, l);
					if ((biomegenbase.canSpawnLightningBolt()) || (biomegenbase.getEnableSnow()))
					{
						int k1 = worldclient.getPrecipitationHeight(i1, l);
						int l1 = l2 - b0;
						int i2 = l2 + b0;
						if (l1 < k1) {
							l1 = k1;
						}
						if (i2 < k1) {
							i2 = k1;
						}
						float f8 = 1.0F;
						int j2 = k1;
						if (k1 < k) {
							j2 = k;
						}
						if (l1 != i2)
						{
							this.random.setSeed(i1 * i1 * 3121 + i1 * 45238971 ^ l * l * 418711 + l * 13761);
							float f9 = biomegenbase.getFloatTemperature(i1, l1, l);
							if (worldclient.getWorldChunkManager().getTemperatureAtHeight(f9, k1) >= 0.15F)
							{
								if (b1 != 0)
								{
									if (b1 >= 0) {
										tessellator.draw();
									}
									b1 = 0;
									this.mc.getTextureManager().bindTexture(locationRainPng);
									tessellator.startDrawingQuads();
								}
								float f10 = ((this.rendererUpdateCount + i1 * i1 * 3121 + i1 * 45238971 + l * l * 418711 + l * 13761 & 0x1F) + p_78474_1_) / 32.0F * (3.0F + this.random.nextFloat());
								double d3 = i1 + 0.5F - entitylivingbase.posX;
								double d4 = l + 0.5F - entitylivingbase.posZ;
								float f12 = MathHelper.sqrt_double(d3 * d3 + d4 * d4) / b0;
								float f13 = 1.0F;
								tessellator.setBrightness(worldclient.getLightBrightnessForSkyBlocks(i1, j2, l, 0));
								tessellator.setColorRGBA_F(f13, f13, f13, ((1.0F - f12 * f12) * 0.5F + 0.5F) * f1);
								tessellator.setTranslation(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
								tessellator.addVertexWithUV(i1 - f6 + 0.5D, l1, l - f7 + 0.5D, 0.0F * f8, l1 * f8 / 4.0F + f10 * f8);
								tessellator.addVertexWithUV(i1 + f6 + 0.5D, l1, l + f7 + 0.5D, 1.0F * f8, l1 * f8 / 4.0F + f10 * f8);
								tessellator.addVertexWithUV(i1 + f6 + 0.5D, i2, l + f7 + 0.5D, 1.0F * f8, i2 * f8 / 4.0F + f10 * f8);
								tessellator.addVertexWithUV(i1 - f6 + 0.5D, i2, l - f7 + 0.5D, 0.0F * f8, i2 * f8 / 4.0F + f10 * f8);
								tessellator.setTranslation(0.0D, 0.0D, 0.0D);
							}
							else
							{
								if (b1 != 1)
								{
									if (b1 >= 0) {
										tessellator.draw();
									}
									b1 = 1;
									this.mc.getTextureManager().bindTexture(locationSnowPng);
									tessellator.startDrawingQuads();
								}
								float f10 = ((this.rendererUpdateCount & 0x1FF) + p_78474_1_) / 512.0F;
								float f16 = this.random.nextFloat() + f5 * 0.01F * (float)this.random.nextGaussian();
								float f11 = this.random.nextFloat() + f5 * (float)this.random.nextGaussian() * 0.001F;
								double d4 = i1 + 0.5F - entitylivingbase.posX;
								double d5 = l + 0.5F - entitylivingbase.posZ;
								float f14 = MathHelper.sqrt_double(d4 * d4 + d5 * d5) / b0;
								float f15 = 1.0F;
								tessellator.setBrightness((worldclient.getLightBrightnessForSkyBlocks(i1, j2, l, 0) * 3 + 15728880) / 4);
								tessellator.setColorRGBA_F(f15, f15, f15, ((1.0F - f14 * f14) * 0.3F + 0.5F) * f1);
								tessellator.setTranslation(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
								tessellator.addVertexWithUV(i1 - f6 + 0.5D, l1, l - f7 + 0.5D, 0.0F * f8 + f16, l1 * f8 / 4.0F + f10 * f8 + f11);
								tessellator.addVertexWithUV(i1 + f6 + 0.5D, l1, l + f7 + 0.5D, 1.0F * f8 + f16, l1 * f8 / 4.0F + f10 * f8 + f11);
								tessellator.addVertexWithUV(i1 + f6 + 0.5D, i2, l + f7 + 0.5D, 1.0F * f8 + f16, i2 * f8 / 4.0F + f10 * f8 + f11);
								tessellator.addVertexWithUV(i1 - f6 + 0.5D, i2, l - f7 + 0.5D, 0.0F * f8 + f16, i2 * f8 / 4.0F + f10 * f8 + f11);
								tessellator.setTranslation(0.0D, 0.0D, 0.0D);
							}
						}
					}
				}
			}
			if (b1 >= 0) {
				tessellator.draw();
			}
			GL11.glEnable(2884);
			GL11.glDisable(3042);
			GL11.glAlphaFunc(516, 0.1F);
			disableLightmap(p_78474_1_);
		}
	}

	public void setupOverlayRendering()
	{
		ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		GL11.glClear(256);
		GL11.glMatrixMode(5889);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}

	private void updateFogColor(float p_78466_1_)
	{
		;
		;
		WorldClient worldclient = this.mc.theWorld;
		EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
		float f1 = 0.25F + 0.75F * this.mc.gameSettings.renderDistanceChunks / 16.0F;
		f1 = 1.0F - (float)Math.pow(f1, 0.25D);
		Vec3 vec3 = worldclient.getSkyColor(this.mc.renderViewEntity, p_78466_1_);
		float f2 = (float)vec3.xCoord;
		float f3 = (float)vec3.yCoord;
		float f4 = (float)vec3.zCoord;
		Vec3 vec31 = worldclient.getFogColor(p_78466_1_);
		this.fogColorRed = ((float)vec31.xCoord);
		this.fogColorGreen = ((float)vec31.yCoord);
		this.fogColorBlue = ((float)vec31.zCoord);
		if (this.mc.gameSettings.renderDistanceChunks >= 4)
		{
			Vec3 vec32 = MathHelper.sin(worldclient.getCelestialAngleRadians(p_78466_1_)) > 0.0F ? Vec3.createVectorHelper(-1.0D, 0.0D, 0.0D) : Vec3.createVectorHelper(1.0D, 0.0D, 0.0D);
			float f5 = (float)entitylivingbase.getLook(p_78466_1_).dotProduct(vec32);
			if (f5 < 0.0F) {
				f5 = 0.0F;
			}
			if (f5 > 0.0F)
			{
				float[] afloat = worldclient.provider.calcSunriseSunsetColors(worldclient.getCelestialAngle(p_78466_1_), p_78466_1_);
				if (afloat != null)
				{
					f5 *= afloat[3];
					this.fogColorRed = (this.fogColorRed * (1.0F - f5) + afloat[0] * f5);
					this.fogColorGreen = (this.fogColorGreen * (1.0F - f5) + afloat[1] * f5);
					this.fogColorBlue = (this.fogColorBlue * (1.0F - f5) + afloat[2] * f5);
				}
			}
		}
		this.fogColorRed += (f2 - this.fogColorRed) * f1;
		this.fogColorGreen += (f3 - this.fogColorGreen) * f1;
		this.fogColorBlue += (f4 - this.fogColorBlue) * f1;
		float f8 = worldclient.getRainStrength(p_78466_1_);
		if (f8 > 0.0F)
		{
			float f5 = 1.0F - f8 * 0.5F;
			float f9 = 1.0F - f8 * 0.4F;
			this.fogColorRed *= f5;
			this.fogColorGreen *= f5;
			this.fogColorBlue *= f9;
		}
		float f5 = worldclient.getWeightedThunderStrength(p_78466_1_);
		if (f5 > 0.0F)
		{
			float f9 = 1.0F - f5 * 0.5F;
			this.fogColorRed *= f9;
			this.fogColorGreen *= f9;
			this.fogColorBlue *= f9;
		}
		Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entitylivingbase, p_78466_1_);
		if (this.cloudFog)
		{
			Vec3 vec33 = worldclient.getCloudColour(p_78466_1_);
			this.fogColorRed = ((float)vec33.xCoord);
			this.fogColorGreen = ((float)vec33.yCoord);
			this.fogColorBlue = ((float)vec33.zCoord);
		}
		else if (block.getMaterial() == Material.water)
		{
			float f10 = EnchantmentHelper.getRespiration(entitylivingbase) * 0.2F;
			this.fogColorRed = (0.02F + f10);
			this.fogColorGreen = (0.02F + f10);
			this.fogColorBlue = (0.2F + f10);
		}
		else if (block.getMaterial() == Material.lava)
		{
			this.fogColorRed = 0.6F;
			this.fogColorGreen = 0.1F;
			this.fogColorBlue = 0.0F;
		}
		float f10 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * p_78466_1_;
		this.fogColorRed *= f10;
		this.fogColorGreen *= f10;
		this.fogColorBlue *= f10;
		double d0 = (entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * p_78466_1_) * worldclient.provider.getVoidFogYFactor();
		if (entitylivingbase.isPotionActive(Potion.blindness))
		{
			int i = entitylivingbase.getActivePotionEffect(Potion.blindness).getDuration();
			if (i < 20) {
				d0 *= (1.0F - i / 20.0F);
			} else {
				d0 = 0.0D;
			}
		}
		if (d0 < 1.0D)
		{
			if (d0 < 0.0D) {
				d0 = 0.0D;
			}
			d0 *= d0;
			this.fogColorRed = ((float)(this.fogColorRed * d0));
			this.fogColorGreen = ((float)(this.fogColorGreen * d0));
			this.fogColorBlue = ((float)(this.fogColorBlue * d0));
		}
		if (this.bossColorModifier > 0.0F)
		{
			float f11 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * p_78466_1_;
			this.fogColorRed = (this.fogColorRed * (1.0F - f11) + this.fogColorRed * 0.7F * f11);
			this.fogColorGreen = (this.fogColorGreen * (1.0F - f11) + this.fogColorGreen * 0.6F * f11);
			this.fogColorBlue = (this.fogColorBlue * (1.0F - f11) + this.fogColorBlue * 0.6F * f11);
		}
		if (entitylivingbase.isPotionActive(Potion.nightVision))
		{
			float f11 = getNightVisionBrightness(this.mc.thePlayer, p_78466_1_);
			float f6 = 1.0F / this.fogColorRed;
			if (f6 > 1.0F / this.fogColorGreen) {
				f6 = 1.0F / this.fogColorGreen;
			}
			if (f6 > 1.0F / this.fogColorBlue) {
				f6 = 1.0F / this.fogColorBlue;
			}
			this.fogColorRed = (this.fogColorRed * (1.0F - f11) + this.fogColorRed * f6 * f11);
			this.fogColorGreen = (this.fogColorGreen * (1.0F - f11) + this.fogColorGreen * f6 * f11);
			this.fogColorBlue = (this.fogColorBlue * (1.0F - f11) + this.fogColorBlue * f6 * f11);
		}
		if (this.mc.gameSettings.anaglyph)
		{
			float f11 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
			float f6 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
			float f7 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
			this.fogColorRed = f11;
			this.fogColorGreen = f6;
			this.fogColorBlue = f7;
		}
		EntityViewRenderEvent.FogColors event = new EntityViewRenderEvent.FogColors(this, entitylivingbase, block, p_78466_1_, this.fogColorRed, this.fogColorGreen, this.fogColorBlue);
		MinecraftForge.EVENT_BUS.post(event);

		this.fogColorRed = event.red;
		this.fogColorBlue = event.blue;
		this.fogColorGreen = event.green;

		GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
	}

	private void setupFog(int p_78468_1_, float p_78468_2_)
	{
		;
		;
		;
		EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
		boolean flag = false;
		if ((entitylivingbase instanceof EntityPlayer)) {
			flag = ((EntityPlayer)entitylivingbase).capabilities.isCreativeMode;
		}
		if (p_78468_1_ == 999)
		{
			GL11.glFog(2918, setFogColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
			GL11.glFogi(2917, 9729);
			GL11.glFogf(2915, 0.0F);
			GL11.glFogf(2916, 8.0F);
			if (GLContext.getCapabilities().GL_NV_fog_distance) {
				GL11.glFogi(34138, 34139);
			}
			GL11.glFogf(2915, 0.0F);
		}
		else
		{
			GL11.glFog(2918, setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
			GL11.glNormal3f(0.0F, -1.0F, 0.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entitylivingbase, p_78468_2_);


			EntityViewRenderEvent.FogDensity event = new EntityViewRenderEvent.FogDensity(this, entitylivingbase, block, p_78468_2_, 0.1F);
			if (MinecraftForge.EVENT_BUS.post(event))
			{
				GL11.glFogf(2914, event.density);
			}
			else if (entitylivingbase.isPotionActive(Potion.blindness))
			{
				float f1 = 5.0F;
				int j = entitylivingbase.getActivePotionEffect(Potion.blindness).getDuration();
				if (j < 20) {
					f1 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - j / 20.0F);
				}
				GL11.glFogi(2917, 9729);
				if (p_78468_1_ < 0)
				{
					GL11.glFogf(2915, 0.0F);
					GL11.glFogf(2916, f1 * 0.8F);
				}
				else
				{
					GL11.glFogf(2915, f1 * 0.25F);
					GL11.glFogf(2916, f1);
				}
				if (GLContext.getCapabilities().GL_NV_fog_distance) {
					GL11.glFogi(34138, 34139);
				}
			}
			else if (this.cloudFog)
			{
				GL11.glFogi(2917, 2048);
				GL11.glFogf(2914, 0.1F);
			}
			else if (block.getMaterial() == Material.water)
			{
				GL11.glFogi(2917, 2048);
				if (entitylivingbase.isPotionActive(Potion.waterBreathing)) {
					GL11.glFogf(2914, 0.05F);
				} else {
					GL11.glFogf(2914, 0.1F - EnchantmentHelper.getRespiration(entitylivingbase) * 0.03F);
				}
			}
			else if (block.getMaterial() == Material.lava)
			{
				GL11.glFogi(2917, 2048);
				GL11.glFogf(2914, 2.0F);
			}
			else
			{
				float f1 = this.farPlaneDistance;
				if ((this.mc.theWorld.provider.getWorldHasVoidParticles()) && (!flag))
				{
					double d0 = ((entitylivingbase.getBrightnessForRender(p_78468_2_) & 0xF00000) >> 20) / 16.0D + (entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * p_78468_2_ + 4.0D) / 32.0D;
					if (d0 < 1.0D)
					{
						if (d0 < 0.0D) {
							d0 = 0.0D;
						}
						d0 *= d0;
						float f2 = 100.0F * (float)d0;
						if (f2 < 5.0F) {
							f2 = 5.0F;
						}
						if (f1 > f2) {
							f1 = f2;
						}
					}
				}
				GL11.glFogi(2917, 9729);
				if (p_78468_1_ < 0)
				{
					GL11.glFogf(2915, 0.0F);
					GL11.glFogf(2916, f1);
				}
				else
				{
					GL11.glFogf(2915, f1 * 0.75F);
					GL11.glFogf(2916, f1);
				}
				if (GLContext.getCapabilities().GL_NV_fog_distance) {
					GL11.glFogi(34138, 34139);
				}
				if (this.mc.theWorld.provider.doesXZShowFog((int)entitylivingbase.posX, (int)entitylivingbase.posZ))
				{
					GL11.glFogf(2915, f1 * 0.05F);
					GL11.glFogf(2916, Math.min(f1, 192.0F) * 0.5F);
				}
//				MinecraftForge.EVENT_BUS.post(new EntityViewRenderEvent.RenderFogEvent(this, entitylivingbase, block, p_78468_2_, p_78468_1_, f1));
			}
			GL11.glEnable(2903);
			GL11.glColorMaterial(1028, 4608);
		}
	}

	private FloatBuffer setFogColorBuffer(float p_78469_1_, float p_78469_2_, float p_78469_3_, float p_78469_4_)
	{
		;
		;
		;
		;
		;
		this.fogColorBuffer.clear();
		this.fogColorBuffer.put(p_78469_1_).put(p_78469_2_).put(p_78469_3_).put(p_78469_4_);
		this.fogColorBuffer.flip();
		return this.fogColorBuffer;
	}

	public MapItemRenderer getMapItemRenderer()
	{
		return this.theMapItemRenderer;
	}
}
