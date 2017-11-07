package tknpow22.wicketexample.app.menu;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;

import tknpow22.wicketexample.app.rds.ICollectAccessibleContext;

public class CollectAccessibleLinkContext implements ICollectAccessibleContext {

	private Roles roles;

	public CollectAccessibleLinkContext(Roles roles) {
		this.roles = roles;
	}

	public Roles getRoles() {
		return roles;
	}
}