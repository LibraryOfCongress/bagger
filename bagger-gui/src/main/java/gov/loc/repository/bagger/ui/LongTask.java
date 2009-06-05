package gov.loc.repository.bagger.ui;

import java.text.MessageFormat;

import gov.loc.repository.bagit.CancelIndicator;
import gov.loc.repository.bagit.ProgressListener;

public class LongTask implements CancelIndicator, ProgressListener {
    public int lengthOfTask;
    public int current = 0;
    public boolean done = false;
    public boolean canceled = false;
    public String statMessage;
    public Progress progress;

    public LongTask() {
        //Compute length of task...
        //In a real program, this would figure out
        //the number of bytes to read or whatever.
    	lengthOfTask = 1000;
    }

    public void setProgress(Progress progress) {
    	this.progress = progress;
    }

    /**
     * Called from ProgressBarDemo to start the task.
     */
    public void go() {
    	final SwingWorker worker = new SwingWorker(this) {
            public Object construct() {
                current = 0;
                done = false;
                canceled = false;
                statMessage = null;
                longTask.progress.execute();
                return new Object();
            }
        };
        worker.start();
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    public int getLengthOfTask() {
        return lengthOfTask;
    }


    public void setLengthOfTask(int lengthOfTask) {
        this.lengthOfTask = lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    public int getCurrent() {
        return current;
    }

    public void stop() {
        canceled = true;
        statMessage = null;
    }

    /**
     * Called from ProgressBarDemo to find out if the task has completed.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Returns the most recent status message, or null
     * if there is no current status message.
     */
    public String getMessage() {
        return statMessage;
    }

	@Override
	public boolean performCancel() {
		return canceled;
	}

	@Override
	public void reportProgress(String activity, String item, int count,	int total) {
		System.out.println(MessageFormat.format("{0} {1} ({2} of {3})", activity, item, count, total));
	}
}
