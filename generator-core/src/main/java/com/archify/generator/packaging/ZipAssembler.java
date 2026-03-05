package com.archify.generator.packaging;

import com.archify.generator.generation.FileLeaf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipAssembler {
    public byte[] zip(List<FileLeaf> files) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                for (FileLeaf file : files) {
                    ZipEntry zipEntry = new ZipEntry(file.getPath());
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(file.getContent().getBytes(StandardCharsets.UTF_8));
                    zipOutputStream.closeEntry();
                }
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to assemble zip", exception);
        }
    }
}
