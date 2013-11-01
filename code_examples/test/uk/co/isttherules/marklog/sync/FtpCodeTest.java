package uk.co.isttherules.marklog.sync;

import org.apache.commons.net.ftp.FTPReply;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sync.FtpCode;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static sync.FtpCode.*;

@RunWith(Parameterized.class)
public class FtpCodeTest {

    private final int number;
    private final FtpCode asExpected;

    public FtpCodeTest(int number, FtpCode asExpected) {
        this.number = number;
        this.asExpected = asExpected;
    }

    @Test
    public void canFindCodes() {
        Assert.assertThat(FtpCode.codeFor(number), is(asExpected));
    }

    @Parameterized.Parameters
    public static List<Object[]> parameters() {
        final Object[][] objects = {
                {FTPReply.RESTART_MARKER, RESTART_MARKER},
                {FTPReply.SERVICE_NOT_READY, SERVICE_NOT_READY},
                {FTPReply.DATA_CONNECTION_ALREADY_OPEN, DATA_CONNECTION_ALREADY_OPEN},
                {FTPReply.FILE_STATUS_OK, FILE_STATUS_OK},
                {FTPReply.COMMAND_OK, COMMAND_OK},
                {FTPReply.COMMAND_IS_SUPERFLUOUS, COMMAND_IS_SUPERFLUOUS},
                {FTPReply.SYSTEM_STATUS, SYSTEM_STATUS},
                {FTPReply.DIRECTORY_STATUS, DIRECTORY_STATUS},
                {FTPReply.FILE_STATUS, FILE_STATUS},
                {FTPReply.HELP_MESSAGE, HELP_MESSAGE},
                {FTPReply.NAME_SYSTEM_TYPE, NAME_SYSTEM_TYPE},
                {FTPReply.SERVICE_READY, SERVICE_READY},
                {FTPReply.SERVICE_CLOSING_CONTROL_CONNECTION, SERVICE_CLOSING_CONTROL_CONNECTION},
                {FTPReply.DATA_CONNECTION_OPEN, DATA_CONNECTION_OPEN},
                {FTPReply.CLOSING_DATA_CONNECTION, CLOSING_DATA_CONNECTION},
                {FTPReply.ENTERING_PASSIVE_MODE, ENTERING_PASSIVE_MODE},
                {FTPReply.ENTERING_EPSV_MODE, ENTERING_EPSV_MODE},
                {FTPReply.USER_LOGGED_IN, USER_LOGGED_IN},
                {FTPReply.FILE_ACTION_OK, FILE_ACTION_OK},
                {FTPReply.PATHNAME_CREATED, PATHNAME_CREATED},
                {FTPReply.NEED_PASSWORD, NEED_PASSWORD},
                {FTPReply.NEED_ACCOUNT, NEED_ACCOUNT},
                {FTPReply.FILE_ACTION_PENDING, FILE_ACTION_PENDING},
                {FTPReply.SERVICE_NOT_AVAILABLE, SERVICE_NOT_AVAILABLE},
                {FTPReply.CANNOT_OPEN_DATA_CONNECTION, CANNOT_OPEN_DATA_CONNECTION},
                {FTPReply.TRANSFER_ABORTED, TRANSFER_ABORTED},
                {FTPReply.FILE_ACTION_NOT_TAKEN, FILE_ACTION_NOT_TAKEN},
                {FTPReply.ACTION_ABORTED, ACTION_ABORTED},
                {FTPReply.INSUFFICIENT_STORAGE, INSUFFICIENT_STORAGE},
                {FTPReply.UNRECOGNIZED_COMMAND, UNRECOGNIZED_COMMAND},
                {FTPReply.SYNTAX_ERROR_IN_ARGUMENTS, SYNTAX_ERROR_IN_ARGUMENTS},
                {FTPReply.COMMAND_NOT_IMPLEMENTED, COMMAND_NOT_IMPLEMENTED},
                {FTPReply.BAD_COMMAND_SEQUENCE, BAD_COMMAND_SEQUENCE},
                {FTPReply.COMMAND_NOT_IMPLEMENTED_FOR_PARAMETER, COMMAND_NOT_IMPLEMENTED_FOR_PARAMETER},
                {FTPReply.NOT_LOGGED_IN, NOT_LOGGED_IN},
                {FTPReply.NEED_ACCOUNT_FOR_STORING_FILES, NEED_ACCOUNT_FOR_STORING_FILES},
                {FTPReply.FILE_UNAVAILABLE, FILE_UNAVAILABLE},
                {FTPReply.PAGE_TYPE_UNKNOWN, PAGE_TYPE_UNKNOWN},
                {FTPReply.STORAGE_ALLOCATION_EXCEEDED, STORAGE_ALLOCATION_EXCEEDED},
                {FTPReply.FILE_NAME_NOT_ALLOWED, FILE_NAME_NOT_ALLOWED},
                {FTPReply.SECURITY_DATA_EXCHANGE_COMPLETE, SECURITY_DATA_EXCHANGE_COMPLETE},
                {FTPReply.SECURITY_DATA_EXCHANGE_SUCCESSFULLY, SECURITY_DATA_EXCHANGE_SUCCESSFULLY},
                {FTPReply.SECURITY_MECHANISM_IS_OK, SECURITY_MECHANISM_IS_OK},
                {FTPReply.SECURITY_DATA_IS_ACCEPTABLE, SECURITY_DATA_IS_ACCEPTABLE},
                {FTPReply.UNAVAILABLE_RESOURCE, UNAVAILABLE_RESOURCE},
                {FTPReply.BAD_TLS_NEGOTIATION_OR_DATA_ENCRYPTION_REQUIRED, BAD_TLS_NEGOTIATION_OR_DATA_ENCRYPTION_REQUIRED},
                {FTPReply.DENIED_FOR_POLICY_REASONS, DENIED_FOR_POLICY_REASONS},
                {FTPReply.REQUEST_DENIED, REQUEST_DENIED},
                {FTPReply.FAILED_SECURITY_CHECK, FAILED_SECURITY_CHECK},
                {FTPReply.REQUESTED_PROT_LEVEL_NOT_SUPPORTED, REQUESTED_PROT_LEVEL_NOT_SUPPORTED}};

        return Arrays.<Object[]>asList(objects);
    }

}
