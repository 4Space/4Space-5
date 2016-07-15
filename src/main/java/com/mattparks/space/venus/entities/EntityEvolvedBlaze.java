package com.mattparks.space.venus.entities;

import com.mattparks.space.venus.items.VenusItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityEvolvedBlaze extends EntityMob implements IEntityBreathable {
	private float heightOffset = 0.5F;
	private int heightOffsetUpdateTime;
	private int field_70846_g;

	public EntityEvolvedBlaze(World par1World) {
		super(par1World);
		this.isImmuneToFire = true;
		this.experienceValue = 10;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, new Byte((byte) 0));
	}

	@Override
	protected String getLivingSound() {
		return "mob.blaze.breathe";
	}

	@Override
	protected String getHurtSound() {
		return "mob.blaze.hit";
	}

	@Override
	protected String getDeathSound() {
		return "mob.blaze.death";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1) {
		return 15728880;
	}

	@Override
	public float getBrightness(float par1) {
		return 1.0F;
	}

	@Override
	public void onLivingUpdate() {
		if (!this.worldObj.isRemote) {
			if (this.isWet()) {
				this.attackEntityFrom(DamageSource.drown, 1.0F);
			}

			--this.heightOffsetUpdateTime;

			if (this.heightOffsetUpdateTime <= 0) {
				this.heightOffsetUpdateTime = 100;
				this.heightOffset = 0.5F + (float) this.rand.nextGaussian() * 3.0F;
			}

			if (this.getEntityToAttack() != null && this.getEntityToAttack().posY + this.getEntityToAttack().getEyeHeight() > this.posY + this.getEyeHeight() + this.heightOffset) {
				this.motionY += (0.30000001192092896D - this.motionY) * 0.30000001192092896D;
			}
		}

		if (this.rand.nextInt(24) == 0) {
			this.worldObj.playSoundEffect(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, "fire.fire", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F);
		}

		if (!this.onGround && this.motionY < 0.0D) {
			this.motionY *= 0.6D;
		}

		for (int i = 0; i < 2; ++i) {
			this.worldObj.spawnParticle("largesmoke", this.posX + (this.rand.nextDouble() - 0.5D) * this.width, this.posY + this.rand.nextDouble() * this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * this.width, 0.0D, 0.0D, 0.0D);
		}
		super.onLivingUpdate();
	}

	@Override
	protected void attackEntity(Entity par1Entity, float par2) {
		if (this.attackTime <= 0 && par2 < 2.0F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			this.attackEntityAsMob(par1Entity);
		} else if (par2 < 30.0F) {
			final double d0 = par1Entity.posX - this.posX;
			final double d1 = par1Entity.boundingBox.minY + par1Entity.height / 2.0F - (this.posY + this.height / 2.0F);
			final double d2 = par1Entity.posZ - this.posZ;

			if (this.attackTime == 0) {
				++this.field_70846_g;

				if (this.field_70846_g == 1) {
					this.attackTime = 60;
					this.func_70844_e(true);
				} else if (this.field_70846_g <= 4) {
					this.attackTime = 6;
				} else {
					this.attackTime = 100;
					this.field_70846_g = 0;
					this.func_70844_e(false);
				}

				if (this.field_70846_g > 1) {
					final float f1 = MathHelper.sqrt_float(par2) * 0.5F;
					this.worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1009, (int) this.posX, (int) this.posY, (int) this.posZ, 0);

					for (int i = 0; i < 1; ++i) {
						final EntitySmallFireball entitysmallfireball = new EntitySmallFireball(this.worldObj, this, d0 + this.rand.nextGaussian() * f1, d1, d2 + this.rand.nextGaussian() * f1);
						entitysmallfireball.posY = this.posY + this.height / 2.0F + 0.5D;
						this.worldObj.spawnEntityInWorld(entitysmallfireball);
					}
				}
			}

			this.rotationYaw = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
			this.hasAttacked = true;
		}
	}

	@Override
	protected void fall(float par1) {
	}

	@Override
	public boolean isBurning() {
		return this.func_70845_n();
	}

	@Override
	protected void dropFewItems(boolean par1, int par2) {
		if (par1) {
			final int j = this.rand.nextInt(2 + par2);

			for (int k = 0; k < j; ++k) {
				this.entityDropItem(new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("venusRod")), 1.0f);
			}
		}
	}

	public boolean func_70845_n() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	public void func_70844_e(boolean par1) {
		byte b0 = this.dataWatcher.getWatchableObjectByte(16);

		if (par1) {
			b0 = (byte) (b0 | 1);
		} else {
			b0 &= -2;
		}
		this.dataWatcher.updateObject(16, Byte.valueOf(b0));
	}

	@Override
	protected boolean isValidLightLevel() {
		return true;
	}

	@Override
	public boolean canBreath() {
		return true;
	}
}
