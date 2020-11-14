package top.shiftregister.safeout;

public class MyJsonTools {

    static String removeBackSlash(String resource) {
        char[] a1 = resource.toCharArray();
        char[] ret = new char[resource.length() + 2];
        int ptr = 0;
        for (char i : a1) {
            if (i != '\\') {
                ret[ptr++] = i;
            }
        }
        ret[ptr] = 0;
        return new String(ret);
    }
}
