package tknpow22.wicketexample.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.UrlResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;

import tknpow22.wicketexample.app.menu.MenuContent;
import tknpow22.wicketexample.app.page.AppAccessDeniedPage;
import tknpow22.wicketexample.app.page.AppInternalErrorPage;
import tknpow22.wicketexample.app.page.AppPageExpiredErrorPage;
import tknpow22.wicketexample.app.rds.Rds;
import tknpow22.wicketexample.app.util.AppUtils;
import tknpow22.wicketexample.app.util.CipherUtils;
import tknpow22.wicketexample.page.Login;

/**
 * アプリケーションを定義する。
 */
public class ExampleApplication extends AuthenticatedWebApplication
{
	private static final Logger logger = LoggerFactory.getLogger(ExampleApplication.class);

	private Rds<MenuContent> menuContents;

	//
	// AuthenticatedWebApplication
	//

	@Override
	public void init()
	{
		super.init();

		// アクセス禁止ページを変更する
		getApplicationSettings().setAccessDeniedPage(AppAccessDeniedPage.class);

		// 内部エラーページを変更する
		getApplicationSettings().setInternalErrorPage(AppInternalErrorPage.class);

		// 失効ページを変更する
		getApplicationSettings().setPageExpiredErrorPage(AppPageExpiredErrorPage.class);
		// NOTE: 原因を特定できていないが AuthenticatedWebApplication を使用している場合、
		//       失効ページは表示されず、AuthenticatedWebApplication#getHomePage() で返されるページへ遷移する(仕様かも?)。
		//       ソースを調査してみると、recreateBookmarkablePagesAfterExpiry フラグを false に設定することで、
		//       失効ページが有効になるようなのだが、タイミングによりうまく働かない場合もあり、
		//       こちらも原因を特定できていない。
		////getPageSettings().setRecreateBookmarkablePagesAfterExpiry(false);

		// アノテーションによる認証処理を変更する
		getSecuritySettings().setAuthorizationStrategy(new AppAnnotationsRoleAuthorizationStrategy(this));

		// 生成される HTML に wicket:id などのタグを出力しない。
		getMarkupSettings().setStripWicketTags(true);

		// Wicket の Ajax 機能を利用する際に出力される jQuery のパス指定する(このアプリケーションが使用しているものを使う)
		{
			String jQueryPath = AppUtils.getContextUrl(this, "js/jquery-3.2.1.min.js");
			getJavaScriptLibrarySettings().setJQueryReference(new UrlResourceReference(Url.parse(jQueryPath)));
		}

		// JavaScript を body の末尾に出力するための準備を行う
		setHeaderResponseDecorator(response -> {
			return new JavaScriptFilteredIntoFooterHeaderResponse(response, "footer-app-script");
		});

		// 指定のパッケージ配下の Page クラスをパスにマッピングする。
		// NOTE: このサンプルでは Page クラスはログインクラスの存在するパッケージの配下に格納する
		mountPagePackage("/", Login.class.getPackage().getName());

		// メニューの情報をプロパティファイルからロードする
		menuContents = AppProperties.getMenu();

		// テスト用データベースを作成する。
		createAppDatabase();
	}

	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return Login.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass()
	{
		return AppAuthenticatedWebSession.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return Login.class;
	}

	//
	//
	//

	public Rds<MenuContent> getMenuContents() {
		return menuContents;
	}

	//
	//
	//

	/**
	 * 指定のパッケージ配下に存在する、Page から派生したすべてのクラスを、URL パスにマッピングする。
	 *
	 * - 例えば、以下のようなパッケージ階層(各クラスは Page の派生クラス)がある場合に、
	 *   引数 parentPath に "/site/"、引数 pagePackageName に "tknpow22.wicketexample.page" を指定すると、
	 *   パッケージ "tknpow22.wicketexample.page" 配下の各クラスファイルを、
	 *   URL パス "/site/" 下にマッピングする。
	 *   ※マッピングの際に、parentPath 配下のパスはすべて toLowerCase している。
	 *
	 *     tknpow22
	 *         wicketexample
	 *             page
	 *                 main
	 *                     Menu.class => /site/main/menu
	 *                     ChangePassword.class => /site/main/changepassword
	 *                 Login.class => /site/login
	 *                 Logout.class => /site/logout
	 *
	 * - プロジェクトで使用しているライブラリに com で始まるパッケージがあり、
	 *   かつ、アプリケーションのパッケージ名が "com" である場合、
	 *   引数 pagePackageName に "com" を指定すると、プロジェクトで使用しているライブラリ内も探索されてしまい、
	 *   クラスロードで例外が発生して、アプリケーションが起動しないなどの、不具合の原因となる。
	 *   引数 pagePackageName には、必ず自身のアプリケーションのページクラスを格納したパッケージのみ指定すること。
	 *
	 * @param parentPath パッケージをマッピングする先のパス
	 * @param pagePackageName アプリケーションで使用する Page の派生クラスを格納したパッケージ名
	 */
	private void mountPagePackage(String parentPath, String pagePackageName) {

		try {

			Set<Class<?>> allClasses;
			{
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

				// pagePackageName ツリー配下のクラスをすべて取得する
				// NOTE: guava - Google Core Libraries for Java を利用
				allClasses = ClassPath.from(classLoader)
						.getTopLevelClassesRecursive(pagePackageName).stream()
						.map(classInfo -> classInfo.load())
						.collect(Collectors.toSet());
			}

			for (Class<?> clazz : allClasses) {

				String className = clazz.getName();

				if (className.startsWith(pagePackageName)
				 && pagePackageName.length() < className.length()
				 && Page.class.isAssignableFrom(clazz)) {

					String subpath = className.substring(pagePackageName.length() + 1);
					subpath = subpath.replace('.', '/');
					String path = parentPath + subpath.toLowerCase();

					mountPage(path, AppUtils.castAsPage(clazz));
					logger.info("class: {} => path: {}", clazz.getName(), path);
				}
			}

		} catch (IOException ex) {
			logger.error("{}", ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * アプリケーションで使用するデータベースを作成する(SQLite  In-Memory Databases 用)
	 */
	private void createAppDatabase() {

		// 作成するユーザーの情報
		class User {
			String userId;
			String username;
			List<String> roles;

			public User(String userId, String username, List<String> roles) {
				this.userId = userId;
				this.username = username;
				this.roles = roles;
			}
		}

		List<User> users = new ArrayList<User>() {
			{
				add(new User("admin", "アドミン太郎", new ArrayList<String>() {
					{
						add("Administrator");
					}
				}));
				add(new User("dirman", "ダーマン一郎", new ArrayList<String>() {
					{
						add("Director");
						add("Manager");
					}
				}));
				add(new User("man", "マン三郎", new ArrayList<String>() {
					{
						add("Manager");
					}
				}));
				add(new User("dir", "ダー四郎", new ArrayList<String>() {
					{
						add("Director");
					}
				}));
			}
		};

		try (Connection connection = AppDatabase.getConnection()) {

			// Users

			executeSql(connection,
					  " CREATE TABLE IF NOT EXISTS Users ("
					+ "   UserId TEXT NOT NULL"
					+ "   , Username TEXT NOT NULL"
					+ "   , Password TEXT NOT NULL"
					+ "   , PRIMARY KEY (UserId)"
					+ ")"
				);

			executeSql(connection,
					  " DELETE FROM Users"
				);

			{
				final String sql = ""
						+ " INSERT INTO Users ("
						+ "   UserId"
						+ "   , Username"
						+ "   , Password"
						+ " ) VALUES ("
						+ "   ?"
						+ "   , ?"
						+ "   , ?"
						+ " )"
						;

				try (PreparedStatement stmt = connection.prepareStatement(sql)) {

					for (User user : users) {
						int index = 0;
						stmt.clearParameters();
						stmt.setString(++index, user.userId);
						stmt.setString(++index, user.username);
						stmt.setString(++index, CipherUtils.encrypt(user.userId));
						stmt.execute();
					}
				}
			}

			// UserRoles

			executeSql(connection,
					  " CREATE TABLE IF NOT EXISTS UserRoles ("
					+ "   UserId TEXT NOT NULL"
					+ "   , Role TEXT NOT NULL"
					+ "   , PRIMARY KEY (UserId, Role)"
					+ ")"
				);

			executeSql(connection,
					  " DELETE FROM UserRoles"
				);

			{
				final String sql = ""
						+ " INSERT INTO UserRoles ("
						+ "   UserId"
						+ "   , Role"
						+ " ) VALUES ("
						+ "     ?"
						+ "   , ?"
						+ " )"
						;

				try (PreparedStatement stmt = connection.prepareStatement(sql)) {

					for (User user : users) {
						for (String role : user.roles) {
							int index = 0;
							stmt.clearParameters();
							stmt.setString(++index, user.userId);
							stmt.setString(++index, role);
							stmt.execute();
						}
					}
				}
			}

			// RoleNames

			executeSql(connection,
					  " CREATE TABLE IF NOT EXISTS RoleNames ("
					+ "   Role TEXT NOT NULL"
					+ "   , RoleName TEXT NOT NULL"
					+ "   , PRIMARY KEY (Role)"
					+ ")"
				);

			executeSql(connection,
					  " DELETE FROM RoleNames"
				);

			executeSql(connection,
					  " INSERT INTO RoleNames VALUES ('Administrator', 'ADMINISTRATOR')"
				);
			executeSql(connection,
					  " INSERT INTO RoleNames VALUES ('Director', 'DIRECTOR')"
				);
			executeSql(connection,
					  " INSERT INTO RoleNames VALUES ('Manager', 'MANAGER')"
				);

			// Employees

			executeSql(connection,
					  " CREATE TABLE IF NOT EXISTS Employees ("
					+ "   EmployeeId TEXT NOT NULL"
					+ "   , EmployeeName TEXT NOT NULL"
					+ "   , Age INTEGER NOT NULL"
					+ "   , PRIMARY KEY (EmployeeId)"
					+ ")"
				);

			executeSql(connection,
					  " DELETE FROM Employees"
				);

			executeSql(connection,
					  " INSERT INTO Employees VALUES ('E001', '山田 太郎', 32)"
				);
			executeSql(connection,
					  " INSERT INTO Employees VALUES ('E002', '山田 次郎', 42)"
				);
			executeSql(connection,
					  " INSERT INTO Employees VALUES ('E003', '山田 花子', 22)"
				);

			connection.commit();

		} catch (Exception ex) {
			logger.error("{}", ex);
			throw new RuntimeException(ex);
		}
	}

	private void executeSql(Connection connection, String sql) throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
		}
	}

	// このメソッドの機能は web.xml への定義で代替できる
	//	<filter>
	//		...
	//		<init-param>
	//			<param-name>configuration</param-name>
	//			<param-value>development</param-value>
	//		</init-param>
	//	</filter>
	//
	//	@Override
	//	public RuntimeConfigurationType getConfigurationType() {
	//		return RuntimeConfigurationType.DEPLOYMENT;
	//	}
}
