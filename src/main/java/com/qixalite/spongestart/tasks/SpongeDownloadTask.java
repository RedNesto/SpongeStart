package com.qixalite.spongestart.tasks;

public abstract class SpongeDownloadTask extends DownloadTask {

    protected static final String DOWNLOAD_API = "https://dl-api.spongepowered.org/v1/org.spongepowered/";
    protected static final String REPO = "https://repo.spongepowered.org/maven/org/spongepowered/";


    protected abstract String getArtifactVersion();


}
