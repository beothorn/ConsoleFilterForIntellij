package consolefilterforintellij;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

public class StartupActivity implements com.intellij.openapi.startup.StartupActivity {

    @Override
    public void runActivity(@NotNull final Project project) {
        project
                .getMessageBus()
                .connect(project)
                .subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
                    @Override
                    public void processStarted(
                            @NotNull String executorId,
                            @NotNull ExecutionEnvironment env,
                            @NotNull ProcessHandler handler
                    ) {
                        handler.addProcessListener(new ProcessAdapter() {
                            @Override
                            public void onTextAvailable(
                                    @NotNull ProcessEvent event,
                                    @NotNull Key outputType
                            ) {
                                ConsoleOutputStore consoleOutputStore = project.getService(ConsoleOutputStore.class);
                                if (consoleOutputStore != null) {
                                    String text = event.getText();
                                    if (!text.isEmpty()) consoleOutputStore.onOutput(text);
                                }
                            }
                        });
                    }
                });
    }
}
