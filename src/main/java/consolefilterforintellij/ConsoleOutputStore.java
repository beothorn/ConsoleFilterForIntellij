package consolefilterforintellij;

import com.intellij.openapi.components.Service;

import java.util.regex.Pattern;

@Service
public final class ConsoleOutputStore {

    StringBuilder sb = new StringBuilder();

    FilteredConsoleOutput filteredConsoleOutput;

    private Pattern filterPattern = Pattern.compile(".*");

    public void append(String s) {
        sb.append(s);
        if (filterPattern.matcher(s).find()) {
            filteredConsoleOutput.log(s);
        }
    }

    public void setFilteredConsoleOutput(FilteredConsoleOutput newFilteredConsoleOutput) {
        filteredConsoleOutput = newFilteredConsoleOutput;
    }

    public void setFilter(String filter) {
        filterPattern = Pattern.compile(filter);
    }

    public void clearLog() {
        sb = new StringBuilder();
        filteredConsoleOutput.clearLog();
    }

}
