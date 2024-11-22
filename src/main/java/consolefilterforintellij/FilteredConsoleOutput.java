package consolefilterforintellij;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.awt.*;

public class FilteredConsoleOutput implements ToolWindowFactory {

    private Editor filteredConsoleOutput;

    private Project project;

    @Override
    public void createToolWindowContent(
            @NotNull Project project,
            ToolWindow toolWindow
    ) {
        this.project = project;

        JPanel panel = new JPanel(new BorderLayout());
        JLabel currentFilter;

        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument("");
        filteredConsoleOutput = editorFactory.createEditor(document, project);
        // Make the editor read-only
        ((EditorEx) filteredConsoleOutput).setViewer(true);

        JPanel header = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Filter:", SwingConstants.LEFT);
        header.add(headerLabel, BorderLayout.WEST);

        JTextField filter = new JTextField();
        header.add(filter, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new BorderLayout());

        JButton applyFilter = new JButton("Apply");
        buttons.add(applyFilter, BorderLayout.WEST);

        JButton clearLog = new JButton("Clear Log");
        buttons.add(clearLog, BorderLayout.EAST);

        header.add(buttons, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        JPanel output = new JPanel(new BorderLayout());

        JPanel currentFilterHeader = new JPanel(new BorderLayout());
        currentFilter = new JLabel(ConsoleOutputStore.DEFAULT_FILTER, SwingConstants.LEFT);
        currentFilterHeader.add(new JLabel("Applied filter: ", SwingConstants.RIGHT), BorderLayout.WEST);
        currentFilterHeader.add(currentFilter, BorderLayout.EAST);

        output.add(currentFilterHeader, BorderLayout.NORTH);

        // Add the editor component
        output.add(filteredConsoleOutput.getComponent(), BorderLayout.CENTER);

        panel.add(output, BorderLayout.CENTER);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);
        ConsoleOutputStore consoleOutputStore = project.getService(ConsoleOutputStore.class);
        consoleOutputStore.setFilteredConsoleOutput(this);
        applyFilter.addActionListener(actionEvent -> {
            String newFilter = filter.getText();
            currentFilter.setText(newFilter);
            consoleOutputStore.setFilter(newFilter);
        });
        clearLog.addActionListener(actionEvent -> consoleOutputStore.clearLog());
    }

    public void log(final String newText) {
        if (filteredConsoleOutput != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                Document document = filteredConsoleOutput.getDocument();
                document.insertString(document.getTextLength(), newText);
            });
        }
    }

    public void clearLog() {
        if (filteredConsoleOutput != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                filteredConsoleOutput.getDocument().setText("");
            });
        }
    }
}
