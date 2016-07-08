package micdoodle8.mods.miccore;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

public class MicdoodleAccessTransformer extends AccessTransformer
{
	public MicdoodleAccessTransformer() throws IOException
	{
		super("micdoodlecore_at.cfg");
	}
}