package com.qixalite.spongestart.tasks;

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

        File f = new File(".idea/workspace.xml");

        try {

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);

            Node run = null;
            NodeList component = document.getElementsByTagName("component");
            for (int i = 0; i < component.getLength(); i++) {
                if (component.item(i).getAttributes().getNamedItem("name").getNodeValue().equals("RunManager")) {
                    run = component.item(i);
                    break;
                }
            }

            int size = -1;

            while (size != 0) {
                size = 0;
                Element e = (Element) run.getChildNodes();
                NodeList conf = e.getElementsByTagName("configuration");
                for (int c = 0; c < conf.getLength(); c++) {
                    Node n = conf.item(c);
                    Node nm = n.getAttributes().getNamedItem("name");
                    if (nm != null) {
                        String name = nm.getNodeValue();
                        if (name.equals(this.name)) {
                            size++;
                            e.removeChild(n);
                        }
                    }
                }
            }

            Element configuration = document.createElement("configuration");
            configuration.setAttribute("name", name );
            configuration.setAttribute("type", "Application");

            Element mainName = document.createElement("option");
            mainName.setAttribute("name", "MAIN_CLASS_NAME");
            mainName.setAttribute("value", main);

            Element VMargs = document.createElement("option");
            Element programParameters = document.createElement("option");

            VMargs.setAttribute("name", "VM_PARAMETERS");

            VMargs.setAttribute("value", vargs);

            programParameters.setAttribute("name", "PROGRAM_PARAMETERS");
            programParameters.setAttribute("value", pargs);


            Element workingDir = document.createElement("option");
            workingDir.setAttribute("name", "WORKING_DIRECTORY");
            workingDir.setAttribute("value", dir);

            Element moduleName = document.createElement("module");
            moduleName.setAttribute("name", module);


            configuration.appendChild(mainName);

            configuration.appendChild(VMargs);
            configuration.appendChild(programParameters);

            configuration.appendChild(workingDir);
            configuration.appendChild(moduleName);

            run.appendChild(configuration);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(f);
            Source input = new DOMSource(document);

            transformer.transform(input, output);

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
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
