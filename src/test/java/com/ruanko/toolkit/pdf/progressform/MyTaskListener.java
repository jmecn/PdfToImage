package com.ruanko.toolkit.pdf.progressform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

// 具体监听代码
public class MyTaskListener implements ChangeListener<Integer> {
	MyTask task;
	VBox console;

    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {

        if (task.getStatus() == 1) {

            // 获取xml操作对象，读取默认数值

        } else {

            String exception = task.getExceptions();

            if (exception != null && !exception.equals("")) {

                Text text = new Text();
                text.setText(exception);
                text.setFill(Color.RED);
                console.getChildren().add(text);
            }
        }
    }
}