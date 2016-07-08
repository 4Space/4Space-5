package net.minecraft.entity.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityPlayer extends EntityLivingBase implements ICommandSender
{
	public static final String PERSISTED_NBT_TAG = "PlayerPersisted";
	private HashMap<Integer, ChunkCoordinates> spawnChunkMap = new HashMap();
	private HashMap<Integer, Boolean> spawnForcedMap = new HashMap();
	public InventoryPlayer inventory = new InventoryPlayer(this);
	private InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();
	public Container inventoryContainer;
	public Container openContainer;
	protected FoodStats foodStats = new FoodStats();
	protected int flyToggleTimer;
	public float prevCameraYaw;
	public float cameraYaw;
	public int xpCooldown;
	public double field_71091_bM;
	public double field_71096_bN;
	public double field_71097_bO;
	public double field_71094_bP;
	public double field_71095_bQ;
	public double field_71085_bR;
	public boolean sleeping;
	public ChunkCoordinates playerLocation;
	public int sleepTimer;
	public float field_71079_bU;
	@SideOnly(Side.CLIENT)
	public float field_71082_cx;
	public float field_71089_bV;
	private ChunkCoordinates spawnChunk;
	private boolean spawnForced;
	private ChunkCoordinates startMinecartRidingCoordinate;
	public PlayerCapabilities capabilities = new PlayerCapabilities();
	public int experienceLevel;
	public int experienceTotal;
	public float experience;
	private ItemStack itemInUse;
	private int itemInUseCount;
	protected float speedOnGround = 0.1F;
	protected float speedInAir = 0.02F;
	private int field_82249_h;
	private final GameProfile field_146106_i;
	public EntityFishHook fishEntity;
	private static final String __OBFID = "CL_00001711";
	public float eyeHeight;
	private String displayname;

	public EntityPlayer(World p_i45324_1_, GameProfile p_i45324_2_)
	{
		super(p_i45324_1_);
		this.entityUniqueID = func_146094_a(p_i45324_2_);
		this.field_146106_i = p_i45324_2_;
		this.inventoryContainer = new ContainerPlayer(this.inventory, !p_i45324_1_.isRemote, this);
		this.openContainer = this.inventoryContainer;
		this.yOffset = 1.62F;
		ChunkCoordinates chunkcoordinates = p_i45324_1_.getSpawnPoint();
		setLocationAndAngles(chunkcoordinates.posX + 0.5D, chunkcoordinates.posY + 1, chunkcoordinates.posZ + 0.5D, 0.0F, 0.0F);
		this.field_70741_aB = 180.0F;
		this.fireResistance = 20;
		this.eyeHeight = getDefaultEyeHeight();
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
	}

	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
		this.dataWatcher.addObject(17, Float.valueOf(0.0F));
		this.dataWatcher.addObject(18, Integer.valueOf(0));
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getItemInUse()
	{
		return this.itemInUse;
	}

	@SideOnly(Side.CLIENT)
	public int getItemInUseCount()
	{
		return this.itemInUseCount;
	}

	public boolean isUsingItem()
	{
		return this.itemInUse != null;
	}

	@SideOnly(Side.CLIENT)
	public int getItemInUseDuration()
	{
		return isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
	}

	public void stopUsingItem()
	{
		if (this.itemInUse != null) {
			if (!ForgeEventFactory.onUseItemStop(this, this.itemInUse, this.itemInUseCount)) {
				this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
			}
		}
		clearItemInUse();
	}

	public void clearItemInUse()
	{
		this.itemInUse = null;
		this.itemInUseCount = 0;
		if (!this.worldObj.isRemote) {
			setEating(false);
		}
	}

	public boolean isBlocking()
	{
		return (isUsingItem()) && (this.itemInUse.getItem().getItemUseAction(this.itemInUse) == EnumAction.block);
	}

	public void onUpdate()
	{
		FMLCommonHandler.instance().onPlayerPreTick(this);
		if (this.itemInUse != null)
		{
			ItemStack itemstack = this.inventory.getCurrentItem();
			if (itemstack == this.itemInUse)
			{
				this.itemInUseCount = ForgeEventFactory.onItemUseTick(this, this.itemInUse, this.itemInUseCount);
				if (this.itemInUseCount <= 0)
				{
					onItemUseFinish();
				}
				else
				{
					this.itemInUse.getItem().onUsingTick(this.itemInUse, this, this.itemInUseCount);
					if ((this.itemInUseCount <= 25) && (this.itemInUseCount % 4 == 0)) {
						updateItemUse(itemstack, 5);
					}
					if ((--this.itemInUseCount == 0) && (!this.worldObj.isRemote)) {
						onItemUseFinish();
					}
				}
			}
			else
			{
				clearItemInUse();
			}
		}
		if (this.xpCooldown > 0) {
			this.xpCooldown -= 1;
		}
		if (isPlayerSleeping())
		{
			this.sleepTimer += 1;
			if (this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}
			if (!this.worldObj.isRemote) {
				if (!isInBed()) {
					wakeUpPlayer(true, true, false);
				} else if (this.worldObj.isDaytime()) {
					wakeUpPlayer(false, true, true);
				}
			}
		}
		else if (this.sleepTimer > 0)
		{
			this.sleepTimer += 1;
			if (this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}
		super.onUpdate();
		if ((!this.worldObj.isRemote) && (this.openContainer != null) && (!ForgeHooks.canInteractWith(this, this.openContainer)))
		{
			closeScreen();
			this.openContainer = this.inventoryContainer;
		}
		if ((isBurning()) && (this.capabilities.disableDamage)) {
			extinguish();
		}
		this.field_71091_bM = this.field_71094_bP;
		this.field_71096_bN = this.field_71095_bQ;
		this.field_71097_bO = this.field_71085_bR;
		double d3 = this.posX - this.field_71094_bP;
		double d0 = this.posY - this.field_71095_bQ;
		double d1 = this.posZ - this.field_71085_bR;
		double d2 = 10.0D;
		if (d3 > d2) {
			this.field_71091_bM = (this.field_71094_bP = this.posX);
		}
		if (d1 > d2) {
			this.field_71097_bO = (this.field_71085_bR = this.posZ);
		}
		if (d0 > d2) {
			this.field_71096_bN = (this.field_71095_bQ = this.posY);
		}
		if (d3 < -d2) {
			this.field_71091_bM = (this.field_71094_bP = this.posX);
		}
		if (d1 < -d2) {
			this.field_71097_bO = (this.field_71085_bR = this.posZ);
		}
		if (d0 < -d2) {
			this.field_71096_bN = (this.field_71095_bQ = this.posY);
		}
		this.field_71094_bP += d3 * 0.25D;
		this.field_71085_bR += d1 * 0.25D;
		this.field_71095_bQ += d0 * 0.25D;
		if (this.ridingEntity == null) {
			this.startMinecartRidingCoordinate = null;
		}
		if (!this.worldObj.isRemote)
		{
			this.foodStats.onUpdate(this);
			addStat(StatList.minutesPlayedStat, 1);
		}
		FMLCommonHandler.instance().onPlayerPostTick(this);
	}

	public int getMaxInPortalTime()
	{
		return this.capabilities.disableDamage ? 0 : 80;
	}

	protected String getSwimSound()
	{
		return "game.player.swim";
	}

	protected String getSplashSound()
	{
		return "game.player.swim.splash";
	}

	public int getPortalCooldown()
	{
		return 10;
	}

	public void playSound(String p_85030_1_, float p_85030_2_, float p_85030_3_)
	{
		this.worldObj.playSoundToNearExcept(this, p_85030_1_, p_85030_2_, p_85030_3_);
	}

	protected void updateItemUse(ItemStack p_71010_1_, int p_71010_2_)
	{
		if (p_71010_1_.getItemUseAction() == EnumAction.drink) {
			playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
		if (p_71010_1_.getItemUseAction() == EnumAction.eat)
		{
			for (int j = 0; j < p_71010_2_; j++)
			{
				Vec3 vec3 = Vec3.createVectorHelper((this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
				vec3.rotateAroundX(-this.rotationPitch * 3.141593F / 180.0F);
				vec3.rotateAroundY(-this.rotationYaw * 3.141593F / 180.0F);
				Vec3 vec31 = Vec3.createVectorHelper((this.rand.nextFloat() - 0.5D) * 0.3D, -this.rand.nextFloat() * 0.6D - 0.3D, 0.6D);
				vec31.rotateAroundX(-this.rotationPitch * 3.141593F / 180.0F);
				vec31.rotateAroundY(-this.rotationYaw * 3.141593F / 180.0F);
				vec31 = vec31.addVector(this.posX, this.posY + getEyeHeight(), this.posZ);
				String s = "iconcrack_" + Item.getIdFromItem(p_71010_1_.getItem());
				if (p_71010_1_.getHasSubtypes()) {
					s = s + "_" + p_71010_1_.getItemDamage();
				}
				this.worldObj.spawnParticle(s, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord);
			}
			playSound("random.eat", 0.5F + 0.5F * this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		}
	}

	protected void onItemUseFinish()
	{
		if (this.itemInUse != null)
		{
			updateItemUse(this.itemInUse, 16);
			int i = this.itemInUse.stackSize;
			ItemStack itemstack = this.itemInUse.onFoodEaten(this.worldObj, this);

			itemstack = ForgeEventFactory.onItemUseFinish(this, this.itemInUse, this.itemInUseCount, itemstack);
			if ((itemstack != this.itemInUse) || ((itemstack != null) && (itemstack.stackSize != i)))
			{
				this.inventory.mainInventory[this.inventory.currentItem] = itemstack;
				if ((itemstack != null) && (itemstack.stackSize == 0)) {
					this.inventory.mainInventory[this.inventory.currentItem] = null;
				}
			}
			clearItemInUse();
		}
	}

	@SideOnly(Side.CLIENT)
	public void handleHealthUpdate(byte p_70103_1_)
	{
		if (p_70103_1_ == 9) {
			onItemUseFinish();
		} else {
			super.handleHealthUpdate(p_70103_1_);
		}
	}

	protected boolean isMovementBlocked()
	{
		return (getHealth() <= 0.0F) || (isPlayerSleeping());
	}

	public void closeScreen()
	{
		this.openContainer = this.inventoryContainer;
	}

	public void mountEntity(Entity p_70078_1_)
	{
		if ((this.ridingEntity != null) && (p_70078_1_ == null))
		{
			if (!this.worldObj.isRemote) {
				dismountEntity(this.ridingEntity);
			}
			if (this.ridingEntity != null) {
				this.ridingEntity.riddenByEntity = null;
			}
			this.ridingEntity = null;
		}
		else
		{
			super.mountEntity(p_70078_1_);
		}
	}

	public void updateRidden()
	{
		if ((!this.worldObj.isRemote) && (isSneaking()))
		{
			mountEntity((Entity)null);
			setSneaking(false);
		}
		else
		{
			double d0 = this.posX;
			double d1 = this.posY;
			double d2 = this.posZ;
			float f = this.rotationYaw;
			float f1 = this.rotationPitch;
			super.updateRidden();
			this.prevCameraYaw = this.cameraYaw;
			this.cameraYaw = 0.0F;
			addMountedMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);
			if (((this.ridingEntity instanceof EntityLivingBase)) && (((EntityLivingBase)this.ridingEntity).shouldRiderFaceForward(this)))
			{
				this.rotationPitch = f1;
				this.rotationYaw = f;
				this.renderYawOffset = ((EntityLivingBase)this.ridingEntity).renderYawOffset;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void preparePlayerToSpawn()
	{
		this.yOffset = 1.62F;
		setSize(0.6F, 1.8F);
		super.preparePlayerToSpawn();
		setHealth(getMaxHealth());
		this.deathTime = 0;
	}

	protected void updateEntityActionState()
	{
		super.updateEntityActionState();
		updateArmSwingProgress();
	}

	public void onLivingUpdate()
	{
		if (this.flyToggleTimer > 0) {
			this.flyToggleTimer -= 1;
		}
		if ((this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) && (getHealth() < getMaxHealth()) && (this.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration")) && (this.ticksExisted % 20 * 12 == 0)) {
			heal(1.0F);
		}
		this.inventory.decrementAnimations();
		this.prevCameraYaw = this.cameraYaw;
		super.onLivingUpdate();
		IAttributeInstance iattributeinstance = getEntityAttribute(SharedMonsterAttributes.movementSpeed);
		if (!this.worldObj.isRemote) {
			iattributeinstance.setBaseValue(this.capabilities.getWalkSpeed());
		}
		this.jumpMovementFactor = this.speedInAir;
		if (isSprinting()) {
			this.jumpMovementFactor = ((float)(this.jumpMovementFactor + this.speedInAir * 0.3D));
		}
		setAIMoveSpeed((float)iattributeinstance.getAttributeValue());
		float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float f1 = (float)Math.atan(-this.motionY * 0.2000000029802322D) * 15.0F;
		if (f > 0.1F) {
			f = 0.1F;
		}
		if ((!this.onGround) || (getHealth() <= 0.0F)) {
			f = 0.0F;
		}
		if ((this.onGround) || (getHealth() <= 0.0F)) {
			f1 = 0.0F;
		}
		this.cameraYaw += (f - this.cameraYaw) * 0.4F;
		this.cameraPitch += (f1 - this.cameraPitch) * 0.8F;
		if (getHealth() > 0.0F)
		{
			AxisAlignedBB axisalignedbb = null;
			if ((this.ridingEntity != null) && (!this.ridingEntity.isDead)) {
				axisalignedbb = this.boundingBox.func_111270_a(this.ridingEntity.boundingBox).expand(1.0D, 0.0D, 1.0D);
			} else {
				axisalignedbb = this.boundingBox.expand(1.0D, 0.5D, 1.0D);
			}
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb);
			if (list != null) {
				for (int i = 0; i < list.size(); i++)
				{
					Entity entity = (Entity)list.get(i);
					if (!entity.isDead) {
						collideWithPlayer(entity);
					}
				}
			}
		}
	}

	private void collideWithPlayer(Entity p_71044_1_)
	{
		p_71044_1_.onCollideWithPlayer(this);
	}

	public int getScore()
	{
		return this.dataWatcher.getWatchableObjectInt(18);
	}

	public void setScore(int p_85040_1_)
	{
		this.dataWatcher.updateObject(18, Integer.valueOf(p_85040_1_));
	}

	public void addScore(int p_85039_1_)
	{
		int j = getScore();
		this.dataWatcher.updateObject(18, Integer.valueOf(j + p_85039_1_));
	}

	public void onDeath(DamageSource p_70645_1_)
	{
		if (ForgeHooks.onLivingDeath(this, p_70645_1_)) {
			return;
		}
		super.onDeath(p_70645_1_);
		setSize(0.2F, 0.2F);
		setPosition(this.posX, this.posY, this.posZ);
		this.motionY = 0.1000000014901161D;

		this.captureDrops = true;
		this.capturedDrops.clear();
		if (getCommandSenderName().equals("Notch")) {
			func_146097_a(new ItemStack(Items.apple, 1), true, false);
		}
		if (!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
			this.inventory.dropAllItems();
		}
		this.captureDrops = false;
		if (!this.worldObj.isRemote)
		{
			PlayerDropsEvent event = new PlayerDropsEvent(this, p_70645_1_, this.capturedDrops, this.recentlyHit > 0);
			if (!MinecraftForge.EVENT_BUS.post(event)) {
				for (EntityItem item : this.capturedDrops) {
					joinEntityItemWithWorld(item);
				}
			}
		}
		if (p_70645_1_ != null)
		{
			this.motionX = (-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * 3.141593F / 180.0F) * 0.1F);
			this.motionZ = (-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * 3.141593F / 180.0F) * 0.1F);
		}
		else
		{
			this.motionX = (this.motionZ = 0.0D);
		}
		this.yOffset = 0.1F;
		addStat(StatList.deathsStat, 1);
	}

	protected String getHurtSound()
	{
		return "game.player.hurt";
	}

	protected String getDeathSound()
	{
		return "game.player.die";
	}

	public void addToPlayerScore(Entity p_70084_1_, int p_70084_2_)
	{
		addScore(p_70084_2_);
		Collection collection = getWorldScoreboard().func_96520_a(IScoreObjectiveCriteria.totalKillCount);
		if ((p_70084_1_ instanceof EntityPlayer))
		{
			addStat(StatList.playerKillsStat, 1);
			collection.addAll(getWorldScoreboard().func_96520_a(IScoreObjectiveCriteria.playerKillCount));
		}
		else
		{
			addStat(StatList.mobKillsStat, 1);
		}
		Iterator iterator = collection.iterator();
		while (iterator.hasNext())
		{
			ScoreObjective scoreobjective = (ScoreObjective)iterator.next();
			Score score = getWorldScoreboard().func_96529_a(getCommandSenderName(), scoreobjective);
			score.func_96648_a();
		}
	}

	public EntityItem dropOneItem(boolean p_71040_1_)
	{
		ItemStack stack = this.inventory.getCurrentItem();
		if (stack == null) {
			return null;
		}
		if (stack.getItem().onDroppedByPlayer(stack, this))
		{
			int count = (p_71040_1_) && (this.inventory.getCurrentItem() != null) ? this.inventory.getCurrentItem().stackSize : 1;
			return ForgeHooks.onPlayerTossEvent(this, this.inventory.decrStackSize(this.inventory.currentItem, count), true);
		}
		return null;
	}

	public EntityItem dropPlayerItemWithRandomChoice(ItemStack p_71019_1_, boolean p_71019_2_)
	{
		return ForgeHooks.onPlayerTossEvent(this, p_71019_1_, false);
	}

	public EntityItem func_146097_a(ItemStack p_146097_1_, boolean p_146097_2_, boolean p_146097_3_)
	{
		if (p_146097_1_ == null) {
			return null;
		}
		if (p_146097_1_.stackSize == 0) {
			return null;
		}
		EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY - 0.300000011920929D + getEyeHeight(), this.posZ, p_146097_1_);
		entityitem.delayBeforeCanPickup = 40;
		if (p_146097_3_) {
			entityitem.func_145799_b(getCommandSenderName());
		}
		float f = 0.1F;
		if (p_146097_2_)
		{
			float f1 = this.rand.nextFloat() * 0.5F;
			float f2 = this.rand.nextFloat() * 3.141593F * 2.0F;
			entityitem.motionX = (-MathHelper.sin(f2) * f1);
			entityitem.motionZ = (MathHelper.cos(f2) * f1);
			entityitem.motionY = 0.2000000029802322D;
		}
		else
		{
			f = 0.3F;
			entityitem.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F) * f);
			entityitem.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F) * f);
			entityitem.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * 3.141593F) * f + 0.1F);
			f = 0.02F;
			float f1 = this.rand.nextFloat() * 3.141593F * 2.0F;
			f *= this.rand.nextFloat();
			entityitem.motionX += Math.cos(f1) * f;
			entityitem.motionY += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
			entityitem.motionZ += Math.sin(f1) * f;
		}
		joinEntityItemWithWorld(entityitem);
		addStat(StatList.dropStat, 1);
		return entityitem;
	}

	public void joinEntityItemWithWorld(EntityItem p_71012_1_)
	{
		if (this.captureDrops)
		{
			this.capturedDrops.add(p_71012_1_);
			return;
		}
		this.worldObj.spawnEntityInWorld(p_71012_1_);
	}

	@Deprecated
	public float getCurrentPlayerStrVsBlock(Block p_146096_1_, boolean p_146096_2_)
	{
		return getBreakSpeed(p_146096_1_, p_146096_2_, 0, 0, -1, 0);
	}

	@Deprecated
	public float getBreakSpeed(Block p_146096_1_, boolean p_146096_2_, int meta)
	{
		return getBreakSpeed(p_146096_1_, p_146096_2_, meta, 0, -1, 0);
	}

	public float getBreakSpeed(Block p_146096_1_, boolean p_146096_2_, int meta, int x, int y, int z)
	{
		ItemStack stack = this.inventory.getCurrentItem();
		float f = stack == null ? 1.0F : stack.getItem().getDigSpeed(stack, p_146096_1_, meta);
		if (f > 1.0F)
		{
			int i = EnchantmentHelper.getEfficiencyModifier(this);
			ItemStack itemstack = this.inventory.getCurrentItem();
			if ((i > 0) && (itemstack != null))
			{
				float f1 = i * i + 1;

				boolean canHarvest = ForgeHooks.canToolHarvestBlock(p_146096_1_, meta, itemstack);
				if ((!canHarvest) && (f <= 1.0F)) {
					f += f1 * 0.08F;
				} else {
					f += f1;
				}
			}
		}
		if (isPotionActive(Potion.digSpeed)) {
			f *= (1.0F + (getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F);
		}
		if (isPotionActive(Potion.digSlowdown)) {
			f *= (1.0F - (getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1) * 0.2F);
		}
		if ((isInsideOfMaterial(Material.water)) && (!EnchantmentHelper.getAquaAffinityModifier(this))) {
			f /= 5.0F;
		}
		if (!this.onGround) {
			f /= 5.0F;
		}
		f = ForgeEventFactory.getBreakSpeed(this, p_146096_1_, meta, f, x, y, z);
		return f < 0.0F ? 0.0F : f;
	}

	public boolean canHarvestBlock(Block p_146099_1_)
	{
		return ForgeEventFactory.doPlayerHarvestCheck(this, p_146099_1_, this.inventory.func_146025_b(p_146099_1_));
	}

	public void readEntityFromNBT(NBTTagCompound p_70037_1_)
	{
		super.readEntityFromNBT(p_70037_1_);
		this.entityUniqueID = func_146094_a(this.field_146106_i);
		NBTTagList nbttaglist = p_70037_1_.getTagList("Inventory", 10);
		this.inventory.readFromNBT(nbttaglist);
		this.inventory.currentItem = p_70037_1_.getInteger("SelectedItemSlot");
		this.sleeping = p_70037_1_.getBoolean("Sleeping");
		this.sleepTimer = p_70037_1_.getShort("SleepTimer");
		this.experience = p_70037_1_.getFloat("XpP");
		this.experienceLevel = p_70037_1_.getInteger("XpLevel");
		this.experienceTotal = p_70037_1_.getInteger("XpTotal");
		setScore(p_70037_1_.getInteger("Score"));
		if (this.sleeping)
		{
			this.playerLocation = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
			wakeUpPlayer(true, true, false);
		}
		if ((p_70037_1_.hasKey("SpawnX", 99)) && (p_70037_1_.hasKey("SpawnY", 99)) && (p_70037_1_.hasKey("SpawnZ", 99)))
		{
			this.spawnChunk = new ChunkCoordinates(p_70037_1_.getInteger("SpawnX"), p_70037_1_.getInteger("SpawnY"), p_70037_1_.getInteger("SpawnZ"));
			this.spawnForced = p_70037_1_.getBoolean("SpawnForced");
		}
		NBTTagList spawnlist = null;
		spawnlist = p_70037_1_.getTagList("Spawns", 10);
		for (int i = 0; i < spawnlist.tagCount(); i++)
		{
			NBTTagCompound spawndata = spawnlist.getCompoundTagAt(i);
			int spawndim = spawndata.getInteger("Dim");
			this.spawnChunkMap.put(Integer.valueOf(spawndim), new ChunkCoordinates(spawndata.getInteger("SpawnX"), spawndata.getInteger("SpawnY"), spawndata.getInteger("SpawnZ")));
			this.spawnForcedMap.put(Integer.valueOf(spawndim), Boolean.valueOf(spawndata.getBoolean("SpawnForced")));
		}
		this.foodStats.readNBT(p_70037_1_);
		this.capabilities.readCapabilitiesFromNBT(p_70037_1_);
		if (p_70037_1_.hasKey("EnderItems", 9))
		{
			NBTTagList nbttaglist1 = p_70037_1_.getTagList("EnderItems", 10);
			this.theInventoryEnderChest.loadInventoryFromNBT(nbttaglist1);
		}
	}

	public void writeEntityToNBT(NBTTagCompound p_70014_1_)
	{
		super.writeEntityToNBT(p_70014_1_);
		p_70014_1_.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
		p_70014_1_.setInteger("SelectedItemSlot", this.inventory.currentItem);
		p_70014_1_.setBoolean("Sleeping", this.sleeping);
		p_70014_1_.setShort("SleepTimer", (short)this.sleepTimer);
		p_70014_1_.setFloat("XpP", this.experience);
		p_70014_1_.setInteger("XpLevel", this.experienceLevel);
		p_70014_1_.setInteger("XpTotal", this.experienceTotal);
		p_70014_1_.setInteger("Score", getScore());
		if (this.spawnChunk != null)
		{
			p_70014_1_.setInteger("SpawnX", this.spawnChunk.posX);
			p_70014_1_.setInteger("SpawnY", this.spawnChunk.posY);
			p_70014_1_.setInteger("SpawnZ", this.spawnChunk.posZ);
			p_70014_1_.setBoolean("SpawnForced", this.spawnForced);
		}
		NBTTagList spawnlist = new NBTTagList();
		for (Map.Entry<Integer, ChunkCoordinates> entry : this.spawnChunkMap.entrySet())
		{
			ChunkCoordinates spawn = (ChunkCoordinates)entry.getValue();
			if (spawn != null)
			{
				Boolean forced = (Boolean)this.spawnForcedMap.get(entry.getKey());
				if (forced == null) {
					forced = Boolean.valueOf(false);
				}
				NBTTagCompound spawndata = new NBTTagCompound();
				spawndata.setInteger("Dim", ((Integer)entry.getKey()).intValue());
				spawndata.setInteger("SpawnX", spawn.posX);
				spawndata.setInteger("SpawnY", spawn.posY);
				spawndata.setInteger("SpawnZ", spawn.posZ);
				spawndata.setBoolean("SpawnForced", forced.booleanValue());
				spawnlist.appendTag(spawndata);
			}
		}
		p_70014_1_.setTag("Spawns", spawnlist);

		this.foodStats.writeNBT(p_70014_1_);
		this.capabilities.writeCapabilitiesToNBT(p_70014_1_);
		p_70014_1_.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
	}

	public void displayGUIChest(IInventory p_71007_1_) {}

	public void func_146093_a(TileEntityHopper p_146093_1_) {}

	public void displayGUIHopperMinecart(EntityMinecartHopper p_96125_1_) {}

	public void displayGUIHorse(EntityHorse p_110298_1_, IInventory p_110298_2_) {}

	public void displayGUIEnchantment(int p_71002_1_, int p_71002_2_, int p_71002_3_, String p_71002_4_) {}

	public void displayGUIAnvil(int p_82244_1_, int p_82244_2_, int p_82244_3_) {}

	public void displayGUIWorkbench(int p_71058_1_, int p_71058_2_, int p_71058_3_) {}

	public float getEyeHeight()
	{
		return this.eyeHeight;
	}

	protected void resetHeight()
	{
		this.yOffset = 1.62F;
	}

	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
	{
		if (ForgeHooks.onLivingAttack(this, p_70097_1_, p_70097_2_)) {
			return false;
		}
		if (isEntityInvulnerable()) {
			return false;
		}
		if ((this.capabilities.disableDamage) && (!p_70097_1_.canHarmInCreative())) {
			return false;
		}
		this.entityAge = 0;
		if (getHealth() <= 0.0F) {
			return false;
		}
		if ((isPlayerSleeping()) && (!this.worldObj.isRemote)) {
			wakeUpPlayer(true, true, false);
		}
		if (p_70097_1_.isDifficultyScaled())
		{
			if (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
				p_70097_2_ = 0.0F;
			}
			if (this.worldObj.difficultySetting == EnumDifficulty.EASY) {
				p_70097_2_ = p_70097_2_ / 2.0F + 1.0F;
			}
			if (this.worldObj.difficultySetting == EnumDifficulty.HARD) {
				p_70097_2_ = p_70097_2_ * 3.0F / 2.0F;
			}
		}
		if (p_70097_2_ == 0.0F) {
			return false;
		}
		Entity entity = p_70097_1_.getEntity();
		if (((entity instanceof EntityArrow)) && (((EntityArrow)entity).shootingEntity != null)) {
			entity = ((EntityArrow)entity).shootingEntity;
		}
		addStat(StatList.damageTakenStat, Math.round(p_70097_2_ * 10.0F));
		return super.attackEntityFrom(p_70097_1_, p_70097_2_);
	}

    public boolean canAttackPlayer(EntityPlayer p_96122_1_)
    {
        Team var2 = this.getTeam();
        Team var3 = p_96122_1_.getTeam();
        return var2 == null ? true : (!var2.isSameTeam(var3) ? true : var2.getAllowFriendlyFire());
    }

	protected void damageArmor(float p_70675_1_)
	{
		this.inventory.damageArmor(p_70675_1_);
	}

	public int getTotalArmorValue()
	{
		return this.inventory.getTotalArmorValue();
	}

	public float getArmorVisibility()
	{
		int i = 0;
		ItemStack[] aitemstack = this.inventory.armorInventory;
		int j = aitemstack.length;
		for (int k = 0; k < j; k++)
		{
			ItemStack itemstack = aitemstack[k];
			if (itemstack != null) {
				i++;
			}
		}
		return i / this.inventory.armorInventory.length;
	}

	protected void damageEntity(DamageSource p_70665_1_, float p_70665_2_)
	{
		if (!isEntityInvulnerable())
		{
			p_70665_2_ = ForgeHooks.onLivingHurt(this, p_70665_1_, p_70665_2_);
			if (p_70665_2_ <= 0.0F) {
				return;
			}
			if ((!p_70665_1_.isUnblockable()) && (isBlocking()) && (p_70665_2_ > 0.0F)) {
				p_70665_2_ = (1.0F + p_70665_2_) * 0.5F;
			}
			p_70665_2_ = ISpecialArmor.ArmorProperties.ApplyArmor(this, this.inventory.armorInventory, p_70665_1_, p_70665_2_);
			if (p_70665_2_ <= 0.0F) {
				return;
			}
			p_70665_2_ = applyPotionDamageCalculations(p_70665_1_, p_70665_2_);
			float f1 = p_70665_2_;
			p_70665_2_ = Math.max(p_70665_2_ - getAbsorptionAmount(), 0.0F);
			setAbsorptionAmount(getAbsorptionAmount() - (f1 - p_70665_2_));
			if (p_70665_2_ != 0.0F)
			{
				addExhaustion(p_70665_1_.getHungerDamage());
				float f2 = getHealth();
				setHealth(getHealth() - p_70665_2_);
				func_110142_aN().func_94547_a(p_70665_1_, f2, p_70665_2_);
			}
		}
	}

	public void func_146101_a(TileEntityFurnace p_146101_1_) {}

	public void func_146102_a(TileEntityDispenser p_146102_1_) {}

	public void func_146100_a(TileEntity p_146100_1_) {}

	public void func_146095_a(CommandBlockLogic p_146095_1_) {}

	public void func_146098_a(TileEntityBrewingStand p_146098_1_) {}

	public void func_146104_a(TileEntityBeacon p_146104_1_) {}

	public void displayGUIMerchant(IMerchant p_71030_1_, String p_71030_2_) {}

	public void displayGUIBook(ItemStack p_71048_1_) {}

	public boolean interactWith(Entity p_70998_1_)
	{
		if (MinecraftForge.EVENT_BUS.post(new EntityInteractEvent(this, p_70998_1_))) {
			return false;
		}
		ItemStack itemstack = getCurrentEquippedItem();
		ItemStack itemstack1 = itemstack != null ? itemstack.copy() : null;
		if (!p_70998_1_.interactFirst(this))
		{
			if ((itemstack != null) && ((p_70998_1_ instanceof EntityLivingBase)))
			{
				if (this.capabilities.isCreativeMode) {
					itemstack = itemstack1;
				}
				if (itemstack.interactWithEntity(this, (EntityLivingBase)p_70998_1_))
				{
					if ((itemstack.stackSize <= 0) && (!this.capabilities.isCreativeMode)) {
						destroyCurrentEquippedItem();
					}
					return true;
				}
			}
			return false;
		}
		if ((itemstack != null) && (itemstack == getCurrentEquippedItem())) {
			if ((itemstack.stackSize <= 0) && (!this.capabilities.isCreativeMode)) {
				destroyCurrentEquippedItem();
			} else if ((itemstack.stackSize < itemstack1.stackSize) && (this.capabilities.isCreativeMode)) {
				itemstack.stackSize = itemstack1.stackSize;
			}
		}
		return true;
	}

	public ItemStack getCurrentEquippedItem()
	{
		return this.inventory.getCurrentItem();
	}

	public void destroyCurrentEquippedItem()
	{
		ItemStack orig = getCurrentEquippedItem();
		this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack)null);
		MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this, orig));
	}

	public double getYOffset()
	{
		return this.yOffset - 0.5F;
	}

	public void attackTargetEntityWithCurrentItem(Entity p_71059_1_)
	{
		if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(this, p_71059_1_))) {
			return;
		}
		ItemStack stack = getCurrentEquippedItem();
		if ((stack != null) && (stack.getItem().onLeftClickEntity(stack, this, p_71059_1_))) {
			return;
		}
		if (p_71059_1_.canAttackWithItem()) {
			if (!p_71059_1_.hitByEntity(this))
			{
				float f = (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
				int i = 0;
				float f1 = 0.0F;
				if ((p_71059_1_ instanceof EntityLivingBase))
				{
					f1 = EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)p_71059_1_);
					i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)p_71059_1_);
				}
				if (isSprinting()) {
					i++;
				}
				if ((f > 0.0F) || (f1 > 0.0F))
				{
					boolean flag = (this.fallDistance > 0.0F) && (!this.onGround) && (!isOnLadder()) && (!isInWater()) && (!isPotionActive(Potion.blindness)) && (this.ridingEntity == null) && ((p_71059_1_ instanceof EntityLivingBase));
					if ((flag) && (f > 0.0F)) {
						f *= 1.5F;
					}
					f += f1;
					boolean flag1 = false;
					int j = EnchantmentHelper.getFireAspectModifier(this);
					if (((p_71059_1_ instanceof EntityLivingBase)) && (j > 0) && (!p_71059_1_.isBurning()))
					{
						flag1 = true;
						p_71059_1_.setFire(1);
					}
					boolean flag2 = p_71059_1_.attackEntityFrom(DamageSource.causePlayerDamage(this), f);
					if (flag2)
					{
						if (i > 0)
						{
							p_71059_1_.addVelocity(-MathHelper.sin(this.rotationYaw * 3.141593F / 180.0F) * i * 0.5F, 0.1D, MathHelper.cos(this.rotationYaw * 3.141593F / 180.0F) * i * 0.5F);
							this.motionX *= 0.6D;
							this.motionZ *= 0.6D;
							setSprinting(false);
						}
						if (flag) {
							onCriticalHit(p_71059_1_);
						}
						if (f1 > 0.0F) {
							onEnchantmentCritical(p_71059_1_);
						}
						if (f >= 18.0F) {
							triggerAchievement(AchievementList.overkill);
						}
						setLastAttacker(p_71059_1_);
						if ((p_71059_1_ instanceof EntityLivingBase)) {
							EnchantmentHelper.func_151384_a((EntityLivingBase)p_71059_1_, this);
						}
						EnchantmentHelper.func_151385_b(this, p_71059_1_);
						ItemStack itemstack = getCurrentEquippedItem();
						Object object = p_71059_1_;
						if ((p_71059_1_ instanceof EntityDragonPart))
						{
							IEntityMultiPart ientitymultipart = ((EntityDragonPart)p_71059_1_).entityDragonObj;
							if ((ientitymultipart != null) && ((ientitymultipart instanceof EntityLivingBase))) {
								object = (EntityLivingBase)ientitymultipart;
							}
						}
						if ((itemstack != null) && ((object instanceof EntityLivingBase)))
						{
							itemstack.hitEntity((EntityLivingBase)object, this);
							if (itemstack.stackSize <= 0) {
								destroyCurrentEquippedItem();
							}
						}
						if ((p_71059_1_ instanceof EntityLivingBase))
						{
							addStat(StatList.damageDealtStat, Math.round(f * 10.0F));
							if (j > 0) {
								p_71059_1_.setFire(j * 4);
							}
						}
						addExhaustion(0.3F);
					}
					else if (flag1)
					{
						p_71059_1_.extinguish();
					}
				}
			}
		}
	}

	public void onCriticalHit(Entity p_71009_1_) {}

	public void onEnchantmentCritical(Entity p_71047_1_) {}

	@SideOnly(Side.CLIENT)
	public void respawnPlayer() {}

	public void setDead()
	{
		super.setDead();
		this.inventoryContainer.onContainerClosed(this);
		if (this.openContainer != null) {
			this.openContainer.onContainerClosed(this);
		}
	}

	public boolean isEntityInsideOpaqueBlock()
	{
		return (!this.sleeping) && (super.isEntityInsideOpaqueBlock());
	}

	public GameProfile getGameProfile()
	{
		return this.field_146106_i;
	}

	public EnumStatus sleepInBedAt(int p_71018_1_, int p_71018_2_, int p_71018_3_)
	{
		PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(this, p_71018_1_, p_71018_2_, p_71018_3_);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.result != null) {
			return event.result;
		}
		if (!this.worldObj.isRemote)
		{
			if ((isPlayerSleeping()) || (!isEntityAlive())) {
				return EnumStatus.OTHER_PROBLEM;
			}
			if (!this.worldObj.provider.isSurfaceWorld()) {
				return EnumStatus.NOT_POSSIBLE_HERE;
			}
			if (this.worldObj.isDaytime()) {
				return EnumStatus.NOT_POSSIBLE_NOW;
			}
			if ((Math.abs(this.posX - p_71018_1_) > 3.0D) || (Math.abs(this.posY - p_71018_2_) > 2.0D) || (Math.abs(this.posZ - p_71018_3_) > 3.0D)) {
				return EnumStatus.TOO_FAR_AWAY;
			}
			double d0 = 8.0D;
			double d1 = 5.0D;
			List list = this.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(p_71018_1_ - d0, p_71018_2_ - d1, p_71018_3_ - d0, p_71018_1_ + d0, p_71018_2_ + d1, p_71018_3_ + d0));
			if (!list.isEmpty()) {
				return EnumStatus.NOT_SAFE;
			}
		}
		if (isRiding()) {
			mountEntity((Entity)null);
		}
		setSize(0.2F, 0.2F);
		this.yOffset = 0.2F;
		if (this.worldObj.blockExists(p_71018_1_, p_71018_2_, p_71018_3_))
		{
			int l = this.worldObj.getBlock(p_71018_1_, p_71018_2_, p_71018_3_).getBedDirection(this.worldObj, p_71018_1_, p_71018_2_, p_71018_3_);
			float f1 = 0.5F;
			float f = 0.5F;
			switch (l)
			{
			case 0: 
				f = 0.9F;
				break;
			case 1: 
				f1 = 0.1F;
				break;
			case 2: 
				f = 0.1F;
				break;
			case 3: 
				f1 = 0.9F;
			}
			func_71013_b(l);
			setPosition(p_71018_1_ + f1, p_71018_2_ + 0.9375F, p_71018_3_ + f);
		}
		else
		{
			setPosition(p_71018_1_ + 0.5F, p_71018_2_ + 0.9375F, p_71018_3_ + 0.5F);
		}
		this.sleeping = true;
		this.sleepTimer = 0;
		this.playerLocation = new ChunkCoordinates(p_71018_1_, p_71018_2_, p_71018_3_);
		this.motionX = (this.motionZ = this.motionY = 0.0D);
		if (!this.worldObj.isRemote) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}
		return EnumStatus.OK;
	}

	private void func_71013_b(int p_71013_1_)
	{
		this.field_71079_bU = 0.0F;
		this.field_71089_bV = 0.0F;
		switch (p_71013_1_)
		{
		case 0: 
			this.field_71089_bV = -1.8F;
			break;
		case 1: 
			this.field_71079_bU = 1.8F;
			break;
		case 2: 
			this.field_71089_bV = 1.8F;
			break;
		case 3: 
			this.field_71079_bU = -1.8F;
		}
	}

	public void wakeUpPlayer(boolean p_70999_1_, boolean p_70999_2_, boolean p_70999_3_)
	{
		setSize(0.6F, 1.8F);
		resetHeight();
		ChunkCoordinates chunkcoordinates = this.playerLocation;
		ChunkCoordinates chunkcoordinates1 = this.playerLocation;
		Block block = chunkcoordinates == null ? null : this.worldObj.getBlock(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ);
		if ((chunkcoordinates != null) && (block.isBed(this.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this)))
		{
			block.setBedOccupied(this.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this, false);
			chunkcoordinates1 = block.getBedSpawnPosition(this.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this);
			if (chunkcoordinates1 == null) {
				chunkcoordinates1 = new ChunkCoordinates(chunkcoordinates.posX, chunkcoordinates.posY + 1, chunkcoordinates.posZ);
			}
			setPosition(chunkcoordinates1.posX + 0.5F, chunkcoordinates1.posY + this.yOffset + 0.1F, chunkcoordinates1.posZ + 0.5F);
		}
		this.sleeping = false;
		if ((!this.worldObj.isRemote) && (p_70999_2_)) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}
		if (p_70999_1_) {
			this.sleepTimer = 0;
		} else {
			this.sleepTimer = 100;
		}
		if (p_70999_3_) {
			setSpawnChunk(this.playerLocation, false);
		}
	}

	private boolean isInBed()
	{
		return this.worldObj.getBlock(this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ).isBed(this.worldObj, this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ, this);
	}

	public static ChunkCoordinates verifyRespawnCoordinates(World p_71056_0_, ChunkCoordinates p_71056_1_, boolean p_71056_2_)
	{
		IChunkProvider ichunkprovider = p_71056_0_.getChunkProvider();
		ichunkprovider.loadChunk(p_71056_1_.posX - 3 >> 4, p_71056_1_.posZ - 3 >> 4);
		ichunkprovider.loadChunk(p_71056_1_.posX + 3 >> 4, p_71056_1_.posZ - 3 >> 4);
		ichunkprovider.loadChunk(p_71056_1_.posX - 3 >> 4, p_71056_1_.posZ + 3 >> 4);
		ichunkprovider.loadChunk(p_71056_1_.posX + 3 >> 4, p_71056_1_.posZ + 3 >> 4);
		if (p_71056_0_.getBlock(p_71056_1_.posX, p_71056_1_.posY, p_71056_1_.posZ).isBed(p_71056_0_, p_71056_1_.posX, p_71056_1_.posY, p_71056_1_.posZ, null))
		{
			ChunkCoordinates chunkcoordinates1 = p_71056_0_.getBlock(p_71056_1_.posX, p_71056_1_.posY, p_71056_1_.posZ).getBedSpawnPosition(p_71056_0_, p_71056_1_.posX, p_71056_1_.posY, p_71056_1_.posZ, null);
			return chunkcoordinates1;
		}
		Material material = p_71056_0_.getBlock(p_71056_1_.posX, p_71056_1_.posY, p_71056_1_.posZ).getMaterial();
		Material material1 = p_71056_0_.getBlock(p_71056_1_.posX, p_71056_1_.posY + 1, p_71056_1_.posZ).getMaterial();
		boolean flag1 = (!material.isSolid()) && (!material.isLiquid());
		boolean flag2 = (!material1.isSolid()) && (!material1.isLiquid());
		return (p_71056_2_) && (flag1) && (flag2) ? p_71056_1_ : null;
	}

	@SideOnly(Side.CLIENT)
	public float getBedOrientationInDegrees()
	{
		if (this.playerLocation != null)
		{
			int x = this.playerLocation.posX;
			int y = this.playerLocation.posY;
			int z = this.playerLocation.posZ;
			int j = this.worldObj.getBlock(x, y, z).getBedDirection(this.worldObj, x, y, z);
			switch (j)
			{
			case 0: 
				return 90.0F;
			case 1: 
				return 0.0F;
			case 2: 
				return 270.0F;
			case 3: 
				return 180.0F;
			}
		}
		return 0.0F;
	}

	public boolean isPlayerSleeping()
	{
		return this.sleeping;
	}

	public boolean isPlayerFullyAsleep()
	{
		return (this.sleeping) && (this.sleepTimer >= 100);
	}

	@SideOnly(Side.CLIENT)
	public int getSleepTimer()
	{
		return this.sleepTimer;
	}

	@SideOnly(Side.CLIENT)
	protected boolean getHideCape(int p_82241_1_)
	{
		return (this.dataWatcher.getWatchableObjectByte(16) & 1 << p_82241_1_) != 0;
	}

	protected void setHideCape(int p_82239_1_, boolean p_82239_2_)
	{
		byte b0 = this.dataWatcher.getWatchableObjectByte(16);
		if (p_82239_2_) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 1 << p_82239_1_)));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & (1 << p_82239_1_ ^ 0xFFFFFFFF))));
		}
	}

	public void addChatComponentMessage(IChatComponent p_146105_1_) {}

	@Deprecated
	public ChunkCoordinates getBedLocation()
	{
		return getBedLocation(this.dimension);
	}

	@Deprecated
	public boolean isSpawnForced()
	{
		return isSpawnForced(this.dimension);
	}

	public void setSpawnChunk(ChunkCoordinates p_71063_1_, boolean p_71063_2_)
	{
		if (this.dimension != 0)
		{
			setSpawnChunk(p_71063_1_, p_71063_2_, this.dimension);
			return;
		}
		if (p_71063_1_ != null)
		{
			this.spawnChunk = new ChunkCoordinates(p_71063_1_);
			this.spawnForced = p_71063_2_;
		}
		else
		{
			this.spawnChunk = null;
			this.spawnForced = false;
		}
	}

	public void triggerAchievement(StatBase p_71029_1_)
	{
		addStat(p_71029_1_, 1);
	}

	public void addStat(StatBase p_71064_1_, int p_71064_2_) {}

	public void jump()
	{
		super.jump();
		addStat(StatList.jumpStat, 1);
		if (isSprinting()) {
			addExhaustion(0.8F);
		} else {
			addExhaustion(0.2F);
		}
	}

	public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_)
	{
		double d0 = this.posX;
		double d1 = this.posY;
		double d2 = this.posZ;
		if ((this.capabilities.isFlying) && (this.ridingEntity == null))
		{
			double d3 = this.motionY;
			float f2 = this.jumpMovementFactor;
			this.jumpMovementFactor = this.capabilities.getFlySpeed();
			super.moveEntityWithHeading(p_70612_1_, p_70612_2_);
			this.motionY = (d3 * 0.6D);
			this.jumpMovementFactor = f2;
		}
		else
		{
			super.moveEntityWithHeading(p_70612_1_, p_70612_2_);
		}
		addMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);
	}

	public float getAIMoveSpeed()
	{
		return (float)getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
	}

	public void addMovementStat(double p_71000_1_, double p_71000_3_, double p_71000_5_)
	{
		if (this.ridingEntity == null) {
			if (isInsideOfMaterial(Material.water))
			{
				int i = Math.round(MathHelper.sqrt_double(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);
				if (i > 0)
				{
					addStat(StatList.distanceDoveStat, i);
					addExhaustion(0.015F * i * 0.01F);
				}
			}
			else if (isInWater())
			{
				int i = Math.round(MathHelper.sqrt_double(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
				if (i > 0)
				{
					addStat(StatList.distanceSwumStat, i);
					addExhaustion(0.015F * i * 0.01F);
				}
			}
			else if (isOnLadder())
			{
				if (p_71000_3_ > 0.0D) {
					addStat(StatList.distanceClimbedStat, (int)Math.round(p_71000_3_ * 100.0D));
				}
			}
			else if (this.onGround)
			{
				int i = Math.round(MathHelper.sqrt_double(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
				if (i > 0)
				{
					addStat(StatList.distanceWalkedStat, i);
					if (isSprinting()) {
						addExhaustion(0.09999999F * i * 0.01F);
					} else {
						addExhaustion(0.01F * i * 0.01F);
					}
				}
			}
			else
			{
				int i = Math.round(MathHelper.sqrt_double(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
				if (i > 25) {
					addStat(StatList.distanceFlownStat, i);
				}
			}
		}
	}

	private void addMountedMovementStat(double p_71015_1_, double p_71015_3_, double p_71015_5_)
	{
		if (this.ridingEntity != null)
		{
			int i = Math.round(MathHelper.sqrt_double(p_71015_1_ * p_71015_1_ + p_71015_3_ * p_71015_3_ + p_71015_5_ * p_71015_5_) * 100.0F);
			if (i > 0) {
				if ((this.ridingEntity instanceof EntityMinecart))
				{
					addStat(StatList.distanceByMinecartStat, i);
					if (this.startMinecartRidingCoordinate == null) {
						this.startMinecartRidingCoordinate = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
					} else if (this.startMinecartRidingCoordinate.getDistanceSquared(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) >= 1000000.0D) {
						addStat(AchievementList.onARail, 1);
					}
				}
				else if ((this.ridingEntity instanceof EntityBoat))
				{
					addStat(StatList.distanceByBoatStat, i);
				}
				else if ((this.ridingEntity instanceof EntityPig))
				{
					addStat(StatList.distanceByPigStat, i);
				}
				else if ((this.ridingEntity instanceof EntityHorse))
				{
					addStat(StatList.field_151185_q, i);
				}
			}
		}
	}

	protected void fall(float p_70069_1_)
	{
		if (!this.capabilities.allowFlying)
		{
			if (p_70069_1_ >= 2.0F) {
				addStat(StatList.distanceFallenStat, (int)Math.round(p_70069_1_ * 100.0D));
			}
			super.fall(p_70069_1_);
		}
		else
		{
			MinecraftForge.EVENT_BUS.post(new PlayerFlyableFallEvent(this, p_70069_1_));
		}
	}

	protected String func_146067_o(int p_146067_1_)
	{
		return p_146067_1_ > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
	}

	public void onKillEntity(EntityLivingBase p_70074_1_)
	{
		if ((p_70074_1_ instanceof IMob)) {
			triggerAchievement(AchievementList.killEnemy);
		}
		int i = EntityList.getEntityID(p_70074_1_);
		EntityList.EntityEggInfo entityegginfo = (EntityList.EntityEggInfo)EntityList.entityEggs.get(Integer.valueOf(i));
		if (entityegginfo != null) {
			addStat(entityegginfo.field_151512_d, 1);
		}
	}

	public void setInWeb()
	{
		if (!this.capabilities.isFlying) {
			super.setInWeb();
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon getItemIcon(ItemStack p_70620_1_, int p_70620_2_)
	{
		IIcon iicon = super.getItemIcon(p_70620_1_, p_70620_2_);
		if ((p_70620_1_.getItem() == Items.fishing_rod) && (this.fishEntity != null))
		{
			iicon = Items.fishing_rod.func_94597_g();
		}
		else
		{
			if ((this.itemInUse != null) && (p_70620_1_.getItem() == Items.bow))
			{
				int j = p_70620_1_.getMaxItemUseDuration() - this.itemInUseCount;
				if (j >= 18) {
					return Items.bow.getItemIconForUseDuration(2);
				}
				if (j > 13) {
					return Items.bow.getItemIconForUseDuration(1);
				}
				if (j > 0) {
					return Items.bow.getItemIconForUseDuration(0);
				}
			}
			iicon = p_70620_1_.getItem().getIcon(p_70620_1_, p_70620_2_, this, this.itemInUse, this.itemInUseCount);
		}
		return iicon;
	}

	public ItemStack getCurrentArmor(int p_82169_1_)
	{
		return this.inventory.armorItemInSlot(p_82169_1_);
	}

	public void addExperience(int p_71023_1_)
	{
		addScore(p_71023_1_);
		int j = 2147483647 - this.experienceTotal;
		if (p_71023_1_ > j) {
			p_71023_1_ = j;
		}
		this.experience += p_71023_1_ / xpBarCap();
		for (this.experienceTotal += p_71023_1_; this.experience >= 1.0F; this.experience /= xpBarCap())
		{
			this.experience = ((this.experience - 1.0F) * xpBarCap());
			addExperienceLevel(1);
		}
	}

	public void addExperienceLevel(int p_82242_1_)
	{
		this.experienceLevel += p_82242_1_;
		if (this.experienceLevel < 0)
		{
			this.experienceLevel = 0;
			this.experience = 0.0F;
			this.experienceTotal = 0;
		}
		if ((p_82242_1_ > 0) && (this.experienceLevel % 5 == 0) && (this.field_82249_h < this.ticksExisted - 100.0F))
		{
			float f = this.experienceLevel > 30 ? 1.0F : this.experienceLevel / 30.0F;
			this.worldObj.playSoundAtEntity(this, "random.levelup", f * 0.75F, 1.0F);
			this.field_82249_h = this.ticksExisted;
		}
	}

	public int xpBarCap()
	{
		return this.experienceLevel >= 15 ? 17 + (this.experienceLevel - 15) * 3 : this.experienceLevel >= 30 ? 62 + (this.experienceLevel - 30) * 7 : 17;
	}

	public void addExhaustion(float p_71020_1_)
	{
		if (!this.capabilities.disableDamage) {
			if (!this.worldObj.isRemote) {
				this.foodStats.addExhaustion(p_71020_1_);
			}
		}
	}

	public FoodStats getFoodStats()
	{
		return this.foodStats;
	}

	public boolean canEat(boolean p_71043_1_)
	{
		return ((p_71043_1_) || (this.foodStats.needFood())) && (!this.capabilities.disableDamage);
	}

	public boolean shouldHeal()
	{
		return (getHealth() > 0.0F) && (getHealth() < getMaxHealth());
	}

	public void setItemInUse(ItemStack p_71008_1_, int p_71008_2_)
	{
		if (p_71008_1_ != this.itemInUse)
		{
			p_71008_2_ = ForgeEventFactory.onItemUseStart(this, p_71008_1_, p_71008_2_);
			if (p_71008_2_ <= 0) {
				return;
			}
			this.itemInUse = p_71008_1_;
			this.itemInUseCount = p_71008_2_;
			if (!this.worldObj.isRemote) {
				setEating(true);
			}
		}
	}

	public boolean isCurrentToolAdventureModeExempt(int p_82246_1_, int p_82246_2_, int p_82246_3_)
	{
		if (this.capabilities.allowEdit) {
			return true;
		}
		Block block = this.worldObj.getBlock(p_82246_1_, p_82246_2_, p_82246_3_);
		if (block.getMaterial() != Material.air)
		{
			if (block.getMaterial().isAdventureModeExempt()) {
				return true;
			}
			if (getCurrentEquippedItem() != null)
			{
				ItemStack itemstack = getCurrentEquippedItem();
				if ((itemstack.func_150998_b(block)) || (itemstack.func_150997_a(block) > 1.0F)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean canPlayerEdit(int p_82247_1_, int p_82247_2_, int p_82247_3_, int p_82247_4_, ItemStack p_82247_5_)
	{
		return this.capabilities.allowEdit;
	}

	protected int getExperiencePoints(EntityPlayer p_70693_1_)
	{
		if (this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
			return 0;
		}
		int i = this.experienceLevel * 7;
		return i > 100 ? 100 : i;
	}

	protected boolean isPlayer()
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	public boolean getAlwaysRenderNameTagForRender()
	{
		return true;
	}

	public void clonePlayer(EntityPlayer p_71049_1_, boolean p_71049_2_)
	{
		if (p_71049_2_)
		{
			this.inventory.copyInventory(p_71049_1_.inventory);
			setHealth(p_71049_1_.getHealth());
			this.foodStats = p_71049_1_.foodStats;
			this.experienceLevel = p_71049_1_.experienceLevel;
			this.experienceTotal = p_71049_1_.experienceTotal;
			this.experience = p_71049_1_.experience;
			setScore(p_71049_1_.getScore());
			this.teleportDirection = p_71049_1_.teleportDirection;

			this.extendedProperties = p_71049_1_.extendedProperties;
			for (IExtendedEntityProperties p : this.extendedProperties.values()) {
				p.init(this, this.worldObj);
			}
		}
		else if (this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
		{
			this.inventory.copyInventory(p_71049_1_.inventory);
			this.experienceLevel = p_71049_1_.experienceLevel;
			this.experienceTotal = p_71049_1_.experienceTotal;
			this.experience = p_71049_1_.experience;
			setScore(p_71049_1_.getScore());
		}
		this.theInventoryEnderChest = p_71049_1_.theInventoryEnderChest;

		this.spawnChunkMap = p_71049_1_.spawnChunkMap;
		this.spawnForcedMap = p_71049_1_.spawnForcedMap;



		NBTTagCompound old = p_71049_1_.getEntityData();
		if (old.hasKey("PlayerPersisted")) {
			getEntityData().setTag("PlayerPersisted", old.getCompoundTag("PlayerPersisted"));
		}
		MinecraftForge.EVENT_BUS.post(new PlayerEvent.Clone(this, p_71049_1_, !p_71049_2_));
	}

	protected boolean canTriggerWalking()
	{
		return !this.capabilities.isFlying;
	}

	public void sendPlayerAbilities() {}

	public void setGameType(WorldSettings.GameType p_71033_1_) {}

	public String getCommandSenderName()
	{
		return this.field_146106_i.getName();
	}

	public World getEntityWorld()
	{
		return this.worldObj;
	}

	public InventoryEnderChest getInventoryEnderChest()
	{
		return this.theInventoryEnderChest;
	}

	public ItemStack getEquipmentInSlot(int p_71124_1_)
	{
		return p_71124_1_ == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[(p_71124_1_ - 1)];
	}

	public ItemStack getHeldItem()
	{
		return this.inventory.getCurrentItem();
	}

	public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_)
	{
		if (p_70062_1_ == 0) {
			this.inventory.mainInventory[this.inventory.currentItem] = p_70062_2_;
		} else {
			this.inventory.armorInventory[(p_70062_1_ - 1)] = p_70062_2_;
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean isInvisibleToPlayer(EntityPlayer p_98034_1_)
	{
		if (!isInvisible()) {
			return false;
		}
		Team team = getTeam();
		return (team == null) || (p_98034_1_ == null) || (p_98034_1_.getTeam() != team) || (!team.func_98297_h());
	}

	public ItemStack[] getLastActiveItems()
	{
		return this.inventory.armorInventory;
	}

	@SideOnly(Side.CLIENT)
	public boolean getHideCape()
	{
		return getHideCape(1);
	}

	public boolean isPushedByWater()
	{
		return !this.capabilities.isFlying;
	}

	public Scoreboard getWorldScoreboard()
	{
		return this.worldObj.getScoreboard();
	}

	public Team getTeam()
	{
		return getWorldScoreboard().getPlayersTeam(getCommandSenderName());
	}

	public IChatComponent func_145748_c_()
	{
		ChatComponentText chatcomponenttext = new ChatComponentText(ScorePlayerTeam.formatPlayerName(getTeam(), getDisplayName()));
		chatcomponenttext.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + getCommandSenderName() + " "));
		return chatcomponenttext;
	}

	public void setAbsorptionAmount(float p_110149_1_)
	{
		if (p_110149_1_ < 0.0F) {
			p_110149_1_ = 0.0F;
		}
		getDataWatcher().updateObject(17, Float.valueOf(p_110149_1_));
	}

	public float getAbsorptionAmount()
	{
		return getDataWatcher().getWatchableObjectFloat(17);
	}

	public static UUID func_146094_a(GameProfile p_146094_0_)
	{
		UUID uuid = p_146094_0_.getId();
		if (uuid == null) {
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + p_146094_0_.getName()).getBytes(Charsets.UTF_8));
		}
		return uuid;
	}

	public static enum EnumChatVisibility
	{
		FULL(0, "options.chat.visibility.full"),  SYSTEM(1, "options.chat.visibility.system"),  HIDDEN(2, "options.chat.visibility.hidden");

		private static final EnumChatVisibility[] field_151432_d;
		private final int chatVisibility;
		private final String resourceKey;
		private static final String __OBFID = "CL_00001714";

		private EnumChatVisibility(int p_i45323_3_, String p_i45323_4_)
		{
			this.chatVisibility = p_i45323_3_;
			this.resourceKey = p_i45323_4_;
		}

		public int getChatVisibility()
		{
			return this.chatVisibility;
		}

		public static EnumChatVisibility getEnumChatVisibility(int p_151426_0_)
		{
			return field_151432_d[(p_151426_0_ % field_151432_d.length)];
		}

		@SideOnly(Side.CLIENT)
		public String getResourceKey()
		{
			return this.resourceKey;
		}

		static
		{
			field_151432_d = new EnumChatVisibility[values().length];





























			EnumChatVisibility[] var0 = values();
			int var1 = var0.length;
			for (int var2 = 0; var2 < var1; var2++)
			{
				EnumChatVisibility var3 = var0[var2];
				field_151432_d[var3.chatVisibility] = var3;
			}
		}
	}

	public void openGui(Object mod, int modGuiId, World world, int x, int y, int z)
	{
		FMLNetworkHandler.openGui(this, mod, modGuiId, world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public Vec3 getPosition(float par1)
	{
		if (par1 == 1.0F) {
			return Vec3.createVectorHelper(this.posX, this.posY + (getEyeHeight() - getDefaultEyeHeight()), this.posZ);
		}
		double d0 = this.prevPosX + (this.posX - this.prevPosX) * par1;
		double d1 = this.prevPosY + (this.posY - this.prevPosY) * par1 + (getEyeHeight() - getDefaultEyeHeight());
		double d2 = this.prevPosZ + (this.posZ - this.prevPosZ) * par1;
		return Vec3.createVectorHelper(d0, d1, d2);
	}

	public ChunkCoordinates getBedLocation(int dimension)
	{
		return dimension == 0 ? this.spawnChunk : (ChunkCoordinates)this.spawnChunkMap.get(Integer.valueOf(dimension));
	}

	public boolean isSpawnForced(int dimension)
	{
		if (dimension == 0) {
			return this.spawnForced;
		}
		Boolean forced = (Boolean)this.spawnForcedMap.get(Integer.valueOf(dimension));
		return forced == null ? false : forced.booleanValue();
	}

	public void setSpawnChunk(ChunkCoordinates chunkCoordinates, boolean forced, int dimension)
	{
		if (dimension == 0)
		{
			if (chunkCoordinates != null)
			{
				this.spawnChunk = new ChunkCoordinates(chunkCoordinates);
				this.spawnForced = forced;
			}
			else
			{
				this.spawnChunk = null;
				this.spawnForced = false;
			}
			return;
		}
		if (chunkCoordinates != null)
		{
			this.spawnChunkMap.put(Integer.valueOf(dimension), new ChunkCoordinates(chunkCoordinates));
			this.spawnForcedMap.put(Integer.valueOf(dimension), Boolean.valueOf(forced));
		}
		else
		{
			this.spawnChunkMap.remove(Integer.valueOf(dimension));
			this.spawnForcedMap.remove(Integer.valueOf(dimension));
		}
	}

	public float getDefaultEyeHeight()
	{
		return 0.12F;
	}

	public String getDisplayName()
	{
		if (this.displayname == null) {
			this.displayname = ForgeEventFactory.getPlayerDisplayName(this, getCommandSenderName());
		}
		return this.displayname;
	}

	public void refreshDisplayName()
	{
		this.displayname = ForgeEventFactory.getPlayerDisplayName(this, getCommandSenderName());
	}

	public static enum EnumStatus
	{
		OK,  NOT_POSSIBLE_HERE,  NOT_POSSIBLE_NOW,  TOO_FAR_AWAY,  OTHER_PROBLEM,  NOT_SAFE;

		private static final String __OBFID = "CL_00001712";

		private EnumStatus() {}
	}
}
