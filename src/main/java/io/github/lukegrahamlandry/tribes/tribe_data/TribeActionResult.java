package io.github.lukegrahamlandry.tribes.tribe_data;

public enum TribeActionResult {
    SUCCESS,
    NAME_TAKEN,
    FAIL,
    IN_TRIBE,
    LONG_NAME,
    CLIENT;

    @Override
    public String toString() {
        switch (this){
            case NAME_TAKEN: return "FAILURE: name taken";
            case SUCCESS: return "SUCCESS";
            case IN_TRIBE: return "FAILURE: you are already in a tribe";
            case LONG_NAME: return "FAILURE: name too long";
            case CLIENT: return "And the lord came down from the heavens and said 'thou shall not create a tribe on the render thread'. This should never happen, DM the Dev.";
            case FAIL: return "FAILURE";
        }
        return "ERROR: invalid TribeActionResult";
    }
}
