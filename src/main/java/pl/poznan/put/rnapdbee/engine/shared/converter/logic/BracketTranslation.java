package pl.poznan.put.rnapdbee.engine.shared.converter.logic;

public class BracketTranslation {

    public static char getStartingBracket(int i) {
        switch (i) {
            case 0:
                return '(';
            case 1:
                return '[';
            case 2:
                return '{';
            case 3:
                return '<';
            case 4:
                return 'A';
            case 5:
                return 'B';
            case 6:
                return 'C';
            case 7:
                return 'D';
            case 8:
                return 'E';
            case 9:
                return 'F';
        }
        return 'Z';
    }

    public static char getEndingBracket(int i) {
        switch (i) {
            case 0:
                return ')';
            case 1:
                return ']';
            case 2:
                return '}';
            case 3:
                return '>';
            case 4:
                return 'a';
            case 5:
                return 'b';
            case 6:
                return 'c';
            case 7:
                return 'd';
            case 8:
                return 'e';
            case 9:
                return 'f';
        }
        return 'z';
    }
}
