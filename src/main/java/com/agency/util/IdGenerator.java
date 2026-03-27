package com.agency.util;


import java.util.ArrayList;
import java.util.List;

public class IdGenerator {

    //Singleton instance (only one object is allowed)
    //So this ensures only one instance of IdGenerator exists in the entire app.
    //which prevents multiple objects generating conflicting IDs
    private static IdGenerator instance = null;

    //Counters stored in this file to survive application restarts
    private static final String COUNTERS_FILE = AppInitialiser.COUNTERS_FILE;

    /**Private constructor - prevents external instantiation. */
    //So it cannot call new IdGenerator() from outside
    private IdGenerator() {
        //Actually AppInititaliser is responsible for creating all files before IdGenerator is ever used.
        // defensive fallback - AppInitialiser should have created this already
        FileUtil.ensureFileExists(COUNTERS_FILE);
    }

    /**Thread-safe method to get the instance of IdGenerator. */
    //Thread safety is the case where if two threads call this at the same time then only one creates the instance.
    public static synchronized IdGenerator getInstance() {
        if(instance == null) {
            instance = new IdGenerator();
        }
        return instance;
    }

    /**Returns the next unique ID for a given entity.
     * This method is synchronized to prevent race conditions. */
    public synchronized int nextId(String entity) {
        int current = readCounter(entity);
        int next = current +1;
        writeCounter(entity, next);
        return next;
    }

    /**Reads the current counter value for a given entity from file. */
    private int readCounter(String entity){
        List<String> lines = FileUtil.readAllLines(COUNTERS_FILE);

        for(String line: lines) {
            String[] parts = line.split("=");

            if(parts.length == 2 && parts[0].equals(entity)) {
                return Integer.parseInt(parts[1]);
            }
        }

        //If entity not found initialize it with 0
        FileUtil.appendLine(COUNTERS_FILE, entity + "=0");
        return 0;
    }

    /**Updates the counter value for a given entity in the file. */
    private void writeCounter(String entity, int newValue) {
        List<String> lines = FileUtil.readAllLines(COUNTERS_FILE);
        List<String> updatedLines = new ArrayList<>();

        boolean found = false;

        for (String line: lines) {
            if (line.startsWith(entity + "=")) {
                updatedLines.add(entity + "=" + newValue);
                found = true;
            } else {
                updatedLines.add(line);
            }
        }
            //If not found we have to add it
            if(!found) {
                updatedLines.add(entity + "=" + newValue);
            }

            FileUtil.writeAllLines(COUNTERS_FILE, updatedLines);

    }
}
