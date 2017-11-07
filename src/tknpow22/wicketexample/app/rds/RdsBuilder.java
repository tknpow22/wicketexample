package tknpow22.wicketexample.app.rds;

import java.util.List;

public class RdsBuilder<T extends IRdsContent> {

	private Rds<T> rdsRoot;

	public RdsBuilder(Rds<T> rdsRoot) {
		this.rdsRoot = rdsRoot;
	}

	public void add(int level, T item) {
		assert 0 <= level;
		add(rdsRoot, level, item);
	}

	private void add(Rds<T> rdsParent, int level,  T item) {
		if (level == 0) {
			rdsParent.getChildren().add(new Rds<T>(item));
		} else 	if (0 < level) {
			List<Rds<T>> children = rdsParent.getChildren();
			if (children.size() != 0) {
				Rds<T> rdsLastChild = children.get(children.size() - 1);
				if (!rdsLastChild.getItem().isLeaf()) {
					add(rdsLastChild, --level, item);
				}
			}
		}
	}
}
