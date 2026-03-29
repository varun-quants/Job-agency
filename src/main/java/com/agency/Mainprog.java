package com.agency;
/**
 Target of the assignment is to create an application as below
 where student number ends in 7
 Implement a system that is suitable for use by a job agency. The system should keep
 records of jobs available, together with the skills required for the job. It should also keep
 records of jobseekers and they skills that they can offer. The system must be able to
 provide a series of reports, for example matching jobs with job-seekers.
*/


import com.agency.ui.ConsoleApp;
import com.agency.util.AppInitialiser;

/**
 * Application entry point.
 * Two responsibilities only:
 *  1.Initialise the environment
 *  2.Hand control to the console
 * Nothing else belongs here
 */
public class Mainprog {
    public static void main(String[] args) {
        //bootstrap - creates all data files if they do not exist
        AppInitialiser.initialise();
        System.out.println();
        System.out.println();

        //start the console application
        new ConsoleApp().start();
        }
}