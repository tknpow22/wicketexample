package tknpow22.wicketexample.page.role.man;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import tknpow22.wicketexample.app.AppRoles;
import tknpow22.wicketexample.app.page.AppRolePageBase;

/**
 * ロールによるアクセス制限のサンプル
 */
@AuthorizeInstantiation({AppRoles.Manager})
public class Page1 extends AppRolePageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();
	}
}
