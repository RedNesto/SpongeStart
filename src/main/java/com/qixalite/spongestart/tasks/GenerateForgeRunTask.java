package com.qixalite.spongestart.tasks;

public class GenerateForgeRunTask extends GenerateSpongeRunTask {

    @Override
    public void refresh() {
        super.refresh();
        setName("StartForgeServer");
        setDescription("Generate Forge run configuration to start a SpongeForge server");
        setDir(getExtension().getForgeServerFolder());
        setMain("net.minecraftforge.fml.relauncher.ServerLaunchWrapper");
        setPargs("--scan-classpath nogui");
    }

    @Override
    public String getServerPath() {
        return getExtension().getForgeServerFolder();
    }
}
