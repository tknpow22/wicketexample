package tknpow22.wicketexample.app;

import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import tknpow22.wicketexample.app.menu.LinkItem;
import tknpow22.wicketexample.app.menu.MenuContent;
import tknpow22.wicketexample.app.menu.MenuItem;
import tknpow22.wicketexample.app.rds.Rds;
import tknpow22.wicketexample.app.rds.RdsBuilder;

/**
 * アプリケーションのプロパティ情報を読み込む
 */
public class AppProperties {

	private static final Logger logger = LoggerFactory.getLogger(ExampleApplication.class);

	private static Configuration config = new PropertiesConfiguration();

	static {
		try {
			Parameters params = new Parameters();
			FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
					PropertiesConfiguration.class).configure(params.properties().setFileName("menu.properties"));

			config = builder.getConfiguration();

		} catch (Exception ex) {
			logger.error("{}", ex);
		}
	}

	protected static enum Property {
		Menu
		;

		public <T> List<T> getList(Class<T> clazz) {
			return config.getList(clazz, this.toString());
		}
	}

	/**
	 * メニュー階層を取得する
	 *
	 * @return プロパティから取得したメニューの情報を階層化して返す
	 */
	public static Rds<MenuContent> getMenu() {

		Rds<MenuContent> root = new Rds<>();
		RdsBuilder<MenuContent> rdsBuilder = new RdsBuilder<>(root);

		List<String> menuJsons = Property.Menu.getList(String.class);

		if (menuJsons != null) {

			ObjectMapper mapper = new ObjectMapper();

			for (String menuJson : menuJsons) {

				try {

					String[][] menuItems = mapper.readValue(menuJson, String[][].class);
					// menuItems: [["menuLevel", "id", "name"] or ["menuLevel", "id", "name", "className"], ... ]

					for (String[] menuItem : menuItems) {
						// menuItem: ["menuLevel", "id", "name"] or ["menuLevel", "id", "name", "className"]

						if (menuItem.length < 3) {
							continue;
						}

						int menuLevel = getMenuLevel(menuItem[0]);
						if (menuLevel < 0) {
							continue;
						}

						String id = StringUtils.trim(menuItem[1]);
						String name = StringUtils.trim(menuItem[2]);
						String className = (3 < menuItem.length) ? StringUtils.trimToNull(menuItem[3]) : null;

						if (StringUtils.isEmpty(id) || StringUtils.isEmpty(name)) {
							continue;
						}

						if (StringUtils.isEmpty(className)) {
							rdsBuilder.add(menuLevel, new MenuItem(id, name));
						} else {
							rdsBuilder.add(menuLevel, new LinkItem(id, name, className));
						}
					}
				} catch (Exception ex) {
					logger.error("{}", ex);
				}
			}
		}

		return root;
	}

	private static int getMenuLevel(String levelString) {
		try {
			return Integer.valueOf(levelString);
		} catch (NumberFormatException ex) {
		}
		return -1;
	}
}
