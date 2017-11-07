package tknpow22.wicketexample.app;

import java.util.List;
import java.util.Map;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import tknpow22.wicketexample.app.util.CipherUtils;
import tknpow22.wicketexample.dao.UserDao;

/**
 * アプリケーションの認証処理を定義する。
 */
public class AppAuthenticatedWebSession extends AuthenticatedWebSession {

	private String userId;
	private String username;
	private AppRoles roles;

	public AppAuthenticatedWebSession(Request request) {
		super(request);
	}

	@Override
	public boolean authenticate(String userId, String password) {

		boolean authResult = false;

		try (UserDao dao = new UserDao()) {

			Map<String, Object> user = dao.findUserById(userId);
			if (user != null) {

				String encryptedDbPassword = (String) user.get("Password");
				String decryptedPassword = CipherUtils.decrypt(encryptedDbPassword);

				if (password.equals(decryptedPassword)) {

					this.userId = userId;
					this.username = (String) user.get("Username");
					this.roles = new AppRoles();

					List<Map<String, Object>> dRoles = dao.findRolesById(userId);
					for (Map<String, Object> dRole : dRoles) {
						this.roles.add((String) dRole.get("Role"), (String) dRole.get("RoleName"));
					}
					if (!this.roles.hasRole(AppRoles.All)) {
						this.roles.add(AppRoles.All, AppRoles.AllName);
					}

					authResult = true;
				}

				decryptedPassword = null;	// 念のため
			}
		}

		return authResult;
	}

	@Override
	public Roles getRoles() {
		return roles;
	}

	@Override
	public void signOut() {
		super.signOut();
		userId = null;
		username = null;
		roles = null;
	}

	public String getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}
}
