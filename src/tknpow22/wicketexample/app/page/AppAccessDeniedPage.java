package tknpow22.wicketexample.app.page;

import org.apache.wicket.markup.html.link.Link;

import tknpow22.wicketexample.app.parts.AppPageCaption;
import tknpow22.wicketexample.app.util.AppUtils;

/**
 * アクセス禁止ページを定義する
 */
public class AppAccessDeniedPage extends AppWebPageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new AppPageCaption("page-caption", "アクセスできません"));

		add(new Link<Void>("logout") {
			@Override
			public void onClick() {
				AppUtils.Logout(this);
			}
		});
	}
}
