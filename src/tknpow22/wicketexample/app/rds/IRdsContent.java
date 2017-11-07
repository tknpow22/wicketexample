package tknpow22.wicketexample.app.rds;

public interface IRdsContent {
	boolean isLeaf();
	<C extends ICollectContentsContext> String getContent(C context, int level, String childContents);
}
