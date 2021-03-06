package mineserver;

public enum Color {

    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_CYAN('3'),
    DARK_RED('4'),
    PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    CYAN('b'),
    RED('c'),
    PINK('d'),
    YELLOW('e'),
    WHITE('f');

    private char code;

    Color(char code) {
        this.code = code;
    }

    public String toString() {
        return "\u00a7" + code;
    }
}