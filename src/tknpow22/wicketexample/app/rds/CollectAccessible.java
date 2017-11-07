package tknpow22.wicketexample.app.rds;

public abstract class CollectAccessible<T extends IRdsContent, C extends ICollectAccessibleContext> {

	private Rds<T> rdsRoot;

	public CollectAccessible(Rds<T> rdsRoot) {
		this.rdsRoot = rdsRoot;
	}

	public Rds<T> collect(C context) {
		return collectInner(context, rdsRoot, new Rds<T>());
	}

	private Rds<T> collectChild(C context, Rds<T> rds) {
		if (rds.getItem().isLeaf()) {
			return isAccessible(context, rds.getItem()) ? rds : null;
		} else {
			Rds<T> rdsNew = null;
			if (isAddEmptyInternalNode() && isAccessible(context, rds.getItem())) {
				rdsNew = new Rds<>(rds.getItem());
			}
			return collectInner(context, rds,  rdsNew);
		}
	}

	private Rds<T> collectInner(C context, Rds<T> rdsParent, Rds<T> valueDefault) {
		Rds<T> rdsParentNew = new Rds<>(rdsParent.getItem());
		for (Rds<T> crds : rdsParent.getChildren()) {
			Rds<T> acsRds = collectChild(context, crds);
			if (acsRds != null) {
				rdsParentNew.getChildren().add(acsRds);
			}
		}

		return (rdsParentNew.getChildren().size() == 0) ? valueDefault : rdsParentNew;
	}

	protected boolean isAddEmptyInternalNode() {
		return false;
	}

	protected abstract boolean isAccessible(C context, T item);
}
