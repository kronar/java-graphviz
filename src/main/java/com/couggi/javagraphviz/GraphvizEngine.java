package com.couggi.javagraphviz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Graphviz engine to generate of graph output
 *
 * @author Everton Cardoso
 */
public class GraphvizEngine {

    private static final Logger LOGGER = Logger.getLogger(GraphvizEngine.class.getName());

    private Map<String, OutputType> type;
    private Graph graph;
    private String layoutManager;

    /**
     * directory path where the dot command will be executed.
     */
    private String directoryPathExecute = ".";

    /**
     * create the engine. type defualt = xdot.
     */
    public GraphvizEngine(Graph graph) {
        this.graph = graph;
        this.type = new HashMap<String, OutputType>();
        this.type.put("png", new OutputType("png"));
        this.layoutManager = "dot";
    }


    public static GraphVizRenderingResult output(File inputDotFile, String pathToDotExecutable, OutputType[] outTypes, String workingDir) {

        List<File> fls = new ArrayList<File>();

        try {
            StringBuilder outputTypes = new StringBuilder();
            for (OutputType type : outTypes) {
                outputTypes.append(" -T")
                        .append(type.name())
                        .append(" -o")
                        .append(type.filePath());

                fls.add(new File(type.filePath()));
            }

            String dotCommand = pathToDotExecutable + outputTypes + " " + inputDotFile.getPath();
            Process process = Runtime.getRuntime().exec(dotCommand, null, new File(workingDir));

            int exitVal = process.waitFor();


            return new GraphVizRenderingResult(exitVal, fls);


        } catch (IOException | InterruptedException e) {

            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "command error", e);
            }
            throw new GraphvizOutputException(e.getMessage(), e);

        }
    }


    /**
     * generate the output file
     */
    public void output() {
        File tmpDot = createDotFileTemp("in", graph.output());
        Collection<OutputType> values = this.type.values();
        OutputType[] a = new OutputType[values.size()];
        values.toArray(a);
        output(tmpDot, findExecutable(layoutManager), a, directoryPathExecute);
    }


    private String findExecutable(String prog) {

        String[] paths = System.getenv().get("PATH").split(File.pathSeparator);
        for (String path : paths) {
            String file = (path == null) ? prog : (path + File.separator + prog);
            if (new File(file).canExecute() && !new File(file).isDirectory()) {
                return file;
            }
        }
        throw new GraphvizEngineException(prog + " program not found.");
    }

    /**
     * create a file temp with the content of the dot.
     *
     * @param dotContent
     * @return
     */
    private File createDotFileTemp(String suffix, String dotContent) {
        try {
            File temp = File.createTempFile("graph", suffix);
            if (dotContent != null) {
                BufferedWriter out = new BufferedWriter(new FileWriter(temp));
                out.write(dotContent);
                out.close();
            }
            return temp;
        } catch (IOException e) {
            throw new GraphvizOutputException(e.getMessage(), e);
        }
    }

    /**
     * type of output
     */
    public List<OutputType> types() {
        return new ArrayList<OutputType>(type.values());
    }

    /**
     * define where the dot command will be executed.
     *
     * @param path
     * @return
     */
    public GraphvizEngine fromDirectoryPath(String path) {
        this.directoryPathExecute = path;
        return this;
    }

    /**
     * set or add a output type.
     */
    public OutputType addType(String name) {
        OutputType output = type.get(name);
        if (output == null) {
            output = new OutputType(name);
            type.put(name, output);
        }

        return this.type.get(name);
    }

    /**
     * remove a output type.
     */
    public GraphvizEngine removeType(String name) {
        if (type.size() == 1) {
            throw new IllegalStateException("must be a type defined.");
        }

        type.remove(name);

        return this;
    }

    /**
     * Set the layout manager. Available options are: dot, neato, fdp, sfdp, twopi, circo
     */
    public GraphvizEngine layout(String layoutManager) {
        this.layoutManager = layoutManager;
        return this;
    }

    /**
     * set filePath of the output type. only used method when exist a output type.
     *
     * @param filePath
     */
    public GraphvizEngine toFilePath(String filePath) {
        this.type.values().iterator().next().toFilePath(filePath);

        return this;
    }


}
