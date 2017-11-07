package tknpow22.wicketexample.app.parts;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.IRequestListener;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * jQuery ajax を素で使うためのコンポーネント
 *
 * AbstractAjaxBehavior をページ内でそのまま使用した場合、jQuery ajax からのリクエストに対し、
 * URL のマッパーが要求先を特定できず、HTTP 302 でリダイレクトした後、親ページそのものを返してしまう。
 * 原因を調べていると、AbstractAjaxBehavior#getCallbackUrl から返されるパスが適切でない気がしたため、
 * ページ配下にコンポーネントを作成し、その中から要求する形にすればうまくいった。
 *
 * 参考: http://www.davidtanzer.net/jquery_wicket
 *
 * なお、参考サイトでは wicket:child を使い、テンプレート的な使い方をしているが、
 * 本サンプルでは、Panel を拡張したコンポーネントとして作成した。
 */
public abstract class AjaxHiddenPanel extends Panel implements IRequestListener {

	private WebMarkupContainer container;
	private AjaxBejavior ajaxBejavior;

	public AjaxHiddenPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		container = new WebMarkupContainer("app-ajax-hidden-pane");	// AjaxHiddenPanel.html 上の wicket:id
		container.setMarkupId(getId());	// このコンポーネントを使用するページ上での HTML の id

		ajaxBejavior = new AjaxBejavior();

		add(container);
		add(ajaxBejavior);
	}

	@Override
	protected void onBeforeRender() {
		String callbackUrl = ajaxBejavior.getCallbackUrl().toString();
		container.add(new AttributeModifier("value",callbackUrl));
		super.onBeforeRender();
	}

	private class AjaxBejavior extends AbstractAjaxBehavior {
		@Override
		public void onRequest() {
			AjaxHiddenPanel.this.onRequest();
		}
	}
}
