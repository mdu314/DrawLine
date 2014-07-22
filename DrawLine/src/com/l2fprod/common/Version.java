/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Sun Feb 19 15:24:33 CET 2006 */
package com.l2fprod.common;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class Version {


   /** buildDate (set during build process to 1140359073234L). */
   private static Date buildDate = new Date(1140359073234L);

   /**
    * Get buildDate (set during build process to Sun Feb 19 15:24:33 CET 2006).
    * @return Date buildDate
    */
   public static final Date getBuildDate() { return buildDate; }


   /** buildTimestamp (set during build process to "02/19/2006 03:24 PM"). */
   private static String buildTimestamp = new String("02/19/2006 03:24 PM");

   /**
    * Get buildTimestamp (set during build process to "02/19/2006 03:24 PM").
    * @return String buildTimestamp
    */
   public static final String getBuildTimestamp() { return buildTimestamp; }


   /** year (set during build process to "2005-2006"). */
   private static String year = new String("2005-2006");

   /**
    * Get year (set during build process to "2005-2006").
    * @return String year
    */
   public static final String getYear() { return year; }


   /** version (set during build process to "6.2"). */
   private static String version = new String("6.2");

   /**
    * Get version (set during build process to "6.2").
    * @return String version
    */
   public static final String getVersion() { return version; }


   /** project (set during build process to "l2fprod-common"). */
   private static String project = new String("l2fprod-common");

   /**
    * Get project (set during build process to "l2fprod-common").
    * @return String project
    */
   public static final String getProject() { return project; }

}
