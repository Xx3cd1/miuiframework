package android.util;

public class PrefixPrinter implements Printer {
    private final String mPrefix;
    private final Printer mPrinter;

    public static Printer create(Printer printer, String prefix) {
        if (prefix == null || prefix.equals("")) {
            return printer;
        }
        return new PrefixPrinter(printer, prefix);
    }

    private PrefixPrinter(Printer printer, String prefix) {
        this.mPrinter = printer;
        this.mPrefix = prefix;
    }

    public void println(String str) {
        Printer printer = this.mPrinter;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mPrefix);
        stringBuilder.append(str);
        printer.println(stringBuilder.toString());
    }
}
