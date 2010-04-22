package altsearch;

import altrrt.World;
import altNodeTypes.EpsilonNode.UpdateScheme;

public abstract class RRTepsilonSearch extends RRTsearch {
	
	protected UpdateScheme incScheme;
	protected double incFactor;
	protected UpdateScheme decScheme;
	protected double decFactor;
	
	public RRTepsilonSearch(World world, int pGoal, int baseLength,
			UpdateScheme incScheme, double incFactor, UpdateScheme decScheme, double decFactor) {
		super(world, pGoal, baseLength);
		this.incScheme = incScheme;
		this.incFactor = incFactor;
		this.decScheme = decScheme;
		this.decFactor = decFactor;
	}
}
