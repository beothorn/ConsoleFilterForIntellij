package consolefilterforintellij;

import com.intellij.openapi.components.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public final class ConsoleOutputStore {

    StringBuilder sb = new StringBuilder();

    List<Consumer<String>> outputListeners = new ArrayList<>();

    private Pattern filterPattern = Pattern.compile(".*");

    public void append(String s) {
        sb.append(s);
        if (filterPattern.matcher(s).find()) {
            outputListeners.forEach(l -> l.accept(s));
        }
    }

    public void addListener(Consumer<String> newConsumer) {
        outputListeners.add(newConsumer);
    }

    // Regex setter
    public void setFilter(String filter) {
        filterPattern = Pattern.compile(filter);
    }

}
