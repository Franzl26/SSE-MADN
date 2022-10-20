package DataAndMethods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static DataAndMethods.FieldState.*;

public class MiscMethods {
    public static boolean checkMoveValid(BoardState boardState, FieldState player, int from, int to, int dice) {
        FieldState[] board = boardState.getBoardState();
        // richtige Spielfigur/nicht selber schlagen
        if (board[from] != player || board[to] == player) return false;

        // Prio-Zug durchgeführt
        int prio = checkForPrioMove(boardState, player, dice);
        boolean b = ((from - 32 + dice) % 40 + 32) == to;
        if (prio != -1 && prio == from && b) return true;

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
            if (player == FIELD_FIGURE0 && from > 0 && from < 4 && to == 32 && board[to] != FIELD_FIGURE0) return true;
            if (player == FIELD_FIGURE1 && from > 4 && from < 8 && to == 42 && board[to] != FIELD_FIGURE1) return true;
            if (player == FIELD_FIGURE2 && from > 8 && from < 12 && to == 52 && board[to] != FIELD_FIGURE2) return true;
            if (player == FIELD_FIGURE3 && from > 12 && from < 16 && to == 62 && board[to] != FIELD_FIGURE3)
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

    public static int checkForPrioMove(BoardState boardState, FieldState player, int dice) {
        FieldState[] board = boardState.getBoardState();
        // rausrücken
        if (dice == 6) {
            if (player == FIELD_FIGURE0)
                for (int i = 0; i < 4; i++) if (board[i] != FIELD_NONE && board[32] != FIELD_FIGURE0) return i;
            if (player == FIELD_FIGURE1)
                for (int i = 4; i < 8; i++) if (board[i] != FIELD_NONE && board[42] != FIELD_FIGURE1) return i;
            if (player == FIELD_FIGURE2)
                for (int i = 8; i < 12; i++) if (board[i] != FIELD_NONE && board[52] != FIELD_FIGURE2) return i;
            if (player == FIELD_FIGURE3)
                for (int i = 12; i < 16; i++) if (board[i] != FIELD_NONE && board[62] != FIELD_FIGURE3) return i;
        }
        // abrücken
        if (player == FIELD_FIGURE0 && board[32] == FIELD_FIGURE0 && board[32 + dice] != FIELD_FIGURE0) return 32;
        if (player == FIELD_FIGURE1 && board[42] == FIELD_FIGURE1 && board[42 + dice] != FIELD_FIGURE1) return 42;
        if (player == FIELD_FIGURE2 && board[52] == FIELD_FIGURE2 && board[52 + dice] != FIELD_FIGURE2) return 52;
        if (player == FIELD_FIGURE3 && board[62] == FIELD_FIGURE3 && board[62 + dice] != FIELD_FIGURE3) return 62;
        // schlagen
        for (int i = 39; i >= 0; i--) {
            if (board[i + 32] == player && board[(i + dice) % 40 + 32] != player && board[(i + dice) % 40 + 32] != FIELD_NONE) {
                // nicht über Start beachten
                if (player == FIELD_FIGURE0 && (i + dice) >= 40) continue;
                if (player == FIELD_FIGURE1 && (i + dice) >= 10 && i < 10) continue;
                if (player == FIELD_FIGURE2 && (i + dice) >= 20 && i < 20) continue;
                if (player == FIELD_FIGURE3 && (i + dice) >= 30 && i < 30) continue;
                return i + 32;
            }
        }
        return -1;
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
