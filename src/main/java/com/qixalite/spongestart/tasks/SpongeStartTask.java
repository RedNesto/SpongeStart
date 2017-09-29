package com.qixalite.spongestart.tasks;

import org.gradle.api.DefaultTask;

public class SpongeStartTask extends DefaultTask {

    private static final String TASK_GROUP = "SpongeStart";

    public SpongeStartTask(){
        setGroup(TASK_GROUP);
    }

}
