
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.domain.BaggerValidationRulesSource;
import gov.loc.repository.bagger.ui.handlers.*;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Cancellable;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.writer.Writer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.JFrame;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.progress.BusyIndicator;
import org.springframework.util.Assert;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BagView extends AbstractView implements ApplicationListener {
	private static final Log log = LogFactory.getLog(BagView.class);
    public final static int ONE_SECOND = 1000;
	private int DEFAULT_WIDTH = 1024;
	private int DEFAULT_HEIGHT = 768;

	public ProgressMonitor progressMonitor;
    public LongTask task;
    public Cancellable longRunningProcess = null;
    private JTextArea taskOutput;
    private Timer timer;

    public Writer bagWriter = null;
	private Bagger bagger;
    private DefaultBag bag;
    public BaggerValidationRulesSource baggerRules;
    public BagProject bagProject = new BagProject(this);
    public BagTree bagPayloadTree;
	public BagTree bagTagFileTree;

	public int bagCount = 0;
    private File bagRootPath;
	public String userHomeDir;

	public CompositePane compositePane;
	public TagManifestPane tagManifestPane;
	public InfoFormsPane infoInputPane;
	public BagTreePanel bagPayloadTreePanel;
	public BagTreePanel bagTagFileTreePanel;

	private JPanel bagButtonPanel;
	private JPanel bagTagButtonPanel;
	private JPanel bagPanel;
	private JPanel topButtonPanel;
	private JButton openButton;
	private JButton createSkeletonButton;
	private JButton addDataButton;
	private JButton removeDataButton;
	private JButton saveButton;
	private JButton saveAsButton;
	private JButton completeButton;
	private JButton validateButton;
	private JButton closeButton;
	private JButton showTagButton;
	private JButton addTagFileButton;
	private JButton removeTagFileButton;

    public StartNewBagHandler startNewBagHandler;
	private StartExecutor startExecutor = new StartExecutor(this);
	public OpenBagHandler openBagHandler;
    private OpenExecutor openExecutor = new OpenExecutor(this);
    public CreateBagInPlaceHandler createBagInPlaceHandler;
    private CreateBagInPlaceExecutor createBagInPlaceExecutor = new CreateBagInPlaceExecutor(this);
    public SaveBagHandler saveBagHandler;
    private SaveBagExecutor saveBagExecutor = new SaveBagExecutor(this);
    public SaveBagAsHandler saveBagAsHandler;
    private SaveBagAsExecutor saveBagAsExecutor = new SaveBagAsExecutor(this);
    public ValidateBagHandler validateBagHandler;
    private ValidateExecutor validateExecutor = new ValidateExecutor(this);
    public CompleteBagHandler completeBagHandler;
    private CompleteExecutor completeExecutor = new CompleteExecutor(this);
    public ClearBagHandler clearBagHandler;
    private ClearBagExecutor clearExecutor = new ClearBagExecutor(this);
    public AddDataHandler addDataHandler;
    private AddDataExecutor addDataExecutor = new AddDataExecutor(this);
    public RemoveDataHandler removeDataHandler;
    public RemoveTagFileHandler removeTagFileHandler;
    public AddTagFileHandler addTagFileHandler;

    public Color errorColor = new Color(255, 128, 128);
	public Color infoColor = new Color(120, 120, 120);

    public void setBagger(Bagger bagger) {
        Assert.notNull(bagger, "The bagger property is required");
        this.bagger = bagger;
    }
    
    public Bagger getBagger() {
    	return this.bagger;
    }
    
    public void setBag(DefaultBag baggerBag) {
        this.bag = baggerBag;
        this.bag.getInfo().setBag(baggerBag);
    }

    public DefaultBag getBag() {
        return this.bag;
    }
    
    public void setBagRootPath(File f) {
    	this.bagRootPath = f;
    }
    
    public File getBagRootPath() {
    	return this.bagRootPath;
    }
    
    public Dimension getMinimumSize() {
		return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public Dimension getPreferredSize() {
		return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public void display(String s) {
		//log.debug(s);
		log.info(s);
	}

	public ImageIcon getPropertyImage(String name) {
		ImageSource source = this.getImageSource();
        Image image = source.getImage(name);
        ImageIcon imageIcon = new ImageIcon(image);
        return imageIcon;
	}

	public String getPropertyMessage(String property) {
    	return getMessage(property);
    }

    public void setBagPayloadTree(BagTree bagTree) {
        this.bagPayloadTree = bagTree;
    }

    public BagTree getBagPayloadTree() {
        return this.bagPayloadTree;
    }
    
    public void setBagTagFileTree(BagTree bagTree) {
    	this.bagTagFileTree = bagTree;
    }
    
    public BagTree getBagTagFileTree() {
    	return this.bagTagFileTree;
    }

    @Override
    // This populates the default view descriptor declared as the startingPageId
    // property in the richclient-application-context.xml file.
    protected JComponent createControl() {
    	ApplicationWindow window = Application.instance().getActiveWindow();
    	JFrame f = window.getControl();
    	f.setBackground(Color.red);
    	this.userHomeDir = System.getProperty("user.home");
        display("createControl - User Home Path: "+ userHomeDir);

        ApplicationServices services = this.getApplicationServices();
        Object rulesSource = services.getService(org.springframework.rules.RulesSource.class);
        baggerRules = (BaggerValidationRulesSource) rulesSource;
		
    	Color bgColor = new Color(20,20,100);
    	topButtonPanel = createTopButtonPanel();
    	topButtonPanel.setBackground(bgColor);

    	bagProject.initializeProfile();
    	updateCommands();

    	infoInputPane = new InfoFormsPane(this);
    	enableSettings(false);
        JPanel bagPanel = createBagPanel();

    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        buildConstraints(glbc, 0, 0, 1, 1, 50, 100, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        layout.setConstraints(bagPanel, glbc);

        buildConstraints(glbc, 1, 0, 1, 1, 50, 100, GridBagConstraints.BOTH, GridBagConstraints.NORTH);
        layout.setConstraints(infoInputPane, glbc);

        JPanel mainPanel = new JPanel(layout);
        mainPanel.add(bagPanel);
        mainPanel.add(infoInputPane);
        
    	JPanel bagViewPanel = new JPanel(new BorderLayout(2, 2));
        bagViewPanel.setBackground(bgColor);
    	bagViewPanel.add(topButtonPanel, BorderLayout.NORTH);
    	bagViewPanel.add(mainPanel, BorderLayout.CENTER);
        return bagViewPanel;
    }
    
    private JPanel createTopButtonPanel() {
        Color fgColor = new Color(250, 250, 250);
        Color bgColor = new Color(50, 50, 150);

    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    	buttonPanel.setBackground(bgColor);

    	JButton createButton = new JButton(getPropertyMessage("bag.button.create"));
    	startNewBagHandler = new StartNewBagHandler(this);
    	createButton.addActionListener(startNewBagHandler);
    	createButton.setOpaque(true);
    	createButton.setBackground(bgColor);
    	createButton.setForeground(fgColor);
    	createButton.setToolTipText(getPropertyMessage("bag.button.create.help"));
    	buttonPanel.add(createButton);

    	openButton = new JButton(getPropertyMessage("bag.button.open"));
    	openBagHandler = new OpenBagHandler(this);
    	openButton.addActionListener(openBagHandler);
    	openButton.setEnabled(true);
    	openButton.setOpaque(true);
    	openButton.setBackground(bgColor);
    	openButton.setForeground(fgColor);
    	openButton.setToolTipText(getPropertyMessage("bag.button.open.help"));
    	buttonPanel.add(openButton);

    	createSkeletonButton = new JButton(getPropertyMessage("bag.button.createskeleton"));
    	createBagInPlaceHandler = new CreateBagInPlaceHandler(this);
    	createSkeletonButton.addActionListener(createBagInPlaceHandler);
    	createSkeletonButton.setEnabled(true);
    	createSkeletonButton.setOpaque(true);
    	createSkeletonButton.setBackground(bgColor);
    	createSkeletonButton.setForeground(fgColor);
    	createSkeletonButton.setToolTipText(getPropertyMessage("bag.button.createskeleton.help"));
    	buttonPanel.add(createSkeletonButton);

        saveButton = new JButton(getPropertyMessage("bag.button.save"));
        saveBagHandler = new SaveBagHandler(this);
        saveButton.addActionListener(saveBagHandler);
        saveButton.setEnabled(false);
        saveButton.setOpaque(true);
        saveButton.setBackground(bgColor);
        saveButton.setForeground(fgColor);
        saveButton.setToolTipText(getPropertyMessage("bag.button.save.help"));
        buttonPanel.add(saveButton);

    	saveAsButton = new JButton(getPropertyMessage("bag.button.saveas"));
    	saveBagAsHandler = new SaveBagAsHandler(this);
    	saveAsButton.addActionListener(saveBagAsHandler);
        saveAsButton.setEnabled(false);
        saveAsButton.setOpaque(true);
        saveAsButton.setBackground(bgColor);
        saveAsButton.setForeground(fgColor);
        saveAsButton.setToolTipText(getPropertyMessage("bag.button.saveas.help"));
        buttonPanel.add(saveAsButton);

        completeButton = new JButton(getPropertyMessage("bag.button.complete"));
    	completeBagHandler = new CompleteBagHandler(this);
        completeButton.addActionListener(completeBagHandler);
        completeButton.setEnabled(false);
        completeButton.setOpaque(true);
        completeButton.setBackground(bgColor);
        completeButton.setForeground(fgColor);
        completeButton.setToolTipText(getPropertyMessage("bag.button.complete.help"));
        buttonPanel.add(completeButton);
        
        validateButton = new JButton(getPropertyMessage("bag.button.validate"));
    	validateBagHandler = new ValidateBagHandler(this);
        validateButton.addActionListener(validateBagHandler);
        validateButton.setEnabled(false);
        validateButton.setOpaque(true);
    	validateButton.setBackground(bgColor);
    	validateButton.setForeground(fgColor);
    	validateButton.setToolTipText(getPropertyMessage("bag.button.validate.help"));
        buttonPanel.add(validateButton);
        
        closeButton = new JButton(getPropertyMessage("bag.button.clear"));
    	clearBagHandler = new ClearBagHandler(this);
    	closeButton.addActionListener(clearBagHandler);
    	closeButton.setEnabled(false);
    	closeButton.setOpaque(true);
    	closeButton.setBackground(bgColor);
    	closeButton.setForeground(fgColor);
    	closeButton.setToolTipText(getPropertyMessage("bag.button.clear.help"));
        buttonPanel.add(closeButton);

        return buttonPanel;
    }
    
    private JPanel createBagPanel() {
    	EmptyBorder border = new EmptyBorder(2, 1, 5, 1);
    	bagButtonPanel = createBagButtonPanel();

    	bagTagButtonPanel = createBagTagButtonPanel();

    	bagPayloadTree = new BagTree(this, AbstractBagConstants.DATA_DIRECTORY, true);
    	bagPayloadTree.setEnabled(false);
    	bagPayloadTreePanel = new BagTreePanel(bagPayloadTree);
    	bagPayloadTreePanel.setEnabled(false);
    	bagPayloadTreePanel.setBorder(border);
    	bagPayloadTreePanel.setToolTipText(getPropertyMessage("bagTree.help"));

    	bagTagFileTree = new BagTree(this, getPropertyMessage("bag.label.noname"), false);
    	bagTagFileTree.setEnabled(false);
    	bagTagFileTreePanel = new BagTreePanel(bagTagFileTree);
    	bagTagFileTreePanel.setEnabled(false);
    	bagTagFileTreePanel.setBorder(border);
    	bagTagFileTreePanel.setToolTipText(getPropertyMessage("bagTree.help"));

    	tagManifestPane = new TagManifestPane(this);
    	tagManifestPane.setToolTipText(getPropertyMessage("compositePane.tab.help"));
    	
    	compositePane = new CompositePane(this, getInitialConsoleMsg());
    	compositePane.setToolTipText(getPropertyMessage("compositePane.tab.help"));

    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();
        int row = 0;
        int colspan = 1;
        bagPanel = new JPanel(layout);

        buildConstraints(glbc, 0, row, colspan, 1, 1, 10, GridBagConstraints.NONE, GridBagConstraints.NORTH);
        layout.setConstraints(bagButtonPanel, glbc);
        bagPanel.add(bagButtonPanel);
        buildConstraints(glbc, 1, row, colspan, 1, 90, 40, GridBagConstraints.BOTH, GridBagConstraints.EAST);
        layout.setConstraints(bagPayloadTreePanel, glbc);
    	bagPanel.add(bagPayloadTreePanel);
        row++;
        buildConstraints(glbc, 0, row, colspan, 1, 1, 10, GridBagConstraints.NONE, GridBagConstraints.NORTH);
        layout.setConstraints(bagTagButtonPanel, glbc);
    	bagPanel.add(bagTagButtonPanel);
        buildConstraints(glbc, 1, row, colspan, 1, 90, 20, GridBagConstraints.BOTH, GridBagConstraints.EAST);
        layout.setConstraints(bagTagFileTreePanel, glbc);
    	bagPanel.add(bagTagFileTreePanel);
        row++;
        colspan = 3;
        buildConstraints(glbc, 0, row, colspan, 1, 1, 30, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(compositePane, glbc);
        bagPanel.add(compositePane);

        return bagPanel;
    }
    
    private JPanel createBagButtonPanel() {
    	JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));

    	addDataButton = new JButton(getPropertyMessage("bag.button.add"));
    	addDataHandler = new AddDataHandler(this);
    	addDataButton.addActionListener(addDataHandler);
    	addDataButton.setEnabled(false);
    	addDataButton.setToolTipText(getPropertyMessage("bag.button.add.help"));
        buttonPanel.add(addDataButton, BorderLayout.NORTH);

    	removeDataButton = new JButton(getPropertyMessage("bag.button.remove"));
    	removeDataHandler = new RemoveDataHandler(this);
    	removeDataButton.addActionListener(removeDataHandler);
    	removeDataButton.setEnabled(false);
    	removeDataButton.setToolTipText(getPropertyMessage("bag.button.remove.help"));
    	buttonPanel.add(removeDataButton, BorderLayout.CENTER);

        return buttonPanel;
    }
    
    private JPanel createBagTagButtonPanel() {
    	JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));

    	showTagButton = new JButton(getPropertyMessage("bag.tagbutton.show"));
    	showTagButton.addActionListener(new ShowTagFilesHandler(this));
    	showTagButton.setEnabled(false);
    	showTagButton.setToolTipText(getPropertyMessage("bag.tagbutton.show.help"));
    	buttonPanel.add(showTagButton, BorderLayout.NORTH);

    	addTagFileButton = new JButton(getPropertyMessage("bag.tagbutton.add"));
    	addTagFileHandler = new AddTagFileHandler(this);
    	addTagFileButton.addActionListener(addTagFileHandler);
    	addTagFileButton.setEnabled(false);
    	addTagFileButton.setToolTipText(getPropertyMessage("bag.tagbutton.add.help"));
    	buttonPanel.add(addTagFileButton, BorderLayout.CENTER);
    	
    	removeTagFileButton = new JButton(getPropertyMessage("bag.tagbutton.remove"));
    	removeTagFileHandler = new RemoveTagFileHandler(this);
    	removeTagFileButton.addActionListener(removeTagFileHandler);
    	removeTagFileButton.setEnabled(false);
    	removeTagFileButton.setToolTipText(getPropertyMessage("bag.tagbutton.remove.help"));
    	buttonPanel.add(removeTagFileButton, BorderLayout.SOUTH);

        return buttonPanel;
    }

    public void enableBagSettings(boolean b) {
    	bagPayloadTree.setEnabled(b);
    	bagPayloadTreePanel.setEnabled(b);
    	bagTagFileTree.setEnabled(b);
    	bagTagFileTreePanel.setEnabled(b);
    	infoInputPane.projectList.setEnabled(b);
        infoInputPane.newProjectButton.setEnabled(b);
        infoInputPane.removeProjectButton.setEnabled(b);
        infoInputPane.defaultProject.setEnabled(b);
        infoInputPane.bagInfoInputPane.setEnabled(b);
        infoInputPane.saveButton.setEnabled(b);
        infoInputPane.loadDefaultsButton.setEnabled(b);
        infoInputPane.clearDefaultsButton.setEnabled(b);
        infoInputPane.holeyCheckbox.setEnabled(false);
        infoInputPane.serializeGroupPanel.setEnabled(false);
        infoInputPane.zipButton.setEnabled(false);
        infoInputPane.tarButton.setEnabled(false);
        infoInputPane.tarGzButton.setEnabled(false);
        infoInputPane.tarBz2Button.setEnabled(false);
        infoInputPane.noneButton.setEnabled(false);
    }

    public void buildConstraints(GridBagConstraints gbc,int x, int y, int w, int h, int wx, int wy, int fill, int anchor) {
    	gbc.gridx = x; // start cell in a row
    	gbc.gridy = y; // start cell in a column
    	gbc.gridwidth = w; // how many column does the control occupy in the row
    	gbc.gridheight = h; // how many column does the control occupy in the column
    	gbc.weightx = wx; // relative horizontal size
    	gbc.weighty = wy; // relative vertical size
    	gbc.fill = fill; // the way how the control fills cells
    	gbc.anchor = anchor; // alignment
    }

    public String updateBaggerRules() {
        baggerRules.init(bag.isEdeposit(), bag.isNdnp(), !bag.isNoProject(), bag.isHoley());
        String messages = "";
        bag.updateStrategy();
        
        return messages;
    }

    public void dropBagTagFile(List<File> files) {
    	addTagFileHandler.addTagFiles(files);
    }

    public void dropBagPayloadData(List<File> files) {
    	addDataHandler.addPayloadData(files);
    }

    public void showWarningErrorDialog(String title, String msg) {
    	MessageDialog dialog = new MessageDialog(title, msg);
	    dialog.showDialog();
    }

    public void enableSettings(boolean b) {
    	infoInputPane.bagNameField.setEnabled(b);
    	infoInputPane.bagVersionValue.setEnabled(b);
    	infoInputPane.projectList.setEnabled(b);
    	infoInputPane.newProjectButton.setEnabled(b);
    	infoInputPane.removeProjectButton.setEnabled(b);
        infoInputPane.holeyValue.setEnabled(b);
        infoInputPane.serializeValue.setEnabled(b);
        infoInputPane.saveButton.setEnabled(b);
        infoInputPane.loadDefaultsButton.setEnabled(b);
        infoInputPane.clearDefaultsButton.setEnabled(b);
    }

    private void updateCommands() {
    	startExecutor.setEnabled(true);
    	openExecutor.setEnabled(true);
    	createBagInPlaceExecutor.setEnabled(true);
    	clearExecutor.setEnabled(false);
        validateExecutor.setEnabled(false);
        completeExecutor.setEnabled(false);
        addDataExecutor.setEnabled(false);
        saveBagExecutor.setEnabled(false);
        saveBagAsExecutor.setEnabled(false);
    }

    public void updateClearBag(String messages) {
    	enableBagSettings(false);
    	infoInputPane.holeyCheckbox.setSelected(false);
    	infoInputPane.holeyValue.setText("false");
    	infoInputPane.noneButton.setSelected(true);
    	addDataButton.setEnabled(false);
    	addDataExecutor.setEnabled(false);
    	infoInputPane.updatePropButton.setEnabled(false);
    	saveButton.setEnabled(false);
    	saveBagExecutor.setEnabled(false);
    	saveAsButton.setEnabled(false);
    	saveBagAsExecutor.setEnabled(false);
    	removeDataButton.setEnabled(false);
    	showTagButton.setEnabled(false);
    	addTagFileButton.setEnabled(false);
    	removeTagFileButton.setEnabled(false);
    	closeButton.setEnabled(false);
    	validateButton.setEnabled(false);
    	completeButton.setEnabled(false);
    	clearExecutor.setEnabled(false);
    	validateExecutor.setEnabled(false);
    	completeExecutor.setEnabled(false);
    	bagButtonPanel.invalidate();
    	topButtonPanel.invalidate();
    }

    public void updateNewBag() {
        showTagButton.setEnabled(true);
        enableBagSettings(true);
		addDataButton.setEnabled(true);
		addDataExecutor.setEnabled(true);
		addTagFileButton.setEnabled(true);
		closeButton.setEnabled(true);
		removeTagFileButton.setEnabled(true);
		bagButtonPanel.invalidate();
    }

    public void updateOpenBag() {
        addDataButton.setEnabled(true);
        addDataExecutor.setEnabled(true);
        infoInputPane.updatePropButton.setEnabled(false);
        saveButton.setEnabled(true);
        saveBagExecutor.setEnabled(true);
        saveAsButton.setEnabled(true);
        removeDataButton.setEnabled(true);
        addTagFileButton.setEnabled(true);
        removeTagFileButton.setEnabled(true);
        showTagButton.setEnabled(true);
        saveBagAsExecutor.setEnabled(true);
        bagButtonPanel.invalidate();
        closeButton.setEnabled(true);
        validateButton.setEnabled(true);
        completeButton.setEnabled(true);
        completeExecutor.setEnabled(true);
        validateExecutor.setEnabled(true);
        topButtonPanel.invalidate();
    }
    
    public void updateBagInPlace() {
        addDataButton.setEnabled(true);
        addDataExecutor.setEnabled(true);
        infoInputPane.updatePropButton.setEnabled(false);
        saveButton.setEnabled(false);
        saveBagExecutor.setEnabled(false);
        saveAsButton.setEnabled(true);
        saveBagAsExecutor.setEnabled(true);
        removeDataButton.setEnabled(true);
        addTagFileButton.setEnabled(true);
        removeTagFileButton.setEnabled(true);
        showTagButton.setEnabled(true);
        bagButtonPanel.invalidate();
        closeButton.setEnabled(true);
        validateButton.setEnabled(true);
        completeButton.setEnabled(true);
        completeExecutor.setEnabled(true);
        validateExecutor.setEnabled(true);
        bagButtonPanel.invalidate();
        topButtonPanel.invalidate();
    }
    
    public void updateSaveBag() {
		addDataButton.setEnabled(true);
		addDataExecutor.setEnabled(true);
		infoInputPane.updatePropButton.setEnabled(false);
		saveButton.setEnabled(true);
		saveBagExecutor.setEnabled(true);
		saveAsButton.setEnabled(true);
		removeDataButton.setEnabled(true);
		addTagFileButton.setEnabled(true);
		removeTagFileButton.setEnabled(true);
		showTagButton.setEnabled(true);
		saveBagAsExecutor.setEnabled(true);
		bagButtonPanel.invalidate();
		closeButton.setEnabled(true);
		validateButton.setEnabled(true);
		completeButton.setEnabled(true);
		clearExecutor.setEnabled(true);
		validateExecutor.setEnabled(true);
		completeExecutor.setEnabled(true);
		topButtonPanel.invalidate();
    }
    
    public void updateAddData() {
    	saveAsButton.setEnabled(true);
    	saveBagAsExecutor.setEnabled(true);
    	removeDataButton.setEnabled(true);
    	bagButtonPanel.invalidate();
    	topButtonPanel.invalidate();
    }
    
    public void updateBag() {
    	infoInputPane.updatePropButton.setEnabled(false);
    }

    public void updateManifestPane() {
        bagTagFileTree = new BagTree(this, bag.getName(), false);
        Collection<BagFile> tags = bag.getBag().getTags();
        for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
            bagTagFileTree.addNode(bf.getFilepath());
        }
        bagTagFileTreePanel.refresh(bagTagFileTree);
    }

    protected void registerLocalCommandExecutors(PageComponentContext context) {
    	context.register("startCommand", startExecutor);
    	context.register("openCommand", openExecutor);
    	context.register("createBagInPlaceCommand", createBagInPlaceExecutor);
    	context.register("clearCommand", clearExecutor);
    	context.register("validateCommand", validateExecutor);
    	context.register("completeCommand", completeExecutor);
    	context.register("addDataCommand", addDataExecutor);
    	context.register("saveBagCommand", saveBagExecutor);
    	context.register("saveBagAsCommand", saveBagAsExecutor);
    }

    public void onApplicationEvent(ApplicationEvent e) {
    	log.info("BagView.onApplicationEvent: " + e);
        if (e instanceof LifecycleApplicationEvent) {
            LifecycleApplicationEvent le = (LifecycleApplicationEvent)e;
            if (le.getEventType() == LifecycleApplicationEvent.CREATED && le.objectIs(Profile.class)) {
            	Profile profile = (Profile) le.getObject();
            	bagProject.userProfiles.add(profile);
            }
        }
    }

    private String getInitialConsoleMsg() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append(getPropertyMessage("consolepane.msg.help"));
    	buffer.append("\n\n");
    	buffer.append(getPropertyMessage("consolepane.status.help"));
    	buffer.append("\n\n");
    	return buffer.toString();
    }

    /**
     * The actionPerformed method in this class
     * is called each time the Timer "goes off".
     */
    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            progressMonitor.setProgress(task.getCurrent().intValue());
            String s = task.getMessage();
            if (s != null) {
                progressMonitor.setNote(s);
                taskOutput.append(s + "\n");
                taskOutput.setCaretPosition(
                    taskOutput.getDocument().getLength());
            }
            if (progressMonitor.isCanceled() || task.isDone()) {
                progressMonitor.close();
                task.stop();
                Toolkit.getDefaultToolkit().beep();
                timer.stop();
                if (longRunningProcess != null) longRunningProcess.cancel();
                if (task.isDone()) {
                    taskOutput.append("Task completed.\n");
                } else {
                    taskOutput.append("Task canceled.\n");

                }
            }
        }
    }

    public void statusBarBegin(Progress progress, String message, Long size) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
        task = new LongTask();
        task.setLengthOfTask(size);
        progress.setTask(task);
        task.setProgress(progress);

        taskOutput = new JTextArea(5, 20);
        timer = new Timer(ONE_SECOND, new TimerListener());

        progressMonitor = new ProgressMonitor(this.getControl(),
                "Running a Long Task", "", 0, task.getLengthOfTask().intValue());
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(1 * ONE_SECOND);
        task.setMonitor(progressMonitor);

        task.go();
        timer.start();
    }
    
    public void statusBarEnd() {
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());    	
    }

}
