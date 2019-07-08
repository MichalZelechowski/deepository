package org.mz.deepository.lego.builder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.io.IOUtils;

class Buildings {

    private final String path;
    private final BuildingCodec codec;

    Buildings(String path) {
        this.path = path;
        this.codec = new BuildingCodec();
    }

    void store(Stream<Building> generatedData) throws IOException {
        ObjectOutputStream writer = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(path))));
        generatedData.forEach((Building building) -> {
            Integer[] encodedBuilding = this.codec.encode(building);
            try {
                writer.writeObject(encodedBuilding);
            } catch (IOException exception) {
                throw new IllegalStateException("Cannot write encoded bulding", exception);
            }
        });
        writer.flush();
        writer.close();
    }

    Stream<Building> load() throws IOException {
        final SerializedObjectsIterator iterator = new SerializedObjectsIterator(path);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false).
                onClose(() -> iterator.close()
                );
    }

    @Override
    public String toString() {
        return "Buildings{" + "path=" + path + '}';
    }

    public static void main(String args[]) throws IOException {
        String targetPath = args[0];

        Buildings buildings = new Buildings(targetPath);

        buildings.store(new BuildingGenerator(Integer.parseInt(args[2])).generate(Integer.parseInt(args[1])));
    }

    private static class SerializedObjectsIterator implements Iterator<Building> {

        private final ObjectInputStream reader;
        private final BuildingCodec codec = new BuildingCodec();
        private boolean objectRead = false;
        private Building objectValue = null;

        public SerializedObjectsIterator(String path) throws IOException {
            this.reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path), 8196 * 2));
        }

        @Override
        public boolean hasNext() {
            return readValue() != null;
        }

        @Override
        public Building next() {
            Building result = this.readValue();
            this.objectRead = false;
            return result;
        }

        private Building readValue() {
            if (this.objectRead) {
                return this.objectValue;
            }
            try {
                Integer[] encodedBuilding = (Integer[]) this.reader.readObject();
                this.objectValue = this.codec.decode(encodedBuilding);
                return this.objectValue;
            } catch (EOFException ex) {
                IOUtils.closeQuietly(this.reader);
                return null;
            } catch (Exception ex) {
                throw new IllegalStateException("Cannot read object from stream", ex);
            } finally {
                this.objectRead = true;
            }
        }

        public void close() {
            IOUtils.closeQuietly(this.reader);
        }
    }

}
