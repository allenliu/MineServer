package mineserver.util;

public class StringUtil {

    public static String join(String[] args, String glue) {
        int n = args.length;
        if (n == 0) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        out.append(args[0]);
        for (int i = 1; i < n; i++)
          out.append(glue).append(args[i]);
        return out.toString();
    }

}
