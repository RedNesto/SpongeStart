package com.qixalite.spongestart.tasks;

import com.qixalite.spongestart.SpongeStart;
import org.gradle.api.tasks.Copy;

public class CopyReobfToRunTask extends Copy {

    public CopyReobfToRunTask() {
        super();
        setGroup(SpongeStart.NAME);
        doLast(task -> getDestinationDir().mkdirs());
    }
}
