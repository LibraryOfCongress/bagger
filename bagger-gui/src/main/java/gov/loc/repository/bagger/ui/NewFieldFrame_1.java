package gov.loc.repository.bagger.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.GridBagLayout;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

public class NewFieldFrame_1 extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewFieldFrame_1 frame = new NewFieldFrame_1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NewFieldFrame_1() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 522, 364);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel = new JPanel();
		panel_2.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblProfile = new JLabel(" Profile Name : ");
		panel.add(lblProfile);
		
		JLabel label = new JLabel("");
		panel.add(label);
		
		JLabel lblValue = new JLabel("");
		panel.add(lblValue);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_2.add(panel_1);
		
		JButton btnAddAllDe = new JButton("Add All Default BagIt Fields");
		panel_1.add(btnAddAllDe);
		
		JPanel panel_3 = new JPanel();
		contentPane.add(panel_3, BorderLayout.CENTER);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_panel_3.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_4.getLayout();
		flowLayout_1.setVgap(0);
		flowLayout_1.setHgap(0);
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.anchor = GridBagConstraints.EAST;
		gbc_panel_4.insets = new Insets(0, 0, 5, 5);
		gbc_panel_4.gridx = 2;
		gbc_panel_4.gridy = 0;
		panel_3.add(panel_4, gbc_panel_4);
		
		JRadioButton radioButton = new JRadioButton("");
		panel_4.add(radioButton);
		
		JLabel lblAddFeild = new JLabel("Add Feild");
		panel_4.add(lblAddFeild);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 0;
		panel_3.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_5.getLayout();
		flowLayout_2.setHgap(0);
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.insets = new Insets(0, 0, 5, 5);
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.gridx = 2;
		gbc_panel_5.gridy = 1;
		panel_3.add(panel_5, gbc_panel_5);
		
		JRadioButton radioButton_1 = new JRadioButton("");
		panel_5.add(radioButton_1);
		
		JLabel lblAdd = new JLabel("Add Existing Field");
		panel_5.add(lblAdd);
		
		JComboBox comboBox = new JComboBox();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 3;
		gbc_comboBox.gridy = 1;
		panel_3.add(comboBox, gbc_comboBox);
		
		JLabel label_3 = new JLabel("New label");
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.anchor = GridBagConstraints.WEST;
		gbc_label_3.insets = new Insets(0, 0, 5, 5);
		gbc_label_3.gridx = 2;
		gbc_label_3.gridy = 2;
		panel_3.add(label_3, gbc_label_3);
		
		JComboBox comboBox_1 = new JComboBox();
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_1.gridx = 3;
		gbc_comboBox_1.gridy = 2;
		panel_3.add(comboBox_1, gbc_comboBox_1);
		
		JLabel label_4 = new JLabel("New label");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.anchor = GridBagConstraints.WEST;
		gbc_label_4.insets = new Insets(0, 0, 5, 5);
		gbc_label_4.gridx = 2;
		gbc_label_4.gridy = 3;
		panel_3.add(label_4, gbc_label_4);
		
		JCheckBox checkBox = new JCheckBox("");
		GridBagConstraints gbc_checkBox = new GridBagConstraints();
		gbc_checkBox.insets = new Insets(0, 0, 5, 0);
		gbc_checkBox.anchor = GridBagConstraints.WEST;
		gbc_checkBox.gridx = 3;
		gbc_checkBox.gridy = 3;
		panel_3.add(checkBox, gbc_checkBox);
		
		JLabel label_5 = new JLabel("New label");
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.anchor = GridBagConstraints.WEST;
		gbc_label_5.insets = new Insets(0, 0, 5, 5);
		gbc_label_5.gridx = 2;
		gbc_label_5.gridy = 4;
		panel_3.add(label_5, gbc_label_5);
		
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 3;
		gbc_textField_1.gridy = 4;
		panel_3.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		
		JButton btnRemoveItem = new JButton("Remove Item");
		GridBagConstraints gbc_btnRemoveItem = new GridBagConstraints();
		gbc_btnRemoveItem.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemoveItem.gridx = 1;
		gbc_btnRemoveItem.gridy = 5;
		panel_3.add(btnRemoveItem, gbc_btnRemoveItem);
		
		JButton btnAddItem = new JButton("Add Item");
		GridBagConstraints gbc_btnAddItem = new GridBagConstraints();
		gbc_btnAddItem.anchor = GridBagConstraints.WEST;
		gbc_btnAddItem.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddItem.gridx = 2;
		gbc_btnAddItem.gridy = 5;
		panel_3.add(btnAddItem, gbc_btnAddItem);
		
		JComboBox comboBox_2 = new JComboBox();
		GridBagConstraints gbc_comboBox_2 = new GridBagConstraints();
		gbc_comboBox_2.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_2.gridx = 3;
		gbc_comboBox_2.gridy = 5;
		panel_3.add(comboBox_2, gbc_comboBox_2);
		
		JLabel lblIsFieldValue = new JLabel("Is Field Value Required");
		GridBagConstraints gbc_lblIsFieldValue = new GridBagConstraints();
		gbc_lblIsFieldValue.anchor = GridBagConstraints.WEST;
		gbc_lblIsFieldValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblIsFieldValue.gridx = 2;
		gbc_lblIsFieldValue.gridy = 6;
		panel_3.add(lblIsFieldValue, gbc_lblIsFieldValue);
		
		JCheckBox checkBox_1 = new JCheckBox("");
		GridBagConstraints gbc_checkBox_1 = new GridBagConstraints();
		gbc_checkBox_1.anchor = GridBagConstraints.WEST;
		gbc_checkBox_1.gridx = 3;
		gbc_checkBox_1.gridy = 6;
		panel_3.add(checkBox_1, gbc_checkBox_1);
	}

}
