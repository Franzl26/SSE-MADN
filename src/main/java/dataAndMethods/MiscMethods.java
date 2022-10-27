package dataAndMethods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dataAndMethods.FieldState.*;

public class MiscMethods {
    /**
     * überprüft, ob der Zug gesetzt werden durfte, unabhängig von einem eventuellen Prio Zug
     */
    public static boolean checkMoveValid(BoardState boardState, FieldState player, int from, int to, int dice) {
        FieldState[] board = boardState.getBoardState();
        // richtige Spielfigur/nicht selber schlagen
        if (board[from] != player || board[to] == player) return false;

        boolean b = ((from - 32 + dice) % 40 + 32) == to;

        // Spielzug auf Weg
        if (from >= 32 && to >= 32 && b && board[to] != player) {
            // nicht über Start beachten
            int i = from - 32;
            if (player == FIELD_FIGURE0 && !((i + dice) >= 40)) return true;
            if (player == FIELD_FIGURE1 && !((i + dice) >= 10 && i < 10)) return true;
            if (player == FIELD_FIGURE2 && !((i + dice) >= 20 && i < 20)) return true;
            if (player == FIELD_FIGURE3 && !((i + dice) >= 30 && i < 30)) return true;
        }
        // raus rücken
        if (dice == 6) {
            //noinspection ConstantConditions
            if (player == FIELD_FIGURE0 && from >= 0 && from < 4 && to == 32 && board[to] != FIELD_FIGURE0) return true;
            if (player == FIELD_FIGURE1 && from >= 4 && from < 8 && to == 42 && board[to] != FIELD_FIGURE1) return true;
            if (player == FIELD_FIGURE2 && from >= 8 && from < 12 && to == 52 && board[to] != FIELD_FIGURE2)
                return true;
            if (player == FIELD_FIGURE3 && from >= 12 && from < 16 && to == 62 && board[to] != FIELD_FIGURE3)
                return true;
        }
        // rein rücken
        if (player == FIELD_FIGURE0 && from >= 66 && from <= 71 && to >= 16 && to <= 19 && from + dice == to + 56) {
            for (int i = 16; i < to; i++) if (board[i] == FIELD_FIGURE0) return false;
            return true;
        }
        if (player == FIELD_FIGURE1 && from >= 36 && from <= 41 && to >= 20 && to <= 23 && from + dice == to + 22) {
            for (int i = 20; i < to; i++) if (board[i] == FIELD_FIGURE1) return false;
            return true;
        }
        if (player == FIELD_FIGURE2 && from >= 46 && from <= 51 && to >= 24 && to <= 27 && from + dice == to + 28) {
            for (int i = 24; i < to; i++) if (board[i] == FIELD_FIGURE2) return false;
            return true;
        }
        if (player == FIELD_FIGURE3 && from >= 56 && from <= 61 && to >= 28 && to <= 31 && from + dice == to + 34) {
            for (int i = 28; i < to; i++) if (board[i] == FIELD_FIGURE3) return false;
            return true;
        }
        // in Zielfeldern rücken
        if (player == FIELD_FIGURE0 && from >= 16 && from <= 19 && to >= 17 && to <= 19 && from + dice == to) {
            for (int i = from + 1; i < to; i++) if (board[i] == FIELD_FIGURE0) return false;
            return true;
        }
        if (player == FIELD_FIGURE1 && from >= 20 && from <= 23 && to >= 21 && to <= 23 && from + dice == to) {
            for (int i = from + 1; i < to; i++) if (board[i] == FIELD_FIGURE1) return false;
            return true;
        }
        if (player == FIELD_FIGURE2 && from >= 24 && from <= 27 && to >= 25 && to <= 27 && from + dice == to) {
            for (int i = from + 1; i < to; i++) if (board[i] == FIELD_FIGURE2) return false;
            return true;
        }
        if (player == FIELD_FIGURE3 && from >= 28 && from <= 31 && to >= 29 && to <= 31 && from + dice == to) {
            for (int i = from + 1; i < to; i++) if (board[i] == FIELD_FIGURE3) return false;
            return true;
        }
        return false;
    }

    /**
     * @return null, falls kein Prio Move vorhanden, sonst [from, to]
     */
    public static int[] checkForPrioMove(BoardState boardState, FieldState player, int dice) {
        FieldState[] board = boardState.getBoardState();
        // rausrücken
        if (dice == 6) {
            if (player == FIELD_FIGURE0) for (int i = 0; i < 4; i++)
                if (board[i] != FIELD_NONE && board[32] != FIELD_FIGURE0) return new int[]{i, 32};
            if (player == FIELD_FIGURE1) for (int i = 4; i < 8; i++)
                if (board[i] != FIELD_NONE && board[42] != FIELD_FIGURE1) return new int[]{i, 42};
            if (player == FIELD_FIGURE2) for (int i = 8; i < 12; i++)
                if (board[i] != FIELD_NONE && board[52] != FIELD_FIGURE2) return new int[]{i, 52};
            if (player == FIELD_FIGURE3) for (int i = 12; i < 16; i++)
                if (board[i] != FIELD_NONE && board[62] != FIELD_FIGURE3) return new int[]{i, 62};
        }
        // abrücken
        if (player == FIELD_FIGURE0 && board[32] == FIELD_FIGURE0 && board[32 + dice] != FIELD_FIGURE0
                && (boardState.getField(0) == FIELD_FIGURE0 || boardState.getField(1) == FIELD_FIGURE0 || boardState.getField(2) == FIELD_FIGURE0 || boardState.getField(3) == FIELD_FIGURE0))
            return new int[]{32, 32 + dice};
        if (player == FIELD_FIGURE1 && board[42] == FIELD_FIGURE1 && board[42 + dice] != FIELD_FIGURE1
                && (boardState.getField(4) == FIELD_FIGURE1 || boardState.getField(5) == FIELD_FIGURE1 || boardState.getField(6) == FIELD_FIGURE1 || boardState.getField(7) == FIELD_FIGURE1))
            return new int[]{42, 42 + dice};
        if (player == FIELD_FIGURE2 && board[52] == FIELD_FIGURE2 && board[52 + dice] != FIELD_FIGURE2
                && (boardState.getField(8) == FIELD_FIGURE2 || boardState.getField(9) == FIELD_FIGURE2 || boardState.getField(10) == FIELD_FIGURE2 || boardState.getField(11) == FIELD_FIGURE2))
            return new int[]{52, 52 + dice};
        if (player == FIELD_FIGURE3 && board[62] == FIELD_FIGURE3 && board[62 + dice] != FIELD_FIGURE3
                && (boardState.getField(12) == FIELD_FIGURE3 || boardState.getField(13) == FIELD_FIGURE3 || boardState.getField(14) == FIELD_FIGURE3 || boardState.getField(15) == FIELD_FIGURE3))
            return new int[]{62, 62 + dice};
        // schlagen
        for (int i = 39; i >= 0; i--) {
            if (board[i + 32] == player && board[(i + dice) % 40 + 32] != player && board[(i + dice) % 40 + 32] != FIELD_NONE) {
                // nicht über Start beachten
                if (player == FIELD_FIGURE0 && (i + dice) >= 40) continue;
                if (player == FIELD_FIGURE1 && (i + dice) >= 10 && i < 10) continue;
                if (player == FIELD_FIGURE2 && (i + dice) >= 20 && i < 20) continue;
                if (player == FIELD_FIGURE3 && (i + dice) >= 30 && i < 30) continue;
                return new int[]{i + 32, (i + dice) % 40 + 32};
            }
        }
        return null;
    }

    /**
     * @return [-1, -1] falls kein Zug möglich, sonst [from, to] berücksichtigt Prio
     */
    public static int[] getValidMove(BoardState boardState, FieldState field, int wurf) {
        int[] prio = checkForPrioMove(boardState, field, wurf);
        if (prio != null) return prio;
        // innerhalb Zielfelder
        if (field == FIELD_FIGURE0) {
            if (checkMoveValid(boardState, field, 16, 16 + wurf, wurf)) return new int[]{16, 16 + wurf};
            if (checkMoveValid(boardState, field, 17, 17 + wurf, wurf)) return new int[]{17, 17 + wurf};
            if (checkMoveValid(boardState, field, 18, 18 + wurf, wurf)) return new int[]{18, 18 + wurf};
        }
        if (field == FIELD_FIGURE1) {
            if (checkMoveValid(boardState, field, 20, 20 + wurf, wurf)) return new int[]{20, 20 + wurf};
            if (checkMoveValid(boardState, field, 21, 21 + wurf, wurf)) return new int[]{21, 21 + wurf};
            if (checkMoveValid(boardState, field, 22, 22 + wurf, wurf)) return new int[]{22, 22 + wurf};
        }
        if (field == FIELD_FIGURE2) {
            if (checkMoveValid(boardState, field, 24, 24 + wurf, wurf)) return new int[]{24, 24 + wurf};
            if (checkMoveValid(boardState, field, 25, 25 + wurf, wurf)) return new int[]{25, 25 + wurf};
            if (checkMoveValid(boardState, field, 26, 26 + wurf, wurf)) return new int[]{26, 26 + wurf};
        }
        if (field == FIELD_FIGURE3) {
            if (checkMoveValid(boardState, field, 28, 28 + wurf, wurf)) return new int[]{28, 28 + wurf};
            if (checkMoveValid(boardState, field, 29, 29 + wurf, wurf)) return new int[]{29, 29 + wurf};
            if (checkMoveValid(boardState, field, 30, 30 + wurf, wurf)) return new int[]{30, 30 + wurf};
        }
        // in Zielfelder
        if (field == FIELD_FIGURE0) for (int i = 71; i > 71 - wurf; i--)
            if (boardState.getField(i) == FIELD_FIGURE0 && i + wurf < 76)
                if (checkMoveValid(boardState, field, i, i + wurf - 56, wurf)) return new int[]{i, i + wurf - 56};
        if (field == FIELD_FIGURE1) for (int i = 41; i > 41 - wurf; i--)
            if (boardState.getField(i) == FIELD_FIGURE1 && i + wurf < 46)
                if (checkMoveValid(boardState, field, i, i + wurf - 22, wurf)) return new int[]{i, i + wurf - 22};
        if (field == FIELD_FIGURE2) for (int i = 51; i > 51 - wurf; i--)
            if (boardState.getField(i) == FIELD_FIGURE2 && i + wurf < 56)
                if (checkMoveValid(boardState, field, i, i + wurf - 28, wurf)) return new int[]{i, i + wurf - 28};
        if (field == FIELD_FIGURE3) for (int i = 61; i > 61 - wurf; i--)
            if (boardState.getField(i) == FIELD_FIGURE3 && i + wurf < 66)
                if (checkMoveValid(boardState, field, i, i + wurf - 34, wurf)) return new int[]{i, i + wurf - 34};
        // sonst zufällig
        int[] start = new int[4];
        int count = 0;
        for (int i = 32; i < 72; i++) {
            if (boardState.getField(i) == field) {
                if (checkMoveValid(boardState, field, i, (i - 32 + wurf) % 40 + 32, wurf)) start[count++] = i;
            }
        }
        if (count > 0) {
            int auswahl = start[(int) (Math.random() * count)];
            return new int[]{auswahl, (auswahl - 32 + wurf) % 40 + 32};
        }

        return new int[]{-1, -1};
    }

    private static final Pattern pwPattern1 = Pattern.compile("[!§$%&/()=?#a-zA-Z\\d]{8,15}");
    private static final Pattern pwPattern2 = Pattern.compile(".*[!§$%&/()=?#]+.*");
    private static final Pattern pwPattern3 = Pattern.compile(".*[a-zA-Z]+.*");
    private static final Pattern pwPattern4 = Pattern.compile(".*\\d+.*");
    private static final Pattern namePattern1 = Pattern.compile("[A-Za-z]{3,8}");
    private static final Pattern namePattern2 = Pattern.compile("([bB][oO][tT]).*");

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean checkPasswordGuidelines(String pw) {
        Matcher match1 = pwPattern1.matcher(pw);
        Matcher match2 = pwPattern2.matcher(pw);
        Matcher match3 = pwPattern3.matcher(pw);
        Matcher match4 = pwPattern4.matcher(pw);
        return match1.matches() && match2.matches() && match3.matches() && match4.matches();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean checkUsernameGuidelines(String name) {
        Matcher match1 = namePattern1.matcher(name);
        Matcher match2 = namePattern2.matcher(name);
        return match1.matches() && !match2.matches();
    }
}
