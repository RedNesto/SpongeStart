package com.qixalite.spongestart.tasks;

import org.gradle.api.tasks.Input;

public class ForgeDownloadTaskV2 extends DownloadTaskV2 {

    private static final String FORGE_REPO = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/";

    @Input
    @Override
    public String getDownloadUrl() {
        String key = getExtension().getMinecraft() + "-" + getExtension().getForge();
        return FORGE_REPO + key + "/forge-" + key + "-installer.jar";
    }

}
