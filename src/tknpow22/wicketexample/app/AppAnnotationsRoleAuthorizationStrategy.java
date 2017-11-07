package tknpow22.wicketexample.app;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;

/**
 * アプリケーションのアノテーションによる認証処理を変更する
 */
public class AppAnnotationsRoleAuthorizationStrategy extends AnnotationsRoleAuthorizationStrategy {

	public AppAnnotationsRoleAuthorizationStrategy(IRoleCheckingStrategy roleCheckingStrategy) {
		super(roleCheckingStrategy);
	}

	@Override
	public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass) {
		boolean authorized = super.isInstantiationAuthorized(componentClass);

		// Administrator ロールを保持している場合、どのページにもアクセスできるようにする
		Roles roles = AuthenticatedWebSession.get().getRoles();
		if (roles != null) {
			if (roles.hasRole(AppRoles.Administrator)) {
				authorized = true;
			}
		}

		return authorized;
	}

}
