package com.mattparks.space.core.music;

import java.util.ArrayList;
import java.util.List;

import com.mattparks.space.core.builder.celestials.ICoreCelestial;
import com.mattparks.space.core.utils.SpacePair;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

/**
 * A client music player.
 */
public class MusicHandlerClient extends MusicTicker {
	public static final Class[][] COMMON_TYPES = { { MusicTicker.MusicType.class, ResourceLocation.class, int.class, int.class }, };
	public List<SpacePair<ICoreCelestial, MusicTicker.MusicType>> musicTypes;
	
	public MusicHandlerClient(Minecraft mc) {
		super(mc);
		musicTypes = new ArrayList<SpacePair<ICoreCelestial, MusicType>>();
	}
	
	public void loadMusicType(ICoreCelestial celestialBody, String musicJson) {
		musicTypes.add(new SpacePair<ICoreCelestial, MusicTicker.MusicType>(celestialBody, EnumHelper.addEnum(COMMON_TYPES, MusicTicker.MusicType.class, celestialBody.prefixAsset.toUpperCase() + "MUSIC", new ResourceLocation(celestialBody.prefixAsset, musicJson), 12000, 24000)));
	}

	@Override
	public void update() {
		MusicTicker.MusicType musictype = this.field_147677_b.func_147109_W();
		WorldClient world = FMLClientHandler.instance().getWorldClient();
		
		for (SpacePair<ICoreCelestial, MusicTicker.MusicType> pair : musicTypes) {
			if (world != null && pair.getFirst().instanceOfProvider(world.provider)) {
				musictype = pair.getSecond();
				break;
			}
		}

		if (this.field_147678_c != null) {
			if (!musictype.getMusicTickerLocation().equals(this.field_147678_c.getPositionedSoundLocation())) {
				this.field_147677_b.getSoundHandler().stopSound(this.field_147678_c);
				this.field_147676_d = MathHelper.getRandomIntegerInRange(this.field_147679_a, 0, musictype.func_148634_b() / 2);
			}

			if (!this.field_147677_b.getSoundHandler().isSoundPlaying(this.field_147678_c)) {
				this.field_147678_c = null;
				this.field_147676_d = Math.min(MathHelper.getRandomIntegerInRange(this.field_147679_a, musictype.func_148634_b(), musictype.func_148633_c()), this.field_147676_d);
			}
		}

		if (this.field_147678_c == null && this.field_147676_d-- <= 0) {
			this.field_147678_c = PositionedSoundRecord.func_147673_a(musictype.getMusicTickerLocation());
			this.field_147677_b.getSoundHandler().playSound(this.field_147678_c);
			this.field_147676_d = Integer.MAX_VALUE;
		}
	}
}
