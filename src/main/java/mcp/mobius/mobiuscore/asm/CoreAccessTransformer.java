package mcp.mobius.mobiuscore.asm;

import java.io.IOException;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

public class CoreAccessTransformer extends AccessTransformer {

    public CoreAccessTransformer() throws IOException {
        super("mobius_at.cfg");
    }
}
