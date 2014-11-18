class LogSummary {
    int count
    Set<String> devices = [] as LinkedHashSet
    Set<String> osVersions = [] as LinkedHashSet
    Set<String> messages = [] as LinkedHashSet
    LogInfo sample

    LogSummary(LogInfo sample) {
        this.sample = sample
        add(sample)
    }

    void add(LogInfo logInfo) {
        count++
        devices << logInfo.device
        osVersions << logInfo.osVersion

        def message = logInfo.parsedStackTrace?.exception?.message
        if (message) {
            messages << message
        }
    }

    @Override
    public String toString() {
        return count + ' times found \n' +
                'on ' + devices.join(', ') + '\n' +
                'with OS ' + osVersions.join(', ') + '\n' +
                (!messages.empty ? ('with messages ' + messages.join(', ') + '\n') : '') +
                sample.fullStacktrace
    }
}
