package pcl.lc.api;

/**
 * Represents the states which the Stargate may be in.
 * 
 * @author AfterLifeLochie
 * 
 */
public enum EnumStargateState {
	/** Stargate is idle. */
	Idle,
	/** Stargate is dialling to a chevron. */
	Dialling,
	/** Stargate has paused after dialling a chevron. */
	InterDialling,
	/** Stargate has formed unstable wormhole (transient effect). */
	Transient,
	/** Stargate is connected and stable. */
	Connected,
	/** Stargate is closing wormhole connection. */
	Disconnecting;

	public static EnumStargateState fromOrdinal(int ordinal) {
		return EnumStargateState.values()[ordinal];
	}

}
