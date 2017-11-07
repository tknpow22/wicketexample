package tknpow22.wicketexample.app.page;

import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * アプリケーションのページのベースクラスを定義する。
 */
public class AppWebPageBase extends WebPage {

	protected static final Logger logger = LoggerFactory.getLogger(AppWebPageBase.class);

	protected boolean checkSignedIn = true;
	protected boolean wicketAjax = false;

	@Override
	protected void onConfigure() {
		super.onConfigure();
		if (checkSignedIn) {
			if(!AuthenticatedWebSession.get().isSignedIn()) {
				AuthenticatedWebApplication app = (AuthenticatedWebApplication) AuthenticatedWebApplication.get();
				app.restartResponseAtSignInPage();
			}
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		// 共通のタイトルを設定する。
		add(new Label("app-title", "Wicket サンプル"));

		// JavaScript を body の末尾に出力するための準備を行う。
		add(new HeaderResponseContainer("app-script", "footer-app-script"));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		// ヘッダ部を出力する。
		////response.render(StringHeaderItem.forString("<title>Wicket サンプル</title>"));
		response.render(CssHeaderItem.forUrl("css/bootstrap.min.css"));
		response.render(CssHeaderItem.forUrl("css/example.css"));

		// body 末尾の JavaScript を出力する。
		if (!wicketAjax) {
			response.render(JavaScriptHeaderItem.forUrl("js/jquery-3.2.1.min.js"));
		}
		response.render(JavaScriptHeaderItem.forUrl("js/popper.min.js"));
		response.render(JavaScriptHeaderItem.forUrl("js/bootstrap.min.js"));
	}
}
