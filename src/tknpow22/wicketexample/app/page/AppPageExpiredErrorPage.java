package tknpow22.wicketexample.app.page;

import org.apache.wicket.markup.html.link.Link;

import tknpow22.wicketexample.app.parts.AppPageCaption;
import tknpow22.wicketexample.app.util.AppUtils;

/**
 * 失効ページを定義する
 */
public class AppPageExpiredErrorPage extends AppWebPageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new AppPageCaption("page-caption", "ページが失効しました"));

		add(new Link<Void>("logout") {
			@Override
			public void onClick() {
				AppUtils.Logout(this);
			}
		});
	}
}
