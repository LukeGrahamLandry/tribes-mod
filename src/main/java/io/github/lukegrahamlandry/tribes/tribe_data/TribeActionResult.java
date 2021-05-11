package io.github.lukegrahamlandry.tribes.tribe_data;

public enum TribeActionResult {
    SUCCESS,
    NAME_TAKEN,
    IN_TRIBE,
    LONG_NAME,
    CLIENT,
    INVALID_TRIBE,
    LOW_RANK,
    YOU_NOT_IN_TRIBE,
    BANNED,
    RANK_DOESNT_EXIST,
    THEY_NOT_IN_TRIBE,
    SAME_TRIBE,
    CONFIG,
    ALREADY_CLAIMED,
    HAVE_HEMI,
    INVALID_ARG,
    WEAK_TRIBE;

    @Override
    public String toString() {
        switch (this){
            case NAME_TAKEN: return "FAILURE: name taken";
            case SUCCESS: return "SUCCESS";
            case IN_TRIBE: return "FAILURE: you are already in a tribe";
            case LONG_NAME: return "FAILURE: name too long";
            case CLIENT: return "And the lord came down from the heavens and said 'thou shall not create a tribe on the render thread'. This should never happen, DM the Dev.";
            case INVALID_TRIBE: return "FAILURE: that tribe does not exist";
            case LOW_RANK: return "FAILURE: your rank in your tribe is too low";
            case YOU_NOT_IN_TRIBE: return "FAILURE: you are not in a tribe";
            case THEY_NOT_IN_TRIBE: return "FAILURE: that player is not in your tribe";
            case BANNED: return "FAILURE: player has been banned";
            case RANK_DOESNT_EXIST: return "FAILURE: there is no rank greater than leader";
            case SAME_TRIBE: return "FAILURE: that's your own tribe";
            case CONFIG: return "FAILURE: that action is not allowed by the current config (ie hit tribes/members limit)";
            case ALREADY_CLAIMED: return "FAILURE: that area has been claimed";
            case HAVE_HEMI: return "FAILURE: you have already claimed a hemisphere";
            case INVALID_ARG: return "FAILURE: invalid argument";
            case WEAK_TRIBE: return "FAILURE: your tribe has too few members";
        }
        return "ERROR: invalid TribeActionResult";
    }
}
