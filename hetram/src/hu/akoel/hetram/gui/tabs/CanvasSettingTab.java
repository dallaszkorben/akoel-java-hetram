package hu.akoel.hetram.gui.tabs;

import hu.akoel.hetram.gui.MainPanel;
import hu.akoel.mgu.MControlPanel;
import hu.akoel.mgu.axis.Axis;
import hu.akoel.mgu.crossline.CrossLine;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas;
import hu.akoel.mgu.drawnblock.DrawnBlockSnapControl;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.scale.Scale;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class CanvasSettingTab extends MControlPanel {

	private static final long serialVersionUID = -8535907380457734338L;

	private DrawnBlockCanvas myCanvas;
	private CrossLine myCrossLine;
	private Grid myGrid;
	private Axis myAxis;
	private Scale myScale;

	public CanvasSettingTab(MainPanel mainPanel) {
		super();

		//this.mainPanel = mainPanel;

		this.myCanvas = mainPanel.getCanvas();
		this.myGrid = mainPanel.getGrid();
		this.myCrossLine = mainPanel.getCrossLine();
		this.myScale = mainPanel.getScale();
		this.myAxis = mainPanel.getAxis();
		
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		this.setLayout(new GridBagLayout());

		JPanel canvasControl = myCanvas.getControl();
		JPanel gridControl = myGrid.getControl( myScale );
		JPanel crossLineControl = myCrossLine.getControl( myScale );
//		JPanel scaleControl = myScale.getControl();
		JPanel axisControl = myAxis.getControl();		
		
		this.addElement( canvasControl );
		this.addElement( gridControl );
		this.addElement( crossLineControl );
//		this.addElement( scaleControl );
		this.addElement( axisControl );
		this.addElement( (new DrawnBlockSnapControl(myCanvas, myGrid).getControl()));
	}

}
