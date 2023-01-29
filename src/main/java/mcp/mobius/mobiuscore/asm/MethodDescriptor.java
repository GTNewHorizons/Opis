package mcp.mobius.mobiuscore.asm;

public final class MethodDescriptor {

    private final String methodName;
    private final String descriptor;
    // public String vanillaDesc;
    // public String coremodDesc;

    public MethodDescriptor(String name, String desc) {
        this.methodName = name;
        this.descriptor = desc;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String getDescriptor() {
        return this.descriptor;
    }

    /*
     * public MethodDescriptor(String name, String vdesc, String cdesc){ this.methodName = name; this.vanillaDesc =
     * vdesc; this.coremodDesc = cdesc; }
     */

}
