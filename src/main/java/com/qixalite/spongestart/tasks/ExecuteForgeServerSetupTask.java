package com.qixalite.spongestart.tasks;

import org.gradle.api.tasks.AbstractExecTask;

public class ExecuteForgeServerSetupTask extends AbstractExecTask<ExecuteForgeServerSetupTask> {

    public ExecuteForgeServerSetupTask() {
        super(ExecuteForgeServerSetupTask.class);
        setCommandLine((Object[]) "java -jar setup.jar --installServer".split(" "));
        //setStandardOutput(System.out);
        //setErrorOutput(System.err);
    }

    public void init(SetupForgeServerTask setupForgeServerTask) {
        setupForgeServerTask.getLocation().mkdirs();
        setWorkingDir(setupForgeServerTask.getLocation());
        setDescription("Executes the Forge server installer");
    }
}
