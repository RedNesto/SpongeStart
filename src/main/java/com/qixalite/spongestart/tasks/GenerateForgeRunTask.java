package com.qixalite.spongestart.tasks;

public class GenerateForgeRunTask extends GenerateSpongeRunTask {

    @Override
    public void refresh() {
        super.refresh();
        setName("StartForgeServer");
        setDir(getExtension().getForgeServerFolder());
        setMain("net.minecraftforge.fml.relauncher.ServerLaunchWrapper");
        setPargs("--scan-classpath nogui");
        setDescription("Generate Forge run configuration to start a SpongeForge server");
    }

    @Override
    public String getServerPath() {
        return getExtension().getForgeServerFolder();
    }
}
