package hu.akoel.hetram.gui;

import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.axis.Axis;
import hu.akoel.mgu.axis.Axis.AxisPosition;
import hu.akoel.mgu.crossline.CrossLine;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.scale.Scale;
import hu.akoel.mgu.scale.values.PixelPerCmValue;
import hu.akoel.mgu.values.ZoomRateValue;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class CanvasSettingTab extends JPanel {

	private static final long serialVersionUID = -8535907380457734338L;

	private MainPanel mainPanel;

	private MCanvas myCanvas;
	private CrossLine myCrossLine;
	private Grid myGrid;
	private Axis myAxis;
	private Scale myScale;
	
	private JRadioButton lbAxisSelector;
	private JRadioButton rbAxisSelector;
	private JRadioButton ltAxisSelector;
	private JRadioButton rtAxisSelector;
	private JRadioButton zzAxisSelector;
	
	private JComboBox<String> gridTypeCombo;
	private JComboBox<String> gridWidthCombo;
	private JComboBox<String> crossLineWidthCombo;
	
	private JTextField crossLineXPosField;
	private JTextField crossLineYPosField;
	private JTextField crossLineXLengthField;
	private JTextField crossLineYLengthField;
	
	private JTextField gridXDeltaField;
	private JTextField gridYDeltaField;
	
	private JTextField xScaleField;
	private JTextField yScaleField;
	private JTextField xPositionField;
	private JTextField yPositionField;
	
	public CanvasSettingTab(MainPanel mainPanel) {
		super();

		this.mainPanel = mainPanel;

		this.myCanvas = mainPanel.getMCanvas();
		this.myGrid = mainPanel.getGrid();
		this.myCrossLine = mainPanel.getCrossLine();
		this.myScale = mainPanel.getScale();
		this.myAxis = mainPanel.getAxis();
		
		int row = 0;

		this.setBorder(BorderFactory.createLoweredBevelBorder());
		this.setLayout(new GridBagLayout());
		GridBagConstraints canvasSettingConstraints = new GridBagConstraints();

		// ----------------------------------------------
		//
		// Canvas Setting TAB elemek letrehozasa
		//
		// ----------------------------------------------


		//--------------------------
		//
		// Grid 
		//
		//--------------------------
		//Delta X
		gridXDeltaField = new JTextField();
		gridXDeltaField.setColumns(8);
		gridXDeltaField.setText(String.valueOf(myGrid.getDeltaGridX()));
		gridXDeltaField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf(myGrid.getDeltaGridX());

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				myGrid.setDeltaGridX(Double.valueOf(goodValue));
				myCanvas.revalidateAndRepaintCoreCanvas();
				return true;
			}
		});

		//Delta Y
		gridYDeltaField = new JTextField();
		gridYDeltaField.setColumns(8);
		gridYDeltaField.setText(String.valueOf(myGrid.getDeltaGridY()));
		gridYDeltaField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf(myGrid.getDeltaGridY());

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				myGrid.setDeltaGridY(Double.valueOf(goodValue));
				myCanvas.revalidateAndRepaintCoreCanvas();
				return true;
			}
		});

		//Width
		String[] gridWidthElements = { "1", "3" };
		gridWidthCombo = new JComboBox<String>(gridWidthElements);
		gridWidthCombo.setSelectedIndex(0);
		gridWidthCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JComboBox<String> jcmbType = (JComboBox<String>) e.getSource();
				String cmbType = (String) jcmbType.getSelectedItem();

				if (cmbType.equals("1")) {
					myGrid.setWidthInPixel(1);
				} else if (cmbType.equals("3")) {
					myGrid.setWidthInPixel(3);
				}
				myCanvas.revalidateAndRepaintCoreCanvas();
			}
		});

		//Type
		String[] gridTypeElements = { "Solid", "Dashed", "Cross", "Dot" };
		gridTypeCombo = new JComboBox<String>(gridTypeElements);
		gridTypeCombo.setSelectedIndex(3);
		gridTypeCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JComboBox<String> jcmbType = (JComboBox<String>) e.getSource();
				String cmbType = (String) jcmbType.getSelectedItem();

				if (cmbType.equals("Solid")) {
					myGrid.setType(Grid.Type.SOLID);
				} else if (cmbType.equals("Dashed")) {
					myGrid.setType(Grid.Type.DASHED);
				} else if (cmbType.equals("Cross")) {
					myGrid.setType(Grid.Type.CROSS);
				} else if (cmbType.equals("Dot")) {
					myGrid.setType(Grid.Type.DOT);
				}
				myCanvas.revalidateAndRepaintCoreCanvas();
			}
		});

		//Turn On/Off
		JCheckBox turnOnGrid = new JCheckBox("Turn On Grid");		
		turnOnGrid.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					myGrid.turnOff();
					gridTypeCombo.setEnabled(false);
					gridWidthCombo.setEnabled(false);
				} else {
					myGrid.turnOn();
					gridTypeCombo.setEnabled(true);
					gridWidthCombo.setEnabled(true);
				}
				myCanvas.repaint();
			}
		});
		turnOnGrid.setSelected( !mainPanel.isNeedDrawGrid() );
		turnOnGrid.setSelected( mainPanel.isNeedDrawGrid() );
		
		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridBagLayout());
		gridPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black), "Grid", TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints gridPanelConstraints = new GridBagConstraints();

		// 1. sor - Turn on grid
		row = 0;
		gridPanelConstraints.gridx = 0;
		gridPanelConstraints.gridy = row;
		gridPanelConstraints.gridwidth = 4;
		gridPanelConstraints.anchor = GridBagConstraints.WEST;
		gridPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridPanelConstraints.weightx = 1;
		gridPanel.add(turnOnGrid, gridPanelConstraints);

		// 2. sor - Type
		row++;
		gridPanelConstraints.gridx = 0;
		gridPanelConstraints.gridy = row;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel("     "), gridPanelConstraints);

		gridPanelConstraints.gridx = 1;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel("Type: "), gridPanelConstraints);

		gridPanelConstraints.gridx = 2;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 1;
		gridPanel.add(gridTypeCombo, gridPanelConstraints);

		// 3. sor - Width
		row++;
		gridPanelConstraints.gridx = 1;
		gridPanelConstraints.gridy = row;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel("Width: "), gridPanelConstraints);

		gridPanelConstraints.gridx = 2;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 1;
		gridPanel.add(gridWidthCombo, gridPanelConstraints);

		gridPanelConstraints.gridx = 3;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel(" px"), gridPanelConstraints);

		// 4. sor - Grid delta x
		row++;
		gridPanelConstraints.gridx = 1;
		gridPanelConstraints.gridy = row;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel("Delta X: "), gridPanelConstraints);

		gridPanelConstraints.gridx = 2;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 1;
		gridPanel.add(gridXDeltaField, gridPanelConstraints);

		gridPanelConstraints.gridx = 3;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel(" " + myScale.getUnitX().getSign()),	gridPanelConstraints);

		// 5. sor - Grid delta y
		row++;
		gridPanelConstraints.gridx = 1;
		gridPanelConstraints.gridy = row;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel("Delta Y: "), gridPanelConstraints);

		gridPanelConstraints.gridx = 2;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 1;
		gridPanel.add(gridYDeltaField, gridPanelConstraints);

		gridPanelConstraints.gridx = 3;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel(" " + myScale.getUnitY().getSign()), gridPanelConstraints);

		// -------------------------
		//
		// Crossline
		//
		// -------------------------
		
		//X position
		crossLineXPosField = new JTextField();
		crossLineXPosField.setColumns(8);
		crossLineXPosField.setText(String.valueOf(myCrossLine.getPositionX()));
		crossLineXPosField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf(myCrossLine.getPositionX());

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				myCrossLine.setPositionX(Double.valueOf(goodValue));
				myCanvas.revalidateAndRepaintCoreCanvas();
				return true;
			}
		});

		//Y position
		crossLineYPosField = new JTextField();
		crossLineYPosField.setColumns(8);
		crossLineYPosField.setText(String.valueOf(myCrossLine.getPositionY()));
		crossLineYPosField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf(myCrossLine.getPositionY());

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				myCrossLine.setPositionY(Double.valueOf(goodValue));
				myCanvas.revalidateAndRepaintCoreCanvas();
				return true;
			}
		});

		//X length
		crossLineXLengthField = new JTextField();
		crossLineXLengthField.setColumns(8);
		crossLineXLengthField.setText(String.valueOf(myCrossLine.getLengthX()));
		crossLineXLengthField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf(myCrossLine.getLengthX());

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				myCrossLine.setLengthX(Double.valueOf(goodValue));
				myCanvas.revalidateAndRepaintCoreCanvas();
				return true;
			}
		});

		//Y length
		crossLineYLengthField = new JTextField();
		crossLineYLengthField.setColumns(8);
		crossLineYLengthField.setText(String.valueOf(myCrossLine.getLengthY()));
		crossLineYLengthField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf(myCrossLine.getLengthY());

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				myCrossLine.setLengthY(Double.valueOf(goodValue));
				myCanvas.revalidateAndRepaintCoreCanvas();
				return true;
			}
		});

		//Width
		String[] crossLineWidthElements = { "1", "3", "5" };
		crossLineWidthCombo = new JComboBox<String>(crossLineWidthElements);
		crossLineWidthCombo.setSelectedIndex(2);
		crossLineWidthCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JComboBox<String> jcmbType = (JComboBox<String>) e.getSource();
				String cmbType = (String) jcmbType.getSelectedItem();

				if (cmbType.equals("1")) {
					myCrossLine.setWidthInPixel(1);
				} else if (cmbType.equals("3")) {
					myCrossLine.setWidthInPixel(3);
				} else if (cmbType.equals("5")) {
					myCrossLine.setWidthInPixel(5);
				}
				myCanvas.revalidateAndRepaintCoreCanvas();
			}
		});

		//Turn On/Off
		JCheckBox turnOnCrossLine = new JCheckBox("Turn On Crossline");		
		turnOnCrossLine.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					myCrossLine.turnOff();
					crossLineWidthCombo.setEnabled(false);
					crossLineXPosField.setEnabled(false);
					crossLineYPosField.setEnabled(false);
					crossLineXLengthField.setEnabled(false);
					crossLineYLengthField.setEnabled(false);
				} else {
					myCrossLine.turnOn();
					crossLineWidthCombo.setEnabled(true);
					crossLineXPosField.setEnabled(true);
					crossLineYPosField.setEnabled(true);
					crossLineXLengthField.setEnabled(true);
					crossLineYLengthField.setEnabled(true);
				}
				myCanvas.repaint();
			}
		});
		turnOnCrossLine.setSelected(!mainPanel.isNeedDrawCrossLine());
		turnOnCrossLine.setSelected(mainPanel.isNeedDrawCrossLine());
		
		JPanel crossLinePanel = new JPanel();
		crossLinePanel.setLayout(new GridBagLayout());
		crossLinePanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black), "Cross line", TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints crossLinePanelConstraints = new GridBagConstraints();

		// 1. sor - Turn on CrossLine
		row = 0;
		crossLinePanelConstraints.gridx = 0;
		crossLinePanelConstraints.gridy = row;
		crossLinePanelConstraints.gridwidth = 4;
		crossLinePanelConstraints.anchor = GridBagConstraints.WEST;
		crossLinePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanelConstraints.anchor = GridBagConstraints.WEST;
		crossLinePanel.add(turnOnCrossLine, crossLinePanelConstraints);

		// 2. sor - Position X
		row++;
		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridy = row;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel("Position X: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add(crossLineXPosField, crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 3;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel(" " + myScale.getUnitX().getSign()),	crossLinePanelConstraints);

		// 3. sor - Position Y
		row++;
		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridy = row;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel("Position Y: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add(crossLineYPosField, crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 3;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel(" " + myScale.getUnitY().getSign()), crossLinePanelConstraints);

		// 4. sor - Width
		row ++;
		crossLinePanelConstraints.gridx = 0;
		crossLinePanelConstraints.gridy = row;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel("     "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel("Width: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add(crossLineWidthCombo, crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 3;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel(" px"), crossLinePanelConstraints);

		// 5. sor - Length X
		row++;
		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridy = row;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel("Length X: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add(crossLineXLengthField, crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 3;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel(" " + myScale.getUnitX().getSign()), crossLinePanelConstraints);

		// 6. sor - Length Y
		row++;
		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridy = row;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel("Length Y: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add(crossLineYLengthField, crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 3;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel(" " + myScale.getUnitY().getSign()), crossLinePanelConstraints);

		
		
		
		
		
		
		
		
		// -------------------------
		//
		// Scale
		//
		// -------------------------
	
JTextField pixelPerCmField;		
		
		//Pixel per cm
		JLabel pixelPerCmLabel = new JLabel("1 cm = ");
		JLabel pixelPerCmUnit = new JLabel( "px" );
		pixelPerCmField = new JTextField();
		pixelPerCmField.setColumns(8);
		pixelPerCmField.setText(String.valueOf( myScale.getPixelPerCm().getX() ) );
		pixelPerCmField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf( myScale.getPixelPerCm().getX() );

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				myScale.setPixelPerCm( new PixelPerCmValue( Double.valueOf(goodValue), Double.valueOf(goodValue) ) );				
				myCanvas.revalidateAndRepaintCoreCanvas();
				return true;
			}
		});
		
JTextField zoomRateField;		
		
		//Rate
		JLabel zoomRateLabel = new JLabel("Zoom rate: ");
		zoomRateField = new JTextField();
		zoomRateField.setColumns(8);
		zoomRateField.setText(String.valueOf( myScale.getZoomRate().getX() ) );
		zoomRateField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf( myScale.getZoomRate().getX() );

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				myScale.setZoomRate( new ZoomRateValue( Double.valueOf(goodValue), Double.valueOf(goodValue) ) );				
				return true;
			}
		});
		
		JPanel scalePanel = new JPanel();
		scalePanel.setLayout(new GridBagLayout());
		scalePanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black), "Scale", TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints scaleConstraints = new GridBagConstraints();

		// 1. sor - pixel per cm
		row = 0;
		scaleConstraints.gridx = 0;
		scaleConstraints.gridy = row;
		scaleConstraints.gridwidth = 1;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scaleConstraints.fill = GridBagConstraints.HORIZONTAL;
		scaleConstraints.weightx = 0;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scalePanel.add(pixelPerCmLabel, scaleConstraints);

		scaleConstraints.gridx = 1;
		scaleConstraints.gridy = row;
		scaleConstraints.gridwidth = 1;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scaleConstraints.fill = GridBagConstraints.HORIZONTAL;
		scaleConstraints.weightx = 0;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scalePanel.add(pixelPerCmField, scaleConstraints);

		scaleConstraints.gridx = 2;
		scaleConstraints.gridy = row;
		scaleConstraints.gridwidth = 1;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scaleConstraints.fill = GridBagConstraints.HORIZONTAL;
		scaleConstraints.weightx = 0;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scalePanel.add(pixelPerCmUnit, scaleConstraints);

		// 2. sor - rate
		row++;
		scaleConstraints.gridx = 0;
		scaleConstraints.gridy = row;
		scaleConstraints.gridwidth = 1;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scaleConstraints.fill = GridBagConstraints.HORIZONTAL;
		scaleConstraints.weightx = 0;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scalePanel.add(zoomRateLabel, scaleConstraints);

		scaleConstraints.gridx = 1;
		scaleConstraints.gridy = row;
		scaleConstraints.gridwidth = 1;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scaleConstraints.fill = GridBagConstraints.HORIZONTAL;
		scaleConstraints.weightx = 0;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scalePanel.add(zoomRateField, scaleConstraints);
		
	
		
		
		
		
		// ----
		//
		// Axis
		//
		// ----
		ActionListener axisSelectorActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == lbAxisSelector) {
					myAxis.setAxisPosition(AxisPosition.AT_LEFT_BOTTOM);
				} else if (e.getSource() == rbAxisSelector) {
					myAxis.setAxisPosition(AxisPosition.AT_RIGHT_BOTTOM);
				} else if (e.getSource() == ltAxisSelector) {
					myAxis.setAxisPosition(AxisPosition.AT_LEFT_TOP);
				} else if (e.getSource() == rtAxisSelector) {
					myAxis.setAxisPosition(AxisPosition.AT_RIGHT_TOP);
				} else if (e.getSource() == zzAxisSelector) {
					myAxis.setAxisPosition(AxisPosition.AT_ZERO_ZERO);
				}
				myAxis.refresh();
			}
		};

		ButtonGroup bg = new ButtonGroup();
		lbAxisSelector = new JRadioButton("LEFT BOTTOM", true);
		bg.add(lbAxisSelector);
		lbAxisSelector.addActionListener(axisSelectorActionListener);
		rbAxisSelector = new JRadioButton("RIGHT BOTTOM");
		bg.add(rbAxisSelector);
		rbAxisSelector.addActionListener(axisSelectorActionListener);
		ltAxisSelector = new JRadioButton("LEFT TOP");
		bg.add(ltAxisSelector);
		ltAxisSelector.addActionListener(axisSelectorActionListener);
		rtAxisSelector = new JRadioButton("RIGHT TOP");
		bg.add(rtAxisSelector);
		rtAxisSelector.addActionListener(axisSelectorActionListener);
		zzAxisSelector = new JRadioButton("ZERO ZERO");
		bg.add(zzAxisSelector);
		zzAxisSelector.addActionListener(axisSelectorActionListener);

		//--------------------------
		//
		// Axis ki/be kapcsolo
		//
		//--------------------------
		
		//Turn On/Off
		JCheckBox turnOnAxis = new JCheckBox("Turn On Axis");		
		turnOnAxis.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					myAxis.turnOff();
					lbAxisSelector.setEnabled(false);
					rbAxisSelector.setEnabled(false);
					ltAxisSelector.setEnabled(false);
					rtAxisSelector.setEnabled(false);
					zzAxisSelector.setEnabled(false);
				} else {
					myAxis.turnOn();
					lbAxisSelector.setEnabled(true);
					rbAxisSelector.setEnabled(true);
					ltAxisSelector.setEnabled(true);
					rtAxisSelector.setEnabled(true);
					zzAxisSelector.setEnabled(true);
				}
				myCanvas.repaint();
			}
		});
		turnOnAxis.setSelected( !mainPanel.isNeedDrawAxis() );
		turnOnAxis.setSelected( mainPanel.isNeedDrawAxis() );
		
		JPanel axisPanel = new JPanel();
		axisPanel.setLayout(new GridBagLayout());
		axisPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black), "Axis", TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints axisPanelConstraints = new GridBagConstraints();

		row = 0;
		axisPanelConstraints.gridx = 0;
		axisPanelConstraints.gridy = row;
		axisPanelConstraints.gridwidth = 2;
		axisPanelConstraints.anchor = GridBagConstraints.WEST;
		axisPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		axisPanelConstraints.weightx = 1;
		axisPanelConstraints.anchor = GridBagConstraints.WEST;
		axisPanel.add(turnOnAxis, axisPanelConstraints);

		row++;
		axisPanelConstraints.gridx = 0;
		axisPanelConstraints.gridy = row;
		axisPanelConstraints.gridwidth = 1;
		axisPanelConstraints.weightx = 0;
		axisPanel.add(new JLabel("     "), axisPanelConstraints);

		axisPanelConstraints.gridx = 1;
		axisPanelConstraints.gridy = 1;
		axisPanelConstraints.gridwidth = 1;
		axisPanel.add(lbAxisSelector, axisPanelConstraints);

		row++;
		axisPanelConstraints.gridx = 1;
		axisPanelConstraints.gridy = row;
		axisPanelConstraints.gridwidth = 1;
		axisPanel.add(rbAxisSelector, axisPanelConstraints);

		row++;
		axisPanelConstraints.gridx = 1;
		axisPanelConstraints.gridy = row;
		axisPanelConstraints.gridwidth = 1;
		axisPanel.add(ltAxisSelector, axisPanelConstraints);

		row++;
		axisPanelConstraints.gridx = 1;
		axisPanelConstraints.gridy = row;
		axisPanelConstraints.gridwidth = 1;
		axisPanel.add(rtAxisSelector, axisPanelConstraints);

		row++;
		axisPanelConstraints.gridx = 1;
		axisPanelConstraints.gridy = row;
		axisPanelConstraints.gridwidth = 1;
		axisPanel.add(zzAxisSelector, axisPanelConstraints);

	

		//-----------------------------------
		//
		// CanvasSetting TAB feltoltese
		//
		//-----------------------------------
		
		row = 0;
		canvasSettingConstraints.gridx = 0;
		canvasSettingConstraints.gridy = row;
		canvasSettingConstraints.anchor = GridBagConstraints.NORTH;
		canvasSettingConstraints.weighty = 0;
		canvasSettingConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(gridPanel, canvasSettingConstraints);

		row++;
		canvasSettingConstraints.weighty = 0;
		canvasSettingConstraints.gridy = row;
		this.add(crossLinePanel, canvasSettingConstraints);

		row++;
		canvasSettingConstraints.weighty = 0;
		canvasSettingConstraints.gridy = row;
		this.add(scalePanel, canvasSettingConstraints);

		row++;
		canvasSettingConstraints.weighty = 0;
		canvasSettingConstraints.gridy = row;
		this.add(axisPanel, canvasSettingConstraints);

		// Azert hogy felfele legyen igazitva
		row++;
		canvasSettingConstraints.weighty = 1;
		canvasSettingConstraints.gridy = row;
		this.add(new JLabel(), canvasSettingConstraints);

	}

}
