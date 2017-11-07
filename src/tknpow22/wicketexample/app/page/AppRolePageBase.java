package tknpow22.wicketexample.app.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import tknpow22.wicketexample.app.parts.AppPageCaption;

/**
 * ロールによるアクセス制限のサンプルページのベースクラス
 */
public class AppRolePageBase extends AppPageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();

		String accessRoles = "";

		AuthorizeInstantiation authorizeInstantiation = this.getClass().getAnnotation(AuthorizeInstantiation.class);
		if (authorizeInstantiation != null) {
			String[] values = authorizeInstantiation.value();

			accessRoles = String.join(",", values);
		}

		add(new AppPageCaption("page-caption", "アクセス制限の確認"));
		add(new Label("class-name", this.getClass().getName()));
		add(new Label("access-roles", accessRoles));
	}
}
