package io.github.lukegrahamlandry.tribes.tribe_data;

public enum TribeActionResult {
    SUCCESS,
    NAME_TAKEN,
    FAIL;

    @Override
    public String toString() {
        switch (this){
            case NAME_TAKEN: return "FAILURE: name taken";
            case SUCCESS: return "SUCCESS";
            case FAIL: return "FAILURE";
        }
        return "invalid TribeActionResult";
    }
}
