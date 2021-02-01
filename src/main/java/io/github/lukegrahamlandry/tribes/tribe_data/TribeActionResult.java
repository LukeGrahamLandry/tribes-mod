package io.github.lukegrahamlandry.tribes.tribe_data;

public enum TribeActionResult {
    SUCCESS,
    NAME_TAKEN,
    IN_TRIBE,
    LONG_NAME,
    CLIENT,
    INVALID_TRIBE,
    LOW_RANK,
    NOT_IN_TRIBE,
    BANNED;

    @Override
    public String toString() {
        switch (this){
            case NAME_TAKEN: return "FAILURE: name taken";
            case SUCCESS: return "SUCCESS";
            case IN_TRIBE: return "FAILURE: you are already in a tribe";
            case LONG_NAME: return "FAILURE: name too long";
            case CLIENT: return "And the lord came down from the heavens and said 'thou shall not create a tribe on the render thread'. This should never happen, DM the Dev.";
            case INVALID_TRIBE: return "FAILURE: that tribe does not exist";
            case LOW_RANK: return "FAILURE: your tribe rank is too low";
            case NOT_IN_TRIBE: return "FAILURE: you are not in a tribe";
            case BANNED: return "FAILURE: player has been banned";
        }
        return "ERROR: invalid TribeActionResult";
    }
}
