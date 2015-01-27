package com.couggi.javagraphviz;

import java.io.File;
import java.util.List;

/**
 * Created by nikita on 27.01.15.
 */
public class GraphVizRenderingResult {


    private final int exitCode;
    private final List<File> outputFiles;

    public GraphVizRenderingResult(int exitCode, List<File> outputFiles) {
        this.exitCode = exitCode;
        this.outputFiles = outputFiles;
    }

    public int getExitCode() {
        return exitCode;
    }

    public List<File> getOutputFiles() {
        return outputFiles;
    }
}
