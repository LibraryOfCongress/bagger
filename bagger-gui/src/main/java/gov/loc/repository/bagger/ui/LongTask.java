package gov.loc.repository.bagger.ui;

import java.text.MessageFormat;

import javax.swing.ProgressMonitor;

import gov.loc.repository.bagit.ProgressListener;

public class LongTask implements ProgressListener {
    public Long lengthOfTask;
    public Long current = 0L;
    public boolean done = false;
    public boolean canceled = false;
    public String statMessage;
    public Progress progress;
    private ProgressMonitor progressMonitor;

    public LongTask() {
        //Compute length of task...
        //In a real program, this would figure out
        //the number of bytes to read or whatever.
    	lengthOfTask = 1L;
    }
    
    public void setMonitor(ProgressMonitor monitor) {
    	this.progressMonitor = monitor;
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
                current = 0L;
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
    public Long getLengthOfTask() {
        return lengthOfTask;
    }


    public void setLengthOfTask(Long lengthOfTask) {
        this.lengthOfTask = lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    public Long getCurrent() {
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
/*
	@Override
	public boolean performCancel() {
		return canceled;
	}
*/
	@Override
	public void reportProgress(String activity, Object item, Long count, Long total) {
		current = count;
		lengthOfTask = total;
		String message = MessageFormat.format("{0} {1} ({2} of {3})", activity, item, count, total);
		statMessage = message;
		this.progressMonitor.setNote(message);
		this.progressMonitor.setMaximum(total.intValue());
	}
}
