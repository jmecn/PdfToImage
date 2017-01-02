package com.ruanko.toolkit.pdf.progressform;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

// 实现监听类
public class ComboBoxMouseEvent implements EventHandler<MouseEvent> {

    public void handle(MouseEvent mouseEvent) {

        MouseButton button = mouseEvent.getButton();

        if (button == MouseButton.SECONDARY) {

            MyTask task = new MyTask(null, null, null);
            task.valueProperty().addListener(new MyTaskListener());

            ProgressFrom progressFrom = new ProgressFrom(task, null);
            progressFrom.activateProgressBar();

        }
    }
}