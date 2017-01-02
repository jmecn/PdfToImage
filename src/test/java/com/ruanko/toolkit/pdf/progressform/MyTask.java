package com.ruanko.toolkit.pdf.progressform;

import java.util.List;

import javax.xml.ws.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;

public class MyTask extends Task<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(MyTask.class);

    private String serverName;
    private String userName;
    private String passWord;

    private List<String> databaseList;
    private String exception;
    private int status;

    public int getStatus(){
        return status;
    }
    public String getExceptions(){
        return exception;
    }

    public List<String> getDatabaseList(){
        return databaseList;
    }

    public MyTask(String serverName, String userName, String passWord){
        this.serverName = serverName;
        this.userName = userName;
        this.passWord = passWord;
    }

    protected Integer call() throws Exception {

        status = 1;

        if (exception != null && !exception.equals("")) {

            status = 0;
            logger.debug(exception);
        }

        return 1;
    }
}