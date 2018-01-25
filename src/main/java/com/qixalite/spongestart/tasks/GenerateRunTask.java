package com.qixalite.spongestart.tasks;

import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class GenerateRunTask extends SpongeStartTask {

    private String name;
    private String main;
    private String pargs = "";
    private String vargs = "";
    private String dir;
    private String module;

    @TaskAction
    public void doStuff() {

        File f = new File(".idea" + File.separatorChar + "workspace.xml");

        try {

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);

            Node run = null;
            NodeList comp = doc.getElementsByTagName("component");
            for (int i = 0; i < comp.getLength(); i++) {
                if (comp.item(i).getAttributes().getNamedItem("name").getNodeValue().equals("RunManager")) {
                    run = comp.item(i);
                    break;
                }
            }

            int count = -1;

            while (count != 0) {
                count = 0;
                Element e = (Element) run.getChildNodes();
                NodeList conf = e.getElementsByTagName("configuration");
                for (int c = 0; c < conf.getLength(); c++) {
                    Node n = conf.item(c);
                    Node nm = n.getAttributes().getNamedItem("name");
                    if (nm != null && nm.getNodeValue().equals(this.name)) {
                        count++;
                        e.removeChild(n);
                    }
                }
            }

            Element configuration = doc.createElement("configuration");
            configuration.setAttribute("name", name );
            configuration.setAttribute("type", "Application");

            Element mainName = doc.createElement("option");
            mainName.setAttribute("name", "MAIN_CLASS_NAME");
            mainName.setAttribute("value", main);

            Element virtualParameters = doc.createElement("option");
            Element programParameters = doc.createElement("option");

            virtualParameters.setAttribute("name", "VM_PARAMETERS");

            virtualParameters.setAttribute("value", vargs);

            programParameters.setAttribute("name", "PROGRAM_PARAMETERS");
            programParameters.setAttribute("value", pargs);


            Element workingDir = doc.createElement("option");
            workingDir.setAttribute("name", "WORKING_DIRECTORY");
            workingDir.setAttribute("value", dir);

            Element moduleName = doc.createElement("module");
            moduleName.setAttribute("name", module);


            configuration.appendChild(mainName);

            configuration.appendChild(virtualParameters);
            configuration.appendChild(programParameters);

            configuration.appendChild(workingDir);
            configuration.appendChild(moduleName);

            run.appendChild(configuration);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(f);
            Source input = new DOMSource(doc);

            transformer.transform(input, output);

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            throw new GradleException("Something went wrong with your workspace.xml: " + e.getMessage());
        }
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setPargs(String pargs) {
        this.pargs = pargs;
    }

    public void setVargs(String vargs) {
        this.vargs = vargs;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
