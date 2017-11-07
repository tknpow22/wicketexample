package tknpow22.wicketexample.app.page;

import org.apache.wicket.markup.html.link.Link;

import tknpow22.wicketexample.app.parts.AppPageCaption;
import tknpow22.wicketexample.app.util.AppUtils;

/**
 * 内部エラーページを定義する
 *
 * NOTE: 本エラーページはアプリケーション設定の configuration が deployment の時だけ表示される。
 *       development の時は例外内容を表示した別の画面が使用される。
 */
public class AppInternalErrorPage extends AppWebPageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new AppPageCaption("page-caption", "内部エラー"));

		add(new Link<Void>("logout") {
			@Override
			public void onClick() {
				AppUtils.Logout(this);
			}
		});
	}
}
