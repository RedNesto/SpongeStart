package com.qixalite.spongestart.tasks;

public class GenerateVanillaRunTask extends GenerateSpongeRunTask {

    @Override
    public void refresh() {
        super.refresh();
        setDescription("Generate Vanilla run configuration to start a SpongeVanilla server");
        setName("StartVanillaServer");
        setDir("file://$PROJECT_DIR$/"+getExtension().getVanillaServerFolder());
        setMain("org.spongepowered.server.launch.VersionCheckingMain");
        setPargs("--scan-classpath");
    }

    @Override
    public String getServerPath() {
        return getExtension().getVanillaServerFolder();
    }
}
