package com.qixalite.spongestart;

import java.io.File;

public class SpongeStartExtension {

    public String minecraft = "";
    public String type = "";
    public String forgeServerFolder = "run" + File.separator + "forge";
    public String vanillaServerFolder = "run" + File.separator + "vanilla";
    public String spongeForgeVersion = "";
    public String spongeVanillaVersion = "";
    public String forge = "";
    public String online = "true";


    public String getSpongeVanillaVersion() {
        return spongeVanillaVersion;
    }

    public void setSpongeVanillaVersion(String spongeVanillaVersion) {
        this.spongeVanillaVersion = spongeVanillaVersion;
    }

    public String getSpongeForgeVersion() {
        return spongeForgeVersion;
    }

    public void setSpongeForgeVersion(String spongeForgeVersion) {
        this.spongeForgeVersion = spongeForgeVersion;
    }

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

    public String getForgeServerFolder() {
        return this.forgeServerFolder;
    }

    public void setForgeServerFolder(String forgeServerFolder) {
        this.forgeServerFolder = forgeServerFolder;
    }

    public String getVanillaServerFolder() {
        return this.vanillaServerFolder;
    }

    public void setVanillaServerFolder(String vanillaServerFolder) {
        this.vanillaServerFolder = vanillaServerFolder;
    }

    public String getForge() {
        return forge;
    }

    public void setForge(String forge) {
        this.forge = forge;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

}
