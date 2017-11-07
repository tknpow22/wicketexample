package tknpow22.wicketexample.app.menu;

import tknpow22.wicketexample.app.rds.IRdsContent;

public abstract class MenuContent implements IRdsContent {

	protected String id;
	protected String name;
	protected boolean page;

	protected MenuContent(String id, String name, boolean page) {
		this.id = id;
		this.name = name;
		this.page = page;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isLeaf() {
		return page;
	}

	@Override
	public String toString() {
		return String.format("name:%s|page:%s", name, page);
	}
}