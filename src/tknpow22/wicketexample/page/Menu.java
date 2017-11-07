package tknpow22.wicketexample.page;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import tknpow22.wicketexample.app.AppAuthenticatedWebSession;
import tknpow22.wicketexample.app.AppRoles;
import tknpow22.wicketexample.app.ExampleApplication;
import tknpow22.wicketexample.app.menu.CollectAccessibleLink;
import tknpow22.wicketexample.app.menu.CollectAccessibleLinkContext;
import tknpow22.wicketexample.app.menu.CollectMenuItemContentsContext;
import tknpow22.wicketexample.app.menu.MenuContent;
import tknpow22.wicketexample.app.page.AppWebPageBase;
import tknpow22.wicketexample.app.parts.AppPageCaption;
import tknpow22.wicketexample.app.rds.CollectContents;
import tknpow22.wicketexample.app.rds.Rds;
import tknpow22.wicketexample.app.util.AppUtils;

/**
 * メニューページ
 */
@AuthorizeInstantiation({AppRoles.All})
public class Menu extends AppWebPageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();

		AppAuthenticatedWebSession session = (AppAuthenticatedWebSession) AuthenticatedWebSession.get();
		Roles roles = session.getRoles();

		add(new AppPageCaption("page-caption", "メニュー"));

		{
			// Wicket でツリー構造を作るのが面倒なため自分でタグを生成する
			ExampleApplication app = (ExampleApplication) getApplication();

			// 保持するロールで表示可能なページ階層を得る
			Rds<MenuContent> menuContents = app.getMenuContents();
			CollectAccessibleLink collectAccessibleLink = new CollectAccessibleLink(menuContents);
			CollectAccessibleLinkContext collectAccessibleLinkContext = new CollectAccessibleLinkContext(roles);
			Rds<MenuContent> accessibleMenuContents = collectAccessibleLink.collect(collectAccessibleLinkContext);

			// ページ階層から表示用のコンテンツ(HTML タグ)を作成する
			CollectContents<MenuContent, CollectMenuItemContentsContext> collectContents = new CollectContents<>(accessibleMenuContents);
			CollectMenuItemContentsContext collectMenuContentsContext = new CollectMenuItemContentsContext(this);
			String menu = collectContents.collect(collectMenuContentsContext);

			add(new Label("menu", menu).setEscapeModelStrings(false));
		}

		add(new Label("userinfo", String.format("%s (%s)", session.getUsername(), String.join(",", AppRoles.getRoleNames(roles)))));

		add(new Link<Void>("logout") {
			@Override
			public void onClick() {
				AppUtils.Logout(this);
			}
		});
	}
}
