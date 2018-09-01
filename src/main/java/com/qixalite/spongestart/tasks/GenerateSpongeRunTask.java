package com.qixalite.spongestart.tasks;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.plugins.ide.idea.model.IdeaModel;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public abstract class GenerateSpongeRunTask extends GenerateRunTask {

    @Override
    public void refresh() {
        super.refresh();
        StringBuilder s = new StringBuilder("-classpath ");

        s.append(getServerPath()).append(File.separatorChar).append("server.jar");

        if (!getProject().getPlugins().hasPlugin("net.minecrell.vanillagradle.server")) {
            String outputFolder = new File(getProject().getRootDir(),
                    (getExtension().getIdeaOutput() == null ? "out" : getExtension().getIdeaOutput()) + File.separatorChar + "production")
                    .getAbsolutePath();

            s.append(';')
                    .append(outputFolder)
                    .append(File.separatorChar)
                    .append(getProject().getExtensions().getByType(IdeaModel.class).getModule().getName())
                    .append("_main");

            List<Configuration> configurations = Arrays.asList(
                    getProject().getConfigurations().getByName("compile"),
                    getProject().getConfigurations().getByName("compileOnly"),
                    getProject().getConfigurations().getByName("runtime")
            );

            configurations.forEach(configuration -> {
                configuration.getDependencies().forEach(dependency -> {
                    if (dependency instanceof ProjectDependency) {
                        s.append(';')
                                .append(outputFolder)
                                .append(File.separatorChar)
                                .append(((ProjectDependency) dependency).getDependencyProject().getExtensions().getByType(IdeaModel.class).getModule()
                                        .getName())
                                .append("_main");
                    }
                });
            });
        }

        setVargs(s.toString());
    }

    public abstract String getServerPath();
}
