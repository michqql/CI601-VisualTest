package me.mp1282.visualtest.system.persistence.exceptions;

public class JarFileNotFoundException extends RuntimeException {
    public JarFileNotFoundException(String filename, String uri) {
        super(getFormatMessage(filename, uri));
    }

    private static String getFormatMessage(String filename, String uri) {
        return String.format(
                "JAR file (%s) not found at absolute location \"%s\" or within the workspace",
                filename, uri
        );
    }
}
