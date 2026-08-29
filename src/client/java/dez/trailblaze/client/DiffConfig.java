package dez.trailblaze.client;

public class DiffConfig {
    public static final DiffConfig INSTANCE = new DiffConfig();


    // NOISE and SURFACE are always enabled
    /*
    Carvers are expensive, needing the 17x17 chunk area to generate. Needs a cache of nearby chunks to determine
    how to place things. Additionally, Features requires either a custom WorldGenRegion, or to pass the actual
    serverLevel, which would then cause it to make real changes to the client.
     */
    //public boolean includeCarvers = false;
    //public boolean includeFeatures = false;
}
