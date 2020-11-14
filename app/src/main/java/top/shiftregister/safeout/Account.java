package top.shiftregister.safeout;

public class Account {
    private String touristId;
    private boolean mode;

    Account() {
        touristId = null;
        mode = false;
    }

    Account(String uid) {
        touristId = uid;
        mode = true;
    }

    String getTouristId() {
        return new String(touristId);
    }

    boolean isLogin() {
        return mode;
    }
}
