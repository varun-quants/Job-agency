package com.agency.util;

import java.util.List;

/**Bootstrap class that runs once at startup.
 * Responsible for ensuring all required fields and directories exist
 * before any other class tries to read from or write to them
 * Must be given the first call in main before anything else.
 */
public class AppInitialiser {

    //if a path ever changes it changes here only
    public static final String JOBS_FILE = "data/jobs.dat";
    public static final String SEEKERS_FILE = "data/seekers.dat";
    public static final String SKILLS_FILE = "data/skills.dat";
    public static final String JOB_SKILLS_FILE = "data/job_skills.dat";
    public static final String SEEKER_SKILLS_FILE = "data/seeker_skills.dat";
    public static final String COUNTERS_FILE = "data/id_counters.dat";

    /**Private constructor - static utility class, never instantiated. */
    private AppInitialiser(){}

    /**
     * Initialises the application environment.
     * Seeds the ID counters file if it is being created for the first time
     * Safe to call on every startup - all operations are idempotent.
     */
    public static void initialise(){
        createDataFiles();
        seedCountersIfEmpty();
        System.out.println("Application initialised successfully.");
    }

    /**
     * Uses FileUtil.ensureFileExists() which is idempotent so calling it upon an existing file does nothing.
     */
    private static void createDataFiles(){

    }

    /**
     * Seeds the counters file with starting values if it is empty.
     * Starting value is 0 for all entities - nextId() will increment
     * to 1 before returning, so the first record always gets ID 1.
     * Only runs on first ever startup - subsequent startups find
     * existing values and leave them untouched.
     */
    private static void seedCountersIfEmpty() {
        FileUtil.ensureFileExists(COUNTERS_FILE);
        List<String> lines = FileUtil.readAllLines(COUNTERS_FILE);

        if(lines.isEmpty()){
            FileUtil.appendLine(COUNTERS_FILE, "jobs=0");
            FileUtil.appendLine(COUNTERS_FILE, "seekers=0");
            FileUtil.appendLine(COUNTERS_FILE, "skills=0");
        }
    }
}
