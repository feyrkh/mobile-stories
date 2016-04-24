package com.liquidenthusiasm;

import jBCrypt.BCrypt;

public final class BCryptUtil {

    private BCryptUtil() {
    }

    public static int workFactor = 12;

    public static String hashpw(String pw) {
        return BCrypt.hashpw(pw, BCrypt.gensalt(workFactor));
    }

    public static boolean checkpw(String candidatePw, String hashedPw) {
        return BCrypt.checkpw(candidatePw, hashedPw);
    }
}
