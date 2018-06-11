package com.qixalite.spongestart.tasks;

import com.qixalite.spongestart.SpongeStart;
import org.gradle.api.DefaultTask;

abstract class SpongeStartTask extends DefaultTask {

    SpongeStartTask() {
        setGroup(SpongeStart.NAME);
    }

}
