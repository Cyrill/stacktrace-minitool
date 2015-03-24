import groovy.io.FileType
import java.util.zip.ZipInputStream

def sourceDir = '../archives'
def destDir = '../out/logs'
def outputFile = '../out/result'
def appVersion = ['68.8.1']

def archives = []
new File(sourceDir).eachFileRecurse(FileType.FILES) { file ->
    if (file.name.endsWith('zip')) {
        archives << file
    }
}


new AntBuilder().delete(dir: destDir)
new File(destDir).mkdir()
int index = 0
archives.each { archive ->
    def result = new ZipInputStream(new FileInputStream(archive))
    result.withStream {
        def entry
        while (entry = result.nextEntry) {
            if (!entry.isDirectory()) {
                new FileOutputStream("${destDir + File.separator}report${index}.log").withStream {
                    int len = 0;
                    byte[] buffer = new byte[4096]
                    while ((len = result.read(buffer)) > 0) {
                        it.write(buffer, 0, len);
                    }
                }
                index++
            }
        }
    }
}

def logs = []
new File(destDir).eachFile { file ->
    try {
        LogInfo info = new LogInfo(file.text)
        if (appVersion.empty || appVersion.contains(info.appVersion)) {
            logs << info
        }
    } catch (e) {
        e.printStackTrace()
    }

}
def logSummaries = [:]
logs.each { LogInfo logInfo ->
    LogSummary summary = logSummaries[logInfo.comparableCusedBy]
    if (summary) {
        summary.add(logInfo)
    } else {
        logSummaries[logInfo.comparableCusedBy] = new LogSummary(logInfo)
    }
}

new FileOutputStream(outputFile).withStream { stream ->
    stream << "Total: ${logSummaries.size()} different stacktraces, ${logSummaries.entrySet().sum {it.value.count}} reports\n"
    stream << Collections.nCopies(140, '=').join()
    stream << '\n\n\n'
    logSummaries.entrySet().sort { it.value.count }.reverse().each { entry ->
        stream << entry.value
        stream << '\n'
        stream << Collections.nCopies(140, '-').join()
        stream << '\n'
    }

}





