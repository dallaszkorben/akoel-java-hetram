package hu.akoel.hetram.gui;

import hu.akoel.hetram.accessories.CompositeIcon;
import hu.akoel.hetram.accessories.VTextIcon;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class SettingTabbedPanel extends JTabbedPane{

	private static final long serialVersionUID = -8066765323009172519L;

	private MainPanel mainPanel;

	ElementSettingTab elementSettingTab;
	ControlSettingTab controlSettingTab;
	VisibilitySettingTab visibilitySettingTab;
	CanvasSettingTab canvasSettingTab;
	
	public SettingTabbedPanel( MainPanel mainPanel ){
		super(LEFT);
		
		this.mainPanel = mainPanel;
	
		elementSettingTab = new ElementSettingTab(mainPanel);
		VTextIcon elementSettingTabTextIcon = new VTextIcon(elementSettingTab, "Rajzi elemek", VTextIcon.ROTATE_LEFT);
		Icon elementSettingTabGraphicIcon = UIManager.getIcon("FileView.computerIcon");
		CompositeIcon elementSettingTabIcon = new CompositeIcon( elementSettingTabGraphicIcon, elementSettingTabTextIcon );

		controlSettingTab = new ControlSettingTab(mainPanel);
		VTextIcon controlSettingTabTextIcon = new VTextIcon(controlSettingTab, "Vezérlés", VTextIcon.ROTATE_LEFT);
		Icon controlSettingTabGraphicIcon = UIManager.getIcon("FileView.computerIcon");
		CompositeIcon controlSettingTabIcon = new CompositeIcon( controlSettingTabGraphicIcon, controlSettingTabTextIcon );

		visibilitySettingTab = new  VisibilitySettingTab(mainPanel);
		VTextIcon visibilitySettingTabTextIcon = new VTextIcon(visibilitySettingTab, "Megjelentítés", VTextIcon.ROTATE_LEFT);
		Icon visibilitySettingTabGraphicIcon = UIManager.getIcon("FileView.computerIcon");
		CompositeIcon visibilitySettingTabIcon = new CompositeIcon( visibilitySettingTabGraphicIcon, visibilitySettingTabTextIcon );

		canvasSettingTab = new  CanvasSettingTab(mainPanel);
		VTextIcon canvasSettingTabTextIcon = new VTextIcon(canvasSettingTab, "Rajzoló felület", VTextIcon.ROTATE_LEFT);
		Icon canvasSettingTabGraphicIcon = UIManager.getIcon("FileView.computerIcon");
		CompositeIcon canvasSettingTabIcon = new CompositeIcon( canvasSettingTabGraphicIcon, canvasSettingTabTextIcon );

		this.addTab( null, elementSettingTabIcon, elementSettingTab );
		this.addTab( null, controlSettingTabIcon, controlSettingTab );
		this.addTab( null, visibilitySettingTabIcon, visibilitySettingTab);
		this.addTab( null, canvasSettingTabIcon, canvasSettingTab);
//		this.addTab(, new CanvasSettingTab( mainPanel ) );

	}
}
