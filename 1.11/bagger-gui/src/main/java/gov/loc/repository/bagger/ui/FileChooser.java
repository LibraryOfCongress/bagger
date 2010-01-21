package gov.loc.repository.bagger.ui;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileChooser extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(FileChooser.class);

	public FileChooser() {
		setTitle("ZipTest");
		setSize(300, 400);

		JMenuBar mbar = new JMenuBar();
		JMenu m = new JMenu("File");
		openItem = new JMenuItem("Open");
		openItem.addActionListener(this);
		m.add(openItem);
		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(this);
		m.add(exitItem);
		mbar.add(m);

		Container contentPane = getContentPane();
		contentPane.add(mbar, "North");
  }

  public void actionPerformed(ActionEvent evt) {
	  Object source = evt.getSource();
	  if (source == openItem) {
		  JFileChooser chooser = new JFileChooser();
		  chooser.setCurrentDirectory(new File("."));
		  chooser.setFileFilter(new FileFilter() {
			  public boolean accept(File f) {
				  return f.getName().toLowerCase().endsWith(".zip")
				  	|| f.isDirectory();
			  }

			  public String getDescription() {
				  return "ZIP Files";
			  }
		  });
		  int r = chooser.showOpenDialog(this);
		  if (r == JFileChooser.APPROVE_OPTION) {
			  String zipname = chooser.getSelectedFile().getPath();
			  log.debug(zipname);
		  }
	  } 
	  else if (source == exitItem) {
		  //this.hide();
		  this.dispatchEvent(evt);
		  //System.exit(0);
	  }
  }

  public static void main(String[] args) {
	  Frame f = new FileChooser();
	  f.toFront();
//	  f.show();
  }

  private JMenuItem openItem;

  private JMenuItem exitItem;

}
