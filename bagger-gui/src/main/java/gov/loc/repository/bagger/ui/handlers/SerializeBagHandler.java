
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JRadioButton;

public class SerializeBagHandler{// extends AbstractAction {
//   	private static final long serialVersionUID = 1L;
//	BagView bagView;
//	DefaultBag bag;
//
//	public SerializeBagHandler(BagView bagView) {
//		super();
//		this.bagView = bagView;
//	}
//
//	public void actionPerformed(ActionEvent e) {
//		this.bag = bagView.getBag();
//
//		JRadioButton cb = (JRadioButton)e.getSource();
//        boolean isSel = cb.isSelected();
//        if (isSel) {
//        	if (cb == bagView.infoInputPane.noneButton) {
//            	bag.isSerial(false);
//            	bag.setSerialMode(DefaultBag.NO_MODE);
//            	bagView.infoInputPane.serializeValue.setText(DefaultBag.NO_LABEL);
//        	} else if (cb == bagView.infoInputPane.zipButton) {
//            	bag.isSerial(true);
//            	bag.setSerialMode(DefaultBag.ZIP_MODE);
//            	bagView.infoInputPane.serializeValue.setText(DefaultBag.ZIP_LABEL);
//        	} else if (cb == bagView.infoInputPane.tarButton) {
//            	bag.isSerial(true);
//            	bag.setSerialMode(DefaultBag.TAR_MODE);
//            	bagView.infoInputPane.serializeValue.setText(DefaultBag.TAR_LABEL);
//        	} else if (cb == bagView.infoInputPane.tarGzButton) {
//        		bag.isSerial(true);
//        		bag.setSerialMode(DefaultBag.TAR_GZ_MODE);
//        		bagView.infoInputPane.serializeValue.setText(DefaultBag.TAR_GZ_LABEL);
//        	} else if (cb == bagView.infoInputPane.tarBz2Button) {
//        		bag.isSerial(true);
//        		bag.setSerialMode(DefaultBag.TAR_BZ2_MODE);
//        		bagView.infoInputPane.serializeValue.setText(DefaultBag.TAR_BZ2_LABEL);
//        	} else {
//            	bag.isSerial(false);
//            	bag.setSerialMode(DefaultBag.NO_MODE);
//            	bagView.infoInputPane.serializeValue.setText(DefaultBag.NO_LABEL);
//        	}
//        }
//        bagView.setBag(bag);
//	}
}
