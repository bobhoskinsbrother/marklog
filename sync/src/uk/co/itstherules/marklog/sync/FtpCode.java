package uk.co.itstherules.marklog.sync;

public enum FtpCode {
    RESTART_MARKER(110),
    SERVICE_NOT_READY(120),
    DATA_CONNECTION_ALREADY_OPEN(125),
    FILE_STATUS_OK(150),
    COMMAND_OK(200),
    COMMAND_IS_SUPERFLUOUS(202),
    SYSTEM_STATUS(211),
    DIRECTORY_STATUS(212),
    FILE_STATUS(213),
    HELP_MESSAGE(214),
    NAME_SYSTEM_TYPE(215),
    SERVICE_READY(220),
    SERVICE_CLOSING_CONTROL_CONNECTION(221),
    DATA_CONNECTION_OPEN(225),
    CLOSING_DATA_CONNECTION(226),
    ENTERING_PASSIVE_MODE(227),
    ENTERING_EPSV_MODE(229),
    USER_LOGGED_IN(230),
    FILE_ACTION_OK(250),
    PATHNAME_CREATED(257),
    NEED_PASSWORD(331),
    NEED_ACCOUNT(332),
    FILE_ACTION_PENDING(350),
    SERVICE_NOT_AVAILABLE(421),
    CANNOT_OPEN_DATA_CONNECTION(425),
    TRANSFER_ABORTED(426),
    FILE_ACTION_NOT_TAKEN(450),
    ACTION_ABORTED(451),
    INSUFFICIENT_STORAGE(452),
    UNRECOGNIZED_COMMAND(500),
    SYNTAX_ERROR_IN_ARGUMENTS(501),
    COMMAND_NOT_IMPLEMENTED(502),
    BAD_COMMAND_SEQUENCE(503),
    COMMAND_NOT_IMPLEMENTED_FOR_PARAMETER(504),
    NOT_LOGGED_IN(530),
    NEED_ACCOUNT_FOR_STORING_FILES(532),
    FILE_UNAVAILABLE(550),
    PAGE_TYPE_UNKNOWN(551),
    STORAGE_ALLOCATION_EXCEEDED(552),
    FILE_NAME_NOT_ALLOWED(553),
    SECURITY_DATA_EXCHANGE_COMPLETE(234),
    SECURITY_DATA_EXCHANGE_SUCCESSFULLY(235),
    SECURITY_MECHANISM_IS_OK(334),
    SECURITY_DATA_IS_ACCEPTABLE(335),
    UNAVAILABLE_RESOURCE(431),
    BAD_TLS_NEGOTIATION_OR_DATA_ENCRYPTION_REQUIRED(522),
    DENIED_FOR_POLICY_REASONS(533),
    REQUEST_DENIED(534),
    FAILED_SECURITY_CHECK(535),
    REQUESTED_PROT_LEVEL_NOT_SUPPORTED(536);
    private final int code;

    private FtpCode(int code) { this.code = code; }

    public int getCode() { return code; }

    public static FtpCode codeFor(int code) {
        for (int i = 0; i < values().length; i++) {
            FtpCode ftpCode = values()[i];
            if (code == ftpCode.code) {
                return ftpCode;
            }
        }
        throw new IllegalArgumentException("Code " + code + " not found.");
    }
}