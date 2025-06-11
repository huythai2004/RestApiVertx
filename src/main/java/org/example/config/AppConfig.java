//package org.example.config;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//public class AppConfig {
//    private static final Properties props = new Properties();
//    static {
//        try (InputStream input = ClassLoader.getSystemResourceAsStream("application.properties")) {
//            if (input == null) {
//                System.out.println("Error: Unable to find application.properties file.");
//            } else {
//                props.load(input);
//                System.out.println("Application properties loaded successfully.");
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public static String get(String key) {
//        return props.getProperty(key);
//    }
//
//    public static int getInt(String key) {
//        String value = props.getProperty(key);
//        if(value == null) {
//            System.out.println("Warning: Property " + key + "is not set in application.properties.");
//            return 0;
//        }
//        try {
//            return Integer.parseInt(value.trim());
//        } catch (NumberFormatException e) {
//            System.err.println("Error: Property " + key + " is not a valid integer: " + value);
//            return 0;
//        }
//    }
//}
//

package org.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties props = new Properties();

    static {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream("application.properties");

            if (input == null) {
                System.err.println("ERROR: Unable to find application.properties in classpath!");
            } else {
                props.load(input);
                System.out.println("Application properties loaded successfully.");
                System.out.println("DEBUG: Properties object -> " + props);
                input.close(); // Đóng stream sau khi load
            }
        } catch (IOException ex) {
            System.err.println("ERROR: Failed to load application.properties");
            ex.printStackTrace();
        }
    }

    public static String get(String key) {
        if (props == null) {
            System.err.println("ERROR: Properties object is null!");
            return null;
        }
        String value = props.getProperty(key);
        System.out.println("DEBUG: Getting property '" + key + "' -> " + value);
        return value;
    }

    public static int getInt(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            System.err.println("WARNING: Property '" + key + "' is not set in application.properties.");
            return 0;
        }
        try {
            return Integer.parseInt(value.trim()); // Trim để loại bỏ khoảng trắng thừa
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Property '" + key + "' is not a valid integer: " + value);
            return 0;
        }
    }
}

