package tknpow22.wicketexample.app.menu;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tknpow22.wicketexample.app.rds.ICollectContentsContext;
import tknpow22.wicketexample.app.util.AppUtils;

public class LinkItem extends MenuContent {

	private static final Logger logger = LoggerFactory.getLogger(LinkItem.class);

	private String className;

	public LinkItem(String id, String name, String className) {
		super(id, name, true);
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public <C extends ICollectContentsContext> String getContent(C context, int level, String childContents) {

		CollectMenuItemContentsContext menuContext = (CollectMenuItemContentsContext) context;

		String path = "";
		try {
			Class<?> clazz = Class.forName(className);
			if (Page.class.isAssignableFrom(clazz)) {
				path = menuContext.getComponent().urlFor(AppUtils.castAsPage(clazz), new PageParameters()).toString();
			}
		} catch (ClassNotFoundException ex) {
			logger.error("{}", ex);
		}

		return String.format("<li><a href=\"%s\">%s</a></li>", path, StringEscapeUtils.escapeHtml4(name));
	}
}