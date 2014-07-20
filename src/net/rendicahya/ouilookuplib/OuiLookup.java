package net.rendicahya.ouilookuplib;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

public class OuiLookup {

    private static final Pattern macAddressPattern;
    private static final Pattern replace;
    private static List<String> oui;

    static {
        macAddressPattern = Pattern.compile("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$");
        replace = Pattern.compile("[:-]");
        InputStream stream = OuiLookup.class.getResourceAsStream("/net/rendicahya/ouilookuplib/oui.txt");

        try {
            oui = IOUtils.readLines(stream);
        } catch (IOException ex) {
            System.err.println("Could not read OUI file.");
            System.exit(1);
        }
    }

    public static String lookup(String input) throws IOException {
        if (!macAddressPattern.matcher(input).matches()) {
            return "Invalid MAC address format";
        }

        String inputPrefix = replace.matcher(input).replaceAll("").substring(0, 6);
        int lines = oui.size();

        for (int i = 6; i < lines; i++) {
            String line = oui.get(i);

            if (line.length() > 7 && line.substring(2, 8).equals(inputPrefix)) {
                return line.substring(22).trim();
            }
        }

        return "Could not find " + input;
    }

    public static List<String> lookup(List<String> macAddresses) throws IOException {
        List<String> result = new ArrayList<>();

        for (String input : macAddresses) {
            result.add(lookup(input));
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(OuiLookup.lookup("DC:85:DE:2A:03:07"));
    }
}
