package gov.loc.repository.bagger;

import javax.swing.AbstractButton;

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.command.config.ToolBarCommandButtonConfigurer;
import org.springframework.richclient.util.Assert;

/**
* Custom <code>CommandButtonConfigurer</code> for buttons on the toolbar.
* <p>
* Allows using large icons for toolbar.
*/
public class BbToolBarCommandButtonConfigurer extends ToolBarCommandButtonConfigurer {

   /**
    * Indicates if large icons should be used.
    */
   private Boolean useLargeIcons;

   /**
    * Creates this command button configurer.
    */
   public BbToolBarCommandButtonConfigurer() {

	super();
   }

   /**
    * {@inheritDoc}
    */
   public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor) {

	super.configure(button, command, faceDescriptor);
	faceDescriptor.configureIconInfo(button, this.getUseLargeIcons());
   }

   /**
    * Gets the useLargeIcons.
    * 
    * @return the useLargeIcons
    */
   public Boolean getUseLargeIcons() {

	if (this.useLargeIcons == null) {
	    this.setUseLargeIcons(Boolean.TRUE);
	}

	return this.useLargeIcons;
   }

   /**
    * Sets the useLargeIcons.
    * 
    * @param useLargeIcons
    *            the useLargeIcons to set
    */
   public void setUseLargeIcons(Boolean useLargeIcons) {
	Assert.notNull(useLargeIcons, "useLargeIcons");
	this.useLargeIcons = useLargeIcons;
   }
}