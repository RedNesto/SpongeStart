package com.qixalite.spongestart.tasks;

import com.qixalite.spongestart.SpongeStart;
import com.qixalite.spongestart.SpongeStartExtension;
import org.gradle.api.tasks.Delete;

public abstract class BaseDeleteTask extends Delete {

    private SpongeStartExtension extension;

    public BaseDeleteTask() {
        super();
        setGroup(SpongeStart.NAME);
    }

    protected SpongeStartExtension getExtension() {
        return this.extension;
    }

    public void setExtension(SpongeStartExtension extension) {
        this.extension = extension;
    }
}
