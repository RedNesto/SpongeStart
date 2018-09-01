package com.qixalite.spongestart.tasks;

import java.io.File;

public class GenerateVanillaRunTask extends GenerateSpongeRunTask {

    @Override
    public void refresh() {
        super.refresh();
        setName("StartVanillaServer");
        setDescription("Generate Vanilla run configuration to start a SpongeVanilla server");
        setDir(new File(getServerPath()).getAbsolutePath());
        setMain("org.spongepowered.server.launch.VersionCheckingMain");
        setPargs("--scan-classpath");
    }

    @Override
    public String getServerPath() {
        return getExtension().getVanillaServerFolder();
    }
}
