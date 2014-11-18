import com.jmolly.stacktraceparser.NStackTrace
import com.jmolly.stacktraceparser.NTrace
import com.jmolly.stacktraceparser.StackTraceParser

public class LogInfo {
    boolean tos
    String device
    String appVersion
    String osVersion
    String fullStacktrace
    String comparableCusedBy
    NTrace parsedStackTrace

    LogInfo(String fullStacktrace) {
        parse(fullStacktrace)
    }

    private void parse(String stacktrace) {
        List<String> lines = stacktrace.split('\n')

        device = lines[0].trim()
        osVersion = lines[1].trim()

        def appLine = lines[2].trim()
        tos = appLine.startsWith('appversion=tos_mob_android')
        appVersion = appLine.substring(appLine.indexOf('.') + 1)
        fullStacktrace = lines[4..-1].join('\n')

        if (fullStacktrace.contains('java.lang.OutOfMemoryError')) {
            comparableCusedBy = 'java.lang.OutOfMemoryError'
        } else {
            NStackTrace result = StackTraceParser.parse(fullStacktrace)
            NTrace trace = result.getTrace()
            //get first cause
            if (trace.getNested() != null) {
                trace = trace.getNested()
            }
            parsedStackTrace = trace
            comparableCusedBy = trace.getFrames()[0]
        }
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof LogInfo)) return false

        LogInfo logInfo = (LogInfo) o

        if (tos != logInfo.tos) return false
        if (comparableCusedBy != logInfo.comparableCusedBy) return false

        return true
    }

    int hashCode() {
        int result
        result = (tos ? 1 : 0)
        result = 31 * result + comparableCusedBy.hashCode()
        return result
    }


    @Override
    public String toString() {
        return "LogInfo{" +
                "tos=" + tos +
                ", device='" + device + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", fullStacktrace='" + fullStacktrace + '\'' +
                ", comparableCusedBy='" + comparableCusedBy + '\'' +
                ", parsedStackTrace=" + parsedStackTrace +
                '}';
    }
}
