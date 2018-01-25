package com.qixalite.spongestart;

import java.io.File;

public class SpongeStartExtension {

    private String minecraft;
    private String type = "";
    private String spongeForge;
    private String spongeVanilla;
    private String online = "true";
    private String forgeServerFolder = "run" + File.separatorChar + "forge";
    private String vanillaServerFolder = "run" + File.separatorChar + "vanilla";
    private String buildClassesFolder;
    private String resourcesFolder;
    private String cacheFolder;
    private String startFolder;
    private String forge;

    public String getMinecraft() {
        return minecraft;
    }

    public void setMinecraft(String minecraft) {
        this.minecraft = minecraft;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpongeForge() {
        return spongeForge;
    }

    public void setSpongeForge(String spongeForge) {
        this.spongeForge = spongeForge;
    }

    public String getSpongeVanilla() {
        return spongeVanilla;
    }

    public void setSpongeVanilla(String spongeVanilla) {
        this.spongeVanilla = spongeVanilla;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getForgeServerFolder() {
        return forgeServerFolder;
    }

    public void setForgeServerFolder(String forgeServerFolder) {
        this.forgeServerFolder = forgeServerFolder;
    }

    public String getVanillaServerFolder() {
        return vanillaServerFolder;
    }

    public void setVanillaServerFolder(String vanillaServerFolder) {
        this.vanillaServerFolder = vanillaServerFolder;
    }

    public String getBuildClassesFolder() {
        return buildClassesFolder;
    }

    public void setBuildClassesFolder(String buildClassesFolder) {
        this.buildClassesFolder = buildClassesFolder;
    }

    public String getResourcesFolder() {
        return resourcesFolder;
    }

    public void setResourcesFolder(String resourcesFolder) {
        this.resourcesFolder = resourcesFolder;
    }

    public String getCacheFolder() {
        return cacheFolder;
    }

    public void setCacheFolder(String cacheFolder) {
        this.cacheFolder = cacheFolder;
    }

    public String getStartFolder() {
        return startFolder;
    }

    public void setStartFolder(String startFolder) {
        this.startFolder = startFolder;
    }

    public String getForge() {
        return forge;
    }

    public void setForge(String forge) {
        this.forge = forge;
    }

}
