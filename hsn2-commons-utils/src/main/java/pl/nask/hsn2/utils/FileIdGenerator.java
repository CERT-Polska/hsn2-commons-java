/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.1.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.nask.hsn2.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileIdGenerator implements IdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileIdGenerator.class);
    private static final String DEFULT_FILE_NAME = ".seq";
    private String seqPath = DEFULT_FILE_NAME;
    private boolean forceCreate = true;

    @Override
    public final synchronized long nextId() throws IdGeneratorException {
        long value = this.load(forceCreate);
        if (value == Long.MAX_VALUE) {
            throw new IllegalStateException("Cannot increase counter: Long.MAX_VALUE reached");
        }
        value++;
        try {
            this.save(value);
        } catch (IOException e) {
            throw new IdGeneratorException("Cannot save new counter.", e);
        }
        return value;
    }

    @Override
    public final void reset() throws IdGeneratorException {
        try {
            this.save(0L);
        } catch (IOException e) {
            throw new IdGeneratorException("Cannot reset counter.", e);
        }
    }

    private long load(boolean create) throws IdGeneratorException {

        BufferedReader bufferedReader = null;
        try {
            LOGGER.info("Using sequence file: {}", this.seqPath);
            bufferedReader = new BufferedReader(new FileReader(this.seqPath));
            return Long.parseLong(bufferedReader.readLine());

        } catch (IOException | NumberFormatException e) {

            if (!create) {
                LOGGER.info("Sequence file {} does not exist or is corrupted. Force creation is not set.", this.seqPath);
                throw new IdGeneratorException("Sequence file doesn't exist: " + this.seqPath, e);
            }

            LOGGER.warn("Sequence file {} does not exist or is corrupted. Will create newone.", this.seqPath);

            try {
                save(0L);
                return 0L;
            } catch (IOException e1) {
                LOGGER.error("Failed to create new sequence file {}", this.seqPath);
                LOGGER.error("IOException creating a file", e);
                throw new IdGeneratorException("Error creating sequence file.", e1);
            }
        } finally {
            IOUtils.closeQuietly(bufferedReader);
        }
    }

    private void save(long value) throws IOException {
        FileWriter fw = new FileWriter(this.seqPath);
        try {
            fw.write("" + value);
        } finally {
            fw.close();
        }
    }

    public final boolean seqFileExists() {
        return new File(this.seqPath).exists();
    }

    public final synchronized void setForceCreate(boolean forceCreate) {
        this.forceCreate = forceCreate;
    }

    public final void setSequenceFileDirPath(String dirPath) {
        this.setSequenceFile(dirPath, DEFULT_FILE_NAME);
    }

    public final void setSequenceFile(String dirPath, String fileName) {
        this.seqPath = dirPath + File.separator + fileName;
    }

    public final void setSequenceFile(String path) {
        this.seqPath = path;
    }
}
