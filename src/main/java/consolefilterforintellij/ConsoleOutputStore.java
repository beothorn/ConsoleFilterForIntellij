package consolefilterforintellij;

import com.intellij.openapi.components.Service;
import com.jgoodies.common.base.Strings;
import java.util.regex.Pattern;

@Service
public final class ConsoleOutputStore {

    public static final String DEFAULT_FILTER = "";

    FilteredConsoleOutput filteredConsoleOutput;

    private Pattern filterPattern;

    public void onOutput(String s) {
        if (filterPattern == null) return;
        if (filterPattern.matcher(s).find()) {
            filteredConsoleOutput.log(s);
        }
    }

    public void setFilteredConsoleOutput(FilteredConsoleOutput newFilteredConsoleOutput) {
        filteredConsoleOutput = newFilteredConsoleOutput;
    }

    public void setFilter(String filter) {
        if (Strings.isBlank(filter)) {
            filterPattern = null;
        } else {
            filterPattern = Pattern.compile(filter);
        }
    }

    public void clearLog() {
        filteredConsoleOutput.clearLog();
    }

}
