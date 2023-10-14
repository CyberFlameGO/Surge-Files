package dev.iiahmed.disguise;

/**
 * The response returned when undisgusing
 */
public enum UndisguiseResponse {

    /**
     * Returns when undisguising an already undisguied player
     */
    FAIL_ALREADY_UNDISGUISED,
    /**
     * Returns when reflections fail to get the player's GameProfile
     * THIS WILL NEVER HAPPEN IN UNDISGUISE UNLESS PLAYER WENT OFFLINE IN A BAD TIMING
     */
    FAIL_PROFILE_NOT_FOUND,
    /**
     * Returns when reflections fail to change the player's GameProfile's name
     * THIS WILL NEVER HAPPEN IN UNDISGUISE EVER
     */
    FAIL_NAME_CHANGE_EXCEPTION,
    /**
     * Returns when the undisguise request succeeds
     */
    SUCCESS

}
