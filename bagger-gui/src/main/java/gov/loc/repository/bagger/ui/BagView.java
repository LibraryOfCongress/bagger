
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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
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
    private final static int ONE_SECOND = 1000;
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
//	private JButton openButton;
//	private JButton createSkeletonButton;
//	private JButton addDataButton;
//	private JButton removeDataButton;
//	private JButton saveButton;
//	private JButton saveAsButton;
//	private JButton completeButton;
//	private JButton validateButton;
//	private JButton closeButton;
//	private JButton showTagButton;
//	private JButton addTagFileButton;
//	private JButton removeTagFileButton;

    public StartNewBagHandler startNewBagHandler;
	public StartExecutor startExecutor = new StartExecutor(this);
	public OpenBagHandler openBagHandler;
    public  OpenExecutor openExecutor = new OpenExecutor(this);
    public CreateBagInPlaceHandler createBagInPlaceHandler;
    public CreateBagInPlaceExecutor createBagInPlaceExecutor = new CreateBagInPlaceExecutor(this);
    public SaveBagHandler saveBagHandler;
    public SaveBagExecutor saveBagExecutor = new SaveBagExecutor(this);
    public SaveBagAsHandler saveBagAsHandler;
    public SaveBagAsExecutor saveBagAsExecutor = new SaveBagAsExecutor(this);
    public ValidateBagHandler validateBagHandler;
    public ValidateExecutor validateExecutor = new ValidateExecutor(this);
    public CompleteBagHandler completeBagHandler;
    public CompleteExecutor completeExecutor = new CompleteExecutor(this);
    public ClearBagHandler clearBagHandler;
    public ClearBagExecutor clearExecutor = new ClearBagExecutor(this);
    public AddDataHandler addDataHandler;
    public  AddDataExecutor addDataExecutor = new AddDataExecutor(this);
    public RemoveDataHandler removeDataHandler;
    public RemoveTagFileHandler removeTagFileHandler;
    public AddTagFileHandler addTagFileHandler;
    
    public static BagView instance;

    public Color errorColor = new Color(255, 128, 128);
	public Color infoColor = new Color(120, 120, 120);
	private JLabel addDataToolBarAction;
	private JLabel removeDataToolBarAction;
	private JLabel viewTagFilesToolbarAction;
	private JLabel addTagFileToolBarAction;
	private JLabel removeTagFileToolbarAction;

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
    	instance = this;
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
        JSplitPane bagPanel = createBagPanel();

    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        buildConstraints(glbc, 0, 0, 1, 1, 50, 100, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        layout.setConstraints(bagPanel, glbc);

//        buildConstraints(glbc, 1, 0, 1, 1, 50, 100, GridBagConstraints.BOTH, GridBagConstraints.NORTH);
//        layout.setConstraints(infoInputPane, glbc);

        JPanel mainPanel = new JPanel(layout);
        mainPanel.add(bagPanel);
        //mainPanel.add(infoInputPane);
        
    	JPanel bagViewPanel = new JPanel(new BorderLayout(2, 2));
        bagViewPanel.setBackground(bgColor);
    	//bagViewPanel.add(bagButtonPanel, BorderLayout.NORTH);
    	bagViewPanel.add(mainPanel, BorderLayout.CENTER);
        return bagViewPanel;
    }
    
    private JPanel createTopButtonPanel() {
        Color fgColor = new Color(250, 250, 250);
        Color bgColor = new Color(50, 50, 150);

    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//    	buttonPanel.setBackground(bgColor);

    	//JButton createButton = new JButton(getPropertyMessage("bag.button.create"));
    	startNewBagHandler = new StartNewBagHandler(this);
//    	createButton.addActionListener(startNewBagHandler);
//    	createButton.setOpaque(true);
//    	createButton.setBackground(bgColor);
//    	createButton.setForeground(fgColor);
//    	createButton.setToolTipText(getPropertyMessage("bag.button.create.help"));
//    	buttonPanel.add(createButton);

    	//openButton = new JButton(getPropertyMessage("bag.button.open"));
    	openBagHandler = new OpenBagHandler(this);
//    	openButton.addActionListener(openBagHandler);
//    	openButton.setEnabled(true);
//    	openButton.setOpaque(true);
//    	openButton.setBackground(bgColor);
//    	openButton.setForeground(fgColor);
//    	openButton.setToolTipText(getPropertyMessage("bag.button.open.help"));
//    	buttonPanel.add(openButton);

    	//createSkeletonButton = new JButton(getPropertyMessage("bag.button.createskeleton"));
    	createBagInPlaceHandler = new CreateBagInPlaceHandler(this);
//    	createSkeletonButton.addActionListener(createBagInPlaceHandler);
//    	createSkeletonButton.setEnabled(true);
//    	createSkeletonButton.setOpaque(true);
//    	createSkeletonButton.setBackground(bgColor);
//    	createSkeletonButton.setForeground(fgColor);
//    	createSkeletonButton.setToolTipText(getPropertyMessage("bag.button.createskeleton.help"));
//    	buttonPanel.add(createSkeletonButton);

        //saveButton = new JButton(getPropertyMessage("bag.button.save"));
        saveBagHandler = new SaveBagHandler(this);
//        saveButton.addActionListener(saveBagHandler);
//        saveButton.setEnabled(false);
//        saveButton.setOpaque(true);
//        saveButton.setBackground(bgColor);
//        saveButton.setForeground(fgColor);
//        saveButton.setToolTipText(getPropertyMessage("bag.button.save.help"));
//        buttonPanel.add(saveButton);

    	//saveAsButton = new JButton(getPropertyMessage("bag.button.saveas"));
    	saveBagAsHandler = new SaveBagAsHandler(this);
//    	saveAsButton.addActionListener(saveBagAsHandler);
//        saveAsButton.setEnabled(false);
//        saveAsButton.setOpaque(true);
//        saveAsButton.setBackground(bgColor);
//        saveAsButton.setForeground(fgColor);
//        saveAsButton.setToolTipText(getPropertyMessage("bag.button.saveas.help"));
//        buttonPanel.add(saveAsButton);

        //completeButton = new JButton(getPropertyMessage("bag.button.complete"));
    	completeBagHandler = new CompleteBagHandler(this);
//        completeButton.addActionListener(completeBagHandler);
//        completeButton.setEnabled(false);
//        completeButton.setOpaque(true);
//        completeButton.setBackground(bgColor);
//        completeButton.setForeground(fgColor);
//        completeButton.setToolTipText(getPropertyMessage("bag.button.complete.help"));
//        buttonPanel.add(completeButton);
        
       // validateButton = new JButton(getPropertyMessage("bag.button.validate"));
    	validateBagHandler = new ValidateBagHandler(this);
//        validateButton.addActionListener(validateBagHandler);
//        validateButton.setEnabled(false);
//        validateButton.setOpaque(true);
//    	validateButton.setBackground(bgColor);
//    	validateButton.setForeground(fgColor);
//    	validateButton.setToolTipText(getPropertyMessage("bag.button.validate.help"));
//        buttonPanel.add(validateButton);
        
        //closeButton = new JButton(getPropertyMessage("bag.button.clear"));
    	clearBagHandler = new ClearBagHandler(this);
//    	closeButton.addActionListener(clearBagHandler);
//    	closeButton.setEnabled(false);
//    	closeButton.setOpaque(true);
//    	closeButton.setBackground(bgColor);
//    	closeButton.setForeground(fgColor);
//    	closeButton.setToolTipText(getPropertyMessage("bag.button.clear.help"));
//        buttonPanel.add(closeButton);

        return buttonPanel;
    }
    
    private JSplitPane createBagPanel() {
    	
    	LineBorder border = new LineBorder(Color.GRAY,1);
    	
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
    	
    	
    	
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		JPanel payloadPannel = new JPanel();
		splitPane.setLeftComponent(payloadPannel);
		payloadPannel.setLayout(new BorderLayout(0, 0));
		
		JPanel payLoadToolBarPanel = new JPanel();
		payloadPannel.add(payLoadToolBarPanel, BorderLayout.NORTH);
		payLoadToolBarPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel payloadLabelPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) payloadLabelPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		payLoadToolBarPanel.add(payloadLabelPanel);
		
		JLabel lblPayloadTree = new JLabel(getPropertyMessage("bagView.payloadTree.name"));
		payloadLabelPanel.add(lblPayloadTree);
		
//		JPanel payloadToolbarButtonPannel = new JPanel();
//		FlowLayout flowLayout_2 = (FlowLayout) payloadToolbarButtonPannel.getLayout();
//		flowLayout_2.setAlignment(FlowLayout.RIGHT);
//		payLoadToolBarPanel.add(payloadToolbarButtonPannel);
//		JLabel label = new JLabel("New label");
//		payloadToolbarButtonPannel.add(label);
		
		payLoadToolBarPanel.add(bagButtonPanel);
		
		
		payloadPannel.add(bagPayloadTreePanel, BorderLayout.CENTER);
		
		JPanel tagFilePanel = new JPanel();
		splitPane.setRightComponent(tagFilePanel);
		tagFilePanel.setLayout(new BorderLayout(0, 0));
		
		JPanel tagFileToolBarPannel = new JPanel();
		tagFilePanel.add(tagFileToolBarPannel, BorderLayout.NORTH);
		tagFileToolBarPannel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel TagFileLabelPanel = new JPanel();
		FlowLayout tagFileToolbarFlowLayout = (FlowLayout) TagFileLabelPanel.getLayout();
		tagFileToolbarFlowLayout.setAlignment(FlowLayout.LEFT);
		tagFileToolBarPannel.add(TagFileLabelPanel);
		
		JLabel tagFileTreeLabel = new JLabel(getPropertyMessage("bagView.TagFilesTree.name"));
		TagFileLabelPanel.add(tagFileTreeLabel);
		
		tagFileToolBarPannel.add(bagTagButtonPanel);
		
		
		tagFilePanel.add(bagTagFileTreePanel, BorderLayout.CENTER);
    	
    	
		compositePane = new CompositePane(this, getInitialConsoleMsg());

		

        return splitPane;
    }
    
    private JPanel createBagButtonPanel() {
    	
    	addDataHandler = new AddDataHandler(this);
    	removeDataHandler = new RemoveDataHandler(this);
    	
    	JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		
		addDataToolBarAction = new JLabel("");
		addDataToolBarAction.setEnabled(false);
		addDataToolBarAction.setHorizontalAlignment(SwingConstants.CENTER);
		addDataToolBarAction.setBorder(new LineBorder(addDataToolBarAction.getBackground(),1));
		addDataToolBarAction.setIcon(getPropertyImage("Bag_Content.add.icon"));
		addDataToolBarAction.setToolTipText(getPropertyMessage("bagView.payloadTree.addbutton.tooltip"));
		
		addDataToolBarAction.addMouseListener(new MouseAdapter()  {
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(addDataToolBarAction.isEnabled())
					addDataHandler.actionPerformed(null);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				addDataToolBarAction.setBorder(new LineBorder(addDataToolBarAction.getBackground(),1));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(addDataToolBarAction.isEnabled())
					addDataToolBarAction.setBorder(new LineBorder(Color.GRAY,1));
			}
		});
		buttonPanel.add(addDataToolBarAction);
		
		removeDataToolBarAction = new JLabel("");
		removeDataToolBarAction.setEnabled(false);
		removeDataToolBarAction.setHorizontalAlignment(SwingConstants.CENTER);
		removeDataToolBarAction.setBorder(new LineBorder(removeDataToolBarAction.getBackground(),1));
		removeDataToolBarAction.setIcon(getPropertyImage("Bag_Content.minus.icon"));
		removeDataToolBarAction.setToolTipText(getPropertyMessage("bagView.payloadTree.remove.tooltip"));
		buttonPanel.add(removeDataToolBarAction);
		removeDataToolBarAction.addMouseListener(new MouseAdapter() {
		  
					@Override
					public void mousePressed(MouseEvent e) {	
						if(removeDataToolBarAction.isEnabled())
							removeDataHandler.actionPerformed(null);
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						removeDataToolBarAction.setBorder(new LineBorder(removeDataToolBarAction.getBackground(),1));
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						if(removeDataToolBarAction.isEnabled())
							removeDataToolBarAction.setBorder(new LineBorder(Color.GRAY,1));
					}
				});
		
		final JLabel spacerLabel = new JLabel("    ");
		buttonPanel.add(spacerLabel);
	
		
    	//addDataButton = new JButton(getPropertyMessage("bag.button.add"));
    	addDataHandler = new AddDataHandler(this);
//    	addDataButton.addActionListener(addDataHandler);
//    	addDataButton.setEnabled(false);
//    	addDataButton.setToolTipText(getPropertyMessage("bag.button.add.help"));
//        buttonPanel.add(addDataButton, BorderLayout.NORTH);

    	//removeDataButton = new JButton(getPropertyMessage("bag.button.remove"));
    	removeDataHandler = new RemoveDataHandler(this);
//    	removeDataButton.addActionListener(removeDataHandler);
//    	removeDataButton.setEnabled(false);
//    	removeDataButton.setToolTipText(getPropertyMessage("bag.button.remove.help"));
//    	buttonPanel.add(removeDataButton, BorderLayout.CENTER);

        return buttonPanel;
    }
    
    private JPanel createBagTagButtonPanel() {
    	
    	JPanel buttonPanel = new JPanel();
    	
    	final ShowTagFilesHandler showTageFileHandler = new ShowTagFilesHandler(this);
    	addTagFileHandler = new AddTagFileHandler(this);
    	removeTagFileHandler = new RemoveTagFileHandler(this);
    	
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		
		viewTagFilesToolbarAction = new JLabel("");
		viewTagFilesToolbarAction.setEnabled(false);
		viewTagFilesToolbarAction.setHorizontalAlignment(SwingConstants.CENTER);
		viewTagFilesToolbarAction.setBorder(new LineBorder(viewTagFilesToolbarAction.getBackground(),1));
		viewTagFilesToolbarAction.setIcon(getPropertyImage("Bag_ViewTagFile.icon"));
		viewTagFilesToolbarAction.setToolTipText(getPropertyMessage("bagView.TagFilesTree.viewfile.tooltip"));
		
		viewTagFilesToolbarAction.addMouseListener(new MouseAdapter()  {
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(viewTagFilesToolbarAction.isEnabled())
					showTageFileHandler.actionPerformed(null);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				viewTagFilesToolbarAction.setBorder(new LineBorder(viewTagFilesToolbarAction.getBackground(),1));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(viewTagFilesToolbarAction.isEnabled())
					viewTagFilesToolbarAction.setBorder(new LineBorder(Color.GRAY,1));
			}
		});
		buttonPanel.add(viewTagFilesToolbarAction);
		
		addTagFileToolBarAction = new JLabel("");
		addTagFileToolBarAction.setEnabled(false);
		addTagFileToolBarAction.setHorizontalAlignment(SwingConstants.CENTER);
		addTagFileToolBarAction.setBorder(new LineBorder(addTagFileToolBarAction.getBackground(),1));
		addTagFileToolBarAction.setIcon(getPropertyImage("Bag_Content.add.icon"));
		addTagFileToolBarAction.setToolTipText(getPropertyMessage("bagView.TagFilesTree.addbutton.tooltip"));

		addTagFileToolBarAction.addMouseListener(new MouseAdapter()  {
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(addTagFileToolBarAction.isEnabled())
					addTagFileHandler.actionPerformed(null);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				addTagFileToolBarAction.setBorder(new LineBorder(addTagFileToolBarAction.getBackground(),1));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(addTagFileToolBarAction.isEnabled())
					addTagFileToolBarAction.setBorder(new LineBorder(Color.GRAY,1));
			}
		});
		buttonPanel.add(addTagFileToolBarAction);
		
		removeTagFileToolbarAction = new JLabel("");
		removeTagFileToolbarAction.setEnabled(false);
		removeTagFileToolbarAction.setHorizontalAlignment(SwingConstants.CENTER);
		removeTagFileToolbarAction.setBorder(new LineBorder(removeTagFileToolbarAction.getBackground(),1));
		removeTagFileToolbarAction.setIcon(getPropertyImage("Bag_Content.minus.icon"));
		removeTagFileToolbarAction.setToolTipText(getPropertyMessage("bagView.TagFilesTree.remove.tooltip"));

		buttonPanel.add(removeTagFileToolbarAction);
		removeTagFileToolbarAction.addMouseListener(new MouseAdapter() {
		  
					@Override
					public void mousePressed(MouseEvent e) {
						if(removeTagFileToolbarAction.isEnabled())
							removeTagFileHandler.actionPerformed(null);
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						removeTagFileToolbarAction.setBorder(new LineBorder(removeTagFileToolbarAction.getBackground(),1));
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						if(removeTagFileToolbarAction.isEnabled())
							removeTagFileToolbarAction.setBorder(new LineBorder(Color.GRAY,1));
					}
				});
		
		
		final JLabel spacerLabel = new JLabel("    ");
		buttonPanel.add(spacerLabel);

    	addTagFileHandler = new AddTagFileHandler(this);
    	removeTagFileHandler = new RemoveTagFileHandler(this);

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
        baggerRules.init(bag.isEdeposit(), bag.isNdnp(), bag.isWdl(), !bag.isNoProject(), bag.isHoley());
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
    	addDataToolBarAction.setEnabled(false);
    	removeDataToolBarAction.setEnabled(false);
    	addDataExecutor.setEnabled(false);
    	infoInputPane.updatePropButton.setEnabled(false);
    	//saveButton.setEnabled(false);
    	saveBagExecutor.setEnabled(false);
    	//saveAsButton.setEnabled(false);
    	saveBagAsExecutor.setEnabled(false);
    	//removeDataButton.setEnabled(false);
    	viewTagFilesToolbarAction.setEnabled(false);
    	addTagFileToolBarAction.setEnabled(false);
    	removeTagFileToolbarAction.setEnabled(false);
    	//removeTagFileButton.setEnabled(false);
    	//closeButton.setEnabled(false);
    	//validateButton.setEnabled(false);
    	//completeButton.setEnabled(false);
    	clearExecutor.setEnabled(false);
    	validateExecutor.setEnabled(false);
    	completeExecutor.setEnabled(false);
    	bagButtonPanel.invalidate();
    	topButtonPanel.invalidate();
    }

    public void updateNewBag() {
        viewTagFilesToolbarAction.setEnabled(true);
        enableBagSettings(true);
		addDataToolBarAction.setEnabled(true);
		addDataExecutor.setEnabled(true);
		addTagFileToolBarAction.setEnabled(true);
		//closeButton.setEnabled(true);
		//removeTagFileButton.setEnabled(true);
		bagButtonPanel.invalidate();
    }

    public void updateOpenBag() {
        addDataToolBarAction.setEnabled(true);
        addDataExecutor.setEnabled(true);
        infoInputPane.updatePropButton.setEnabled(false);
        //saveButton.setEnabled(true);
        saveBagExecutor.setEnabled(true);
        //saveAsButton.setEnabled(true);
        //removeDataButton.setEnabled(true);
        addTagFileToolBarAction.setEnabled(true);
        //removeTagFileButton.setEnabled(true);
        viewTagFilesToolbarAction.setEnabled(true);
        saveBagAsExecutor.setEnabled(true);
        bagButtonPanel.invalidate();
        //closeButton.setEnabled(true);
        //validateButton.setEnabled(true);
        //completeButton.setEnabled(true);
        completeExecutor.setEnabled(true);
        validateExecutor.setEnabled(true);
        topButtonPanel.invalidate();
    }
    
    public void updateBagInPlace() {
    	addDataToolBarAction.setEnabled(true);
        addDataExecutor.setEnabled(true);
        infoInputPane.updatePropButton.setEnabled(false);
        //saveButton.setEnabled(false);
        saveBagExecutor.setEnabled(false);
        //saveAsButton.setEnabled(true);
        saveBagAsExecutor.setEnabled(true);
        //removeDataButton.setEnabled(true);
        addTagFileToolBarAction.setEnabled(true);
        //removeTagFileButton.setEnabled(true);
        viewTagFilesToolbarAction.setEnabled(true);
        bagButtonPanel.invalidate();
        //closeButton.setEnabled(true);
        //validateButton.setEnabled(true);
        //completeButton.setEnabled(true);
        completeExecutor.setEnabled(true);
        validateExecutor.setEnabled(true);
        bagButtonPanel.invalidate();
        topButtonPanel.invalidate();
    }
    
    public void updateSaveBag() {
		addDataToolBarAction.setEnabled(true);
		addDataExecutor.setEnabled(true);
		infoInputPane.updatePropButton.setEnabled(false);
		//saveButton.setEnabled(true);
		saveBagExecutor.setEnabled(true);
		//saveAsButton.setEnabled(true);
		//removeDataButton.setEnabled(true);
		addTagFileToolBarAction.setEnabled(true);
		//removeTagFileButton.setEnabled(true);
		viewTagFilesToolbarAction.setEnabled(true);
		saveBagAsExecutor.setEnabled(true);
		bagButtonPanel.invalidate();
		//closeButton.setEnabled(true);
		//validateButton.setEnabled(true);
		//completeButton.setEnabled(true);
		clearExecutor.setEnabled(true);
		validateExecutor.setEnabled(true);
		completeExecutor.setEnabled(true);
		topButtonPanel.invalidate();
    }
    
    public void updateAddData() {
    	//saveAsButton.setEnabled(true);
    	saveBagAsExecutor.setEnabled(true);
    	//removeDataButton.setEnabled(true);
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
            	bagProject.userProfiles.put(bag.getProject().getName(), profile);
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
    
    
    public void registerTreeListener(String label, final JTree tree)
    {
    	if(AbstractBagConstants.DATA_DIRECTORY.equals(label))
    	{
    		tree.addTreeSelectionListener(new TreeSelectionListener() {

    			public void valueChanged(TreeSelectionEvent e) {

    				TreePath[] paths = tree.getSelectionPaths();
    				if(paths == null || paths.length == 0)
    					return;

    				for(TreePath path: paths)
    				{
    					if(path.getPathCount() == 1)
    					{
    						removeDataToolBarAction.setEnabled(false);
    						return;
    					}
    				}

    				removeDataToolBarAction.setEnabled(true);
    			}
    		});
    	}
    	else 
    	{
    		tree.addTreeSelectionListener(new TreeSelectionListener() {

    			public void valueChanged(TreeSelectionEvent e) {

    				TreePath[] paths = tree.getSelectionPaths();
    				if(paths == null || paths.length == 0)
    					return;

    				for(TreePath path: paths)
    				{
    					if(path.getPathCount() == 1)
    					{
    						removeTagFileToolbarAction.setEnabled(false);
    						return;
    					}
    				}

    				removeTagFileToolbarAction.setEnabled(true);
    			}
    		});
    	}
    }
}
