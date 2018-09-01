package com.qixalite.spongestart.tasks;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public abstract class GenerateSpongeRunTask extends GenerateRunTask {

    @Override
    public void refresh() {
        super.refresh();
        StringBuilder s = new StringBuilder("-classpath \"$PROJECT_DIR$" + File.separatorChar + getServerPath() + File.separatorChar + "server.jar\"" + File.pathSeparatorChar + '"');

        Configuration compile = getProject().getConfigurations().getByName("compile");
        ResolvedConfiguration resolvedconfig = compile.getResolvedConfiguration();

        StringBuilder api = new StringBuilder(15);
        resolvedconfig.getFirstLevelModuleDependencies()
                .forEach(res -> {
                    if (!res.getName().startsWith("org.spongepowered:spongeapi")) {
                        res.getAllModuleArtifacts().forEach(artifact -> s.append(artifact.getFile().getAbsolutePath()).append('"').append(File.pathSeparatorChar).append('"'));
                    } else {
                        api.replace(0, api.length(), res.getModuleVersion());
                    }
                });

        int isSnapshost = api.lastIndexOf("-");
        if (isSnapshost != -1) {
            api.delete(isSnapshost, api.length());
        }

        getExtension().setApi(api.toString());

        s.append(getExtension().getResourcesFolder()).append('"').append(File.pathSeparatorChar).append('"');

        File f = new File(getProject().getRootDir(), ".idea" + File.separatorChar + "misc.xml");

        try {

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);

            Node run = null;
            NodeList comp = doc.getElementsByTagName("component");
            for (int i = 0; i < comp.getLength(); i++) {
                if (comp.item(i).getAttributes().getNamedItem("name").getNodeValue().equals("ProjectRootManager")) {
                    run = comp.item(i);
                    break;
                }
            }

            String dir = null;
            Element e = (Element) run.getChildNodes();
            NodeList conf = e.getElementsByTagName("output");
            for (int c = 0; c < conf.getLength(); c++) {
                Node n = conf.item(c);
                Node nm = n.getAttributes().getNamedItem("url");
                dir = nm.getNodeValue();
            }

            Pattern pattern = Pattern.compile("\\$PROJECT_DIR\\$");

            s.append(getProject().getRootDir()).append(pattern.split(dir)[1])
                    .append(File.separatorChar).append("production")
                    .append(File.separatorChar).append(getModule());
        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }

        setVargs(s.toString());
    }

    public abstract String getServerPath();


}
