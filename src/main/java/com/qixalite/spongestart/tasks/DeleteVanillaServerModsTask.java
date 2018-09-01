package com.qixalite.spongestart.tasks;

public class DeleteVanillaServerModsTask extends BaseDeleteTask {

    public DeleteVanillaServerModsTask() {
        super();
        doFirst(task -> delete(getProject().fileTree(getExtension().getVanillaServerFolder() + "/mods")));
    }
}
