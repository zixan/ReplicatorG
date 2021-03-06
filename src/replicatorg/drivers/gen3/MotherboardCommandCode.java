package replicatorg.drivers.gen3;

/**
 * An enumeration of the available command codes for the three-axis CNC
 * stage.
 */
public enum MotherboardCommandCode {
	VERSION(0),
	INIT(1),
	GET_BUFFER_SIZE(2),
	CLEAR_BUFFER(3),
	GET_POSITION(4),
	GET_RANGE(5),
	SET_RANGE(6),
	ABORT(7),
	PAUSE(8),
	PROBE(9),
	TOOL_QUERY(10),
	IS_FINISHED(11),
	READ_EEPROM(12),
	WRITE_EEPROM(13),
	
	CAPTURE_TO_FILE(14),
	END_CAPTURE(15),
	PLAYBACK_CAPTURE(16),
	
	RESET(17),

	NEXT_FILENAME(18),
	
	// QUEUE_POINT_INC(128) obsolete
	QUEUE_POINT_ABS(129),
	SET_POSITION(130),
	FIND_AXES_MINIMUM(131),
	FIND_AXES_MAXIMUM(132),
	DELAY(133),
	CHANGE_TOOL(134),
	WAIT_FOR_TOOL(135),
	TOOL_COMMAND(136),
	ENABLE_AXES(137);
	
	private int code;
	private MotherboardCommandCode(int code) {
		this.code = code;
	}
	int getCode() { return code; }
}