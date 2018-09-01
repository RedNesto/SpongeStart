package com.qixalite.spongestart.tasks;

import java.io.File;

public class GenerateForgeRunTask extends GenerateSpongeRunTask {

    @Override
    public void refresh() {
        super.refresh();
        setName("StartForgeServer");
        setDescription("Generate Forge run configuration to start a SpongeForge server");
        setDir(new File(getServerPath()).getAbsolutePath());
        setMain("net.minecraftforge.fml.relauncher.ServerLaunchWrapper");
        setPargs("--scan-classpath nogui");
    }

    @Override
    public String getServerPath() {
        return getExtension().getForgeServerFolder();
    }
}
