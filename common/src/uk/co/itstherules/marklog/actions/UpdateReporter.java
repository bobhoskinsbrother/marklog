package uk.co.itstherules.marklog.actions;

public interface UpdateReporter {

    void report(String... toReport);
    void error(String... toReport);
    void success(String... success);
}
