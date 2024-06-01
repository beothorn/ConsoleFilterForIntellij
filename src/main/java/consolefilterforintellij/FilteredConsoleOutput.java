package consolefilterforintellij;

import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class FilteredConsoleOutput implements ToolWindowFactory, Consumer<String> {

    private JTextArea filteredConsoleOutput;

    @Override
    public void createToolWindowContent(
        @NotNull Project project,
        ToolWindow toolWindow
    ) {
        JPanel panel = new JPanel(new BorderLayout());
        filteredConsoleOutput = new JTextArea();
        filteredConsoleOutput.setEditable(false);

        JPanel header = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Filter:", JLabel.LEFT);
        header.add(headerLabel, BorderLayout.WEST);

        JTextField filter = new JTextField();
        header.add(filter, BorderLayout.CENTER);

        JButton applyFilter = new JButton("Apply");
        header.add(applyFilter, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        JBScrollPane scrollPane = new JBScrollPane(filteredConsoleOutput);
        scrollPane.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);
        ConsoleOutputStore consoleOutputStore = project.getService(ConsoleOutputStore.class);
        consoleOutputStore.addListener(this);
        applyFilter.addActionListener(actionEvent -> consoleOutputStore.setFilter(filter.getText()));
    }

    @Override
    public void accept(final String newText) {
        if (filteredConsoleOutput != null) {
            filteredConsoleOutput.append("\n");
            filteredConsoleOutput.append(newText);
        }
    }
}
