package tknpow22.wicketexample.app.page;

import tknpow22.wicketexample.app.parts.AppPageLink;

/**
 * 個々のページのベースクラスを定義する。
 */
public class AppPageBase extends AppWebPageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new AppPageLink("app-page-link"));
	}
}
