package com.archify.generator.generation;

public class FileLeaf {
    private final String path;
    private final String content;

    public FileLeaf(String path, String content) {
        this.path = path;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }
}
