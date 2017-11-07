package tknpow22.wicketexample.app.menu;

import org.apache.commons.text.StringEscapeUtils;

import tknpow22.wicketexample.app.rds.ICollectContentsContext;

public class MenuItem extends MenuContent {

	public MenuItem(String id, String name) {
		super(id, name, false);
	}

	@Override
	public <C extends ICollectContentsContext> String getContent(C context, int level, String childContents) {
		return String.format("<li><div data-id=\"%s\">%s</div><ul id=\"%s\" class=\"menu-item\">%s</ul></li>", id, StringEscapeUtils.escapeHtml4(name), id, childContents);
	}
}