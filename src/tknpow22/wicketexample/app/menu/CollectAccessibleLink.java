package tknpow22.wicketexample.app.menu;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tknpow22.wicketexample.app.AppRoles;
import tknpow22.wicketexample.app.rds.CollectAccessible;
import tknpow22.wicketexample.app.rds.Rds;

public class CollectAccessibleLink extends CollectAccessible<MenuContent, CollectAccessibleLinkContext> {

	private static final Logger logger = LoggerFactory.getLogger(CollectAccessibleLink.class);

	public CollectAccessibleLink(Rds<MenuContent> rdsRoot) {
		super(rdsRoot);
	}

	@Override
	protected boolean isAccessible(CollectAccessibleLinkContext context, MenuContent item) {

		boolean accessible = false;

		try {

			if (item instanceof MenuItem) {
				accessible = true;
			} else if (item instanceof LinkItem) {

				LinkItem linkItem = (LinkItem) item;

				Class<?> clazz = Class.forName(linkItem.getClassName());

				if (context.getRoles().hasRole(AppRoles.Administrator)) {
					accessible = true;
				} else {
					AuthorizeInstantiation authorizeInstantiation = clazz.getAnnotation(AuthorizeInstantiation.class);
					if (authorizeInstantiation != null) {
						String[] values = authorizeInstantiation.value();
						for (String value : values) {
							if (context.getRoles().hasRole(value)) {
								accessible = true;
								break;
							}
						}
					}
				}
			}

		} catch (ClassNotFoundException ex) {
			logger.error("{}", ex);
		}

		return accessible;
	}
}