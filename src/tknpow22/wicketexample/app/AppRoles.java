package tknpow22.wicketexample.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;

/**
 * アプリケーションで使用するユーザーのロールを定義する
 */
public class AppRoles extends Roles {

	// NOTE: ユーザーのロールとしては "Administrator", "Director", "Manager" のみを利用する。
	//
	//       - ロール "All" は Wicket のアノテーションによる認証処理の簡便化のために、
	//         認証されたユーザーに必ず追加しており、すべての認証済みユーザーがアクセスできるページには
	//         アノテーション "All" のみ指定しておけば良い。
	//
	//       - Wicket のアノテーションによる認証処理の仕組み上、
	//         ロール "Administrator" がすべてのページにアクセスできるようにするためには、
	//         すべてのページにアノテーション "Administrator" を指定する必要がある。
	//         これは面倒なので、ロールに "Administrator" を保持していれば、すべてのページにアクセスできるよう
	//         認証処理を変更している。
	//           => AppAnnotationsRoleAuthorizationStrategy#isInstantiationAuthorized()

	public static final String Administrator = "Administrator";

	public static final String Director = "Director";

	public static final String Manager = "Manager";

	// この値はデータベース等には登録しないこと
	public static final String All = "All";
	public static final String AllName = "ALL";

	/**
	 * ユーザーのロールの一覧を返す。
	 * All はユーザーのロールではなく、認証処理の簡便化のために付与したものなので、
	 * もしデータとして登録されていたとしても、返却する一覧からは除いている。
	 *
	 * @param roles Wicket の Roles オブジェクト
	 * @return ロールの一覧
	 */
	public static List<String> getRoles(Roles roles) {
		List<String> roleList = new ArrayList<String>(Arrays.asList(roles.toArray(new String[0])));
		roleList.remove(AppRoles.All);
		return roleList;
	}

	public static List<String> getRoleNames(Roles roles) {
		AppRoles appRoles = (AppRoles) roles;

		List<String> roleList = new ArrayList<String>(Arrays.asList(appRoles.roleNames.values().toArray(new String[0])));
		roleList.remove(AppRoles.AllName);
		return roleList;
	}

	// ロールの名称を保持する
	private Map<String, String> roleNames = new HashMap<>();

	public AppRoles() {
		super();
	}

	public boolean add(String roles) {
		throw new UnsupportedOperationException("This operation is not allowed");
	}

	/**
	 * ロールを追加する
	 * @param role ロール
	 * @param roleName ロール名称
	 * @return
	 */
    public boolean add(String role, String roleName) {
		boolean result = super.add(role);

		roleNames.put(role, roleName);

		return result;
	}
}
