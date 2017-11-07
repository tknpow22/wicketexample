package tknpow22.wicketexample.app.parts;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import tknpow22.wicketexample.app.util.AppUtils;
import tknpow22.wicketexample.page.Menu;

/**
 * ページで使用するリンクを定義する。
 */
public class AppPageLink extends Panel {

	public AppPageLink(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new Link<Void>("parts-gomenu") {
			@Override
			public void onClick() {
				setResponsePage(Menu.class);
			}
		});

		add(new Link<Void>("parts-logout") {
			@Override
			public void onClick() {
				AppUtils.Logout(this);
			}
		});
	}
}
