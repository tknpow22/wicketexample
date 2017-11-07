package tknpow22.wicketexample.app.menu;

import org.apache.wicket.Component;

import tknpow22.wicketexample.app.rds.ICollectContentsContext;

public class CollectMenuItemContentsContext implements ICollectContentsContext {

	private Component component;

	public CollectMenuItemContentsContext(Component component) {
		this.component = component;
	}

	public Component getComponent() {
		return component;
	}
}