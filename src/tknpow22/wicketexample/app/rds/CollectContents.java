package tknpow22.wicketexample.app.rds;

public class CollectContents<T extends IRdsContent, C extends ICollectContentsContext> {

	private Rds<T> rdsRoot;

	public CollectContents(Rds<T> rdsRoot) {
		this.rdsRoot = rdsRoot;
	}

	public String collect(C context) {
		return collectInnter(context, -1, rdsRoot);
	}

	private String collectChild(C context, int level, Rds<T> rds) {
		String childContents = (rds.getItem().isLeaf()) ? "" : collectInnter(context, level, rds);
		return rds.getItem().getContent(context, level, childContents);
	}

	private String collectInnter(C context, int level, Rds<T> rds) {
		StringBuffer contents = new StringBuffer();

		for (Rds<T> crds : rds.getChildren()) {
			contents.append(collectChild(context, level + 1, crds));
		}

		return contents.toString();
	}
}
