package altgui;

import altrrt.Tree;
import altrrt.World;

public interface PausingSearch {
	public Tree getSearchTree();
	public World getWorld();
	
	public void setNextStep(boolean step);
	public void setExit(boolean exit);
}
