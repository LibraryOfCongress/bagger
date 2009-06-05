package gov.loc.repository.bagger.ui;

public class ActualTask implements Progress {

	private LongTask task;
    /**
     * The actual long running task.  This runs in a SwingWorker thread.
     */
	public ActualTask() {
	}

	public void setTask(LongTask task) {
		this.task = task;
	}

	public void execute() {
        //Fake a long task,
        //making a random amount of progress every second.
//    	System.out.println("ActualTask.execute");
/* */
    	while (!task.canceled && !task.done) {
//        	System.out.println("canceled: " + task.canceled + ", done: " + task.done);
            try {
                Thread.sleep(1000); //sleep for a second
                task.current += Math.random() * 100; //make some progress
//                System.out.println("ActualTask.current: " + task.current);
                if (task.current >= task.lengthOfTask) {
                    task.done = true;
                    task.current = task.lengthOfTask;
                }
                task.statMessage = "Completed " + task.current +
                              " out of " + task.lengthOfTask + ".";
//            	System.out.println("ActualTask.statMessage: " + task.statMessage);
            } catch (InterruptedException e) {
//                System.out.println("ActualTask interrupted");
            }
        }
/* */
	}
}