class LogSummary {
    int count
    Set<String> devices = [] as TreeSet
    Set<String> osVersions = [] as TreeSet
    Set<String> messages = [] as LinkedHashSet
    Set<String> appVersions = [] as TreeSet
    LogInfo sample

    LogSummary(LogInfo sample) {
        this.sample = sample
        add(sample)
    }

    void add(LogInfo logInfo) {
        count++
        devices << logInfo.device
        osVersions << logInfo.osVersion
        appVersions << logInfo.appVersion

        def message = logInfo.parsedStackTrace?.exception?.message
        if (message) {
            messages << message
        }
    }

    @Override
    public String toString() {
        return count + ' times found \n' +
                'on ' + devices.join(', ') + '\n' +
                'with OS ' + osVersions.descendingSet().join(', ') + '\n' +
                (appVersions.size() > 1 ? ('with App ' + appVersions.join(', ') + '\n') : '') +
                (!messages.empty ? ('with messages ' + messages.join(', ') + '\n') : '') +
                sample.fullStacktrace
    }
}
